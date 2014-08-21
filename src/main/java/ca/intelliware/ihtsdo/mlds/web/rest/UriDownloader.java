package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UriDownloader {

    private final Logger log = LoggerFactory.getLogger(UriDownloader.class);

	public int download(String downloadUrl, HttpServletRequest clientRequest, HttpServletResponse clientResponse) {
		try {
			CloseableHttpClient httpClient = createHttpClient();
			try {
				HttpGet hostingRequest = new HttpGet(downloadUrl);
		    	copyClientHeadersToHostingRequest(clientRequest, hostingRequest);
		    	CloseableHttpResponse hostingResponse = httpClient.execute(hostingRequest);
		    	try {
		    		log.info("entity url="+downloadUrl+" disp="+hostingResponse.getFirstHeader("Content-Disposition")+" type="+hostingResponse.getFirstHeader("Content-Type"));
		    		int statusCode = hostingResponse.getStatusLine().getStatusCode();
		    		if (statusCode >= 200 && statusCode < 300) {
				    	copyHostingHeadersToClientResponse(hostingResponse, clientResponse);
				    	setContentDispositionIfMissing(hostingResponse, clientResponse, downloadUrl);
				    	HttpEntity hostingEntity = hostingResponse.getEntity();
				    	log.info("entity streaming="+hostingEntity.isStreaming());
				    	hostingEntity.writeTo(clientResponse.getOutputStream());
			    	} else {
			    		log.info("aborted due to hosting="+statusCode);
			    		clientResponse.sendError(statusCode);
			    	}
		    		return statusCode;
		    	} finally {
		    		hostingResponse.close();
		    		log.info("entity download done");
		    	}
			} finally {
				httpClient.close();
			}
		} catch (IOException ex) {
			throw new RuntimeException("Error while downloading file");
		}
	}

	private CloseableHttpClient createHttpClient() {
		return HttpClients.createDefault();
	}

	private void setContentDispositionIfMissing(CloseableHttpResponse hostingResponse, HttpServletResponse clientResponse, String downloadUrl) {
		if (hostingResponse.getFirstHeader("Content-Disposition") != null) {
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
			clientResponse.addHeader("Content-Disposition", value);
		} catch (URISyntaxException e) {
			log.error("Failed to generate content-disposition due to error parsing uri");
		}
	}

	private void copyClientHeadersToHostingRequest(HttpServletRequest clientRequest, HttpGet hostingRequest) {
		copyClientHeaderToHostingRequest("If-Modified-Since", clientRequest, hostingRequest);
		copyClientHeaderToHostingRequest("If-None-Match", clientRequest, hostingRequest);
		copyClientHeaderToHostingRequest("If-Unmodified-Since", clientRequest, hostingRequest);
		
	}

	private void copyClientHeaderToHostingRequest(String key,
			HttpServletRequest clientRequest, HttpGet hostingRequest) {
		String value = clientRequest.getHeader(key);
		if (value != null) {
			hostingRequest.addHeader(key, value);
		}
	}

	private void copyHostingHeadersToClientResponse(CloseableHttpResponse hostingResponse, HttpServletResponse clientResponse) {
		copyHostingHeaderToClientResponse("Cache-Control", hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse("Content-Disposition", hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse("Content-Type", hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse("Content-Length", hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse("Date", hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse("ETag", hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse("Expires", hostingResponse, clientResponse);
		copyHostingHeaderToClientResponse("Last-Modified", hostingResponse, clientResponse);
	}

	private void copyHostingHeaderToClientResponse(String key, CloseableHttpResponse hostingResponse, HttpServletResponse clientResponse) {
		Header header = hostingResponse.getFirstHeader(key);
		if (header != null) {
			clientResponse.setHeader(key, header.getValue());
		}
	}
}
