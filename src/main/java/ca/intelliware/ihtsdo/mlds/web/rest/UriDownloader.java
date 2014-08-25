package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UriDownloader {

    private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    
	private final Logger log = LoggerFactory.getLogger(UriDownloader.class);

	public int download(String downloadUrl, HttpServletRequest clientRequest, HttpServletResponse clientResponse) {
		try {
			CloseableHttpClient httpClient = createHttpClient();
			try {
				HttpGet hostingRequest = new HttpGet(downloadUrl);
		    	copyClientHeadersToHostingRequest(clientRequest, hostingRequest);
		    	CloseableHttpResponse hostingResponse = httpClient.execute(hostingRequest);
		    	try {
		    		int statusCode = hostingResponse.getStatusLine().getStatusCode();
		    		if (statusCode >= 200 && statusCode < 300 || statusCode == HttpStatus.SC_NOT_MODIFIED) {
				    	copyHostingHeadersToClientResponse(hostingResponse, clientResponse);
				    	setContentDispositionIfMissing(hostingResponse, clientResponse, downloadUrl);
				    	HttpEntity hostingEntity = hostingResponse.getEntity();
				    	hostingEntity.writeTo(clientResponse.getOutputStream());
			    	} else {
			    		clientResponse.sendError(statusCode);
			    	}
		    		return statusCode;
		    	} finally {
		    		hostingResponse.close();
		    	}
			} finally {
				httpClient.close();
			}
		} catch (IOException ex) {
			throw new RuntimeException("Error while downloading file", ex);
		}
	}

	private CloseableHttpClient createHttpClient() {
		return HttpClients.createDefault();
	}

	private void setContentDispositionIfMissing(CloseableHttpResponse hostingResponse, HttpServletResponse clientResponse, String downloadUrl) {
		if (hostingResponse.getFirstHeader(HEADER_CONTENT_DISPOSITION) != null) {
			return;
		}
		try {
			URI uri = new URI(downloadUrl);
			String value = "attachment";
			String path = uri.getPath();
			if (path != null) {
				int lastIndex = path.lastIndexOf('/');
				if (lastIndex != -1) {
					path = path.substring(lastIndex+1);
				}
				value += "; filename=\""+path+"\"";
			}
			clientResponse.addHeader(HEADER_CONTENT_DISPOSITION, value);
		} catch (URISyntaxException e) {
			log.error("Failed to generate content-disposition due to error parsing uri");
		}
	}

	private void copyClientHeadersToHostingRequest(HttpServletRequest clientRequest, HttpGet hostingRequest) {
		copyClientHeaderToHostingRequest(HttpHeaders.IF_MODIFIED_SINCE, clientRequest, hostingRequest);
		copyClientHeaderToHostingRequest(HttpHeaders.IF_NONE_MATCH, clientRequest, hostingRequest);
		copyClientHeaderToHostingRequest(HttpHeaders.IF_UNMODIFIED_SINCE, clientRequest, hostingRequest);
		
	}

	private void copyClientHeaderToHostingRequest(String key,
			HttpServletRequest clientRequest, HttpGet hostingRequest) {
		String value = clientRequest.getHeader(key);
		if (value != null) {
			hostingRequest.addHeader(key, value);
		}
	}

	private void copyHostingHeadersToClientResponse(CloseableHttpResponse hostingResponse, HttpServletResponse clientResponse) {
		copyHostingHeaderToClientResponse(HttpHeaders.CACHE_CONTROL, hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse(HEADER_CONTENT_DISPOSITION, hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse(HttpHeaders.CONTENT_TYPE, hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse(HttpHeaders.CONTENT_LENGTH, hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse(HttpHeaders.DATE, hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse(HttpHeaders.ETAG, hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse(HttpHeaders.EXPIRES, hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse(HttpHeaders.LAST_MODIFIED, hostingResponse, clientResponse);
	}

	private void copyHostingHeaderToClientResponse(String key, CloseableHttpResponse hostingResponse, HttpServletResponse clientResponse) {
		Header header = hostingResponse.getFirstHeader(key);
		if (header != null) {
			clientResponse.setHeader(key, header.getValue());
		}
	}
}
