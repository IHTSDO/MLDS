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

	public void download(String downloadUrl, HttpServletRequest clientRequest, HttpServletResponse clientResponse) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet hostingRequest = new HttpGet(downloadUrl);
	    	copyClientHeadersToHostingRequest(clientRequest, hostingRequest);
	    	CloseableHttpResponse hostingResponse = httpclient.execute(hostingRequest);
	    	try {
	    		log.info("entity url="+downloadUrl+" disp="+hostingResponse.getFirstHeader("Content-Disposition")+" type="+hostingResponse.getFirstHeader("Content-Type"));
		    	copyHostingHeadersToClientResponse(hostingResponse, clientResponse);
		    	setContentDispositionIfMissing(hostingResponse, clientResponse, downloadUrl);
		    	HttpEntity hostingEntity = hostingResponse.getEntity();
		    	log.info("entity streaming="+hostingEntity.isStreaming());
		    	hostingEntity.writeTo(clientResponse.getOutputStream());
//		    	IOUtils.copyLarge(hostingEntity.getContent(), clientResponse.getOutputStream());
	    	} finally {
	    		hostingResponse.close();
	    		log.info("entity download done");
	    	}
		} catch (IOException ex) {
			throw new RuntimeException("Error while downloading file");
		}
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
		// TODO Auto-generated method stub
		
	}

	private void copyHostingHeadersToClientResponse(CloseableHttpResponse hostingResponse, HttpServletResponse clientResponse) {
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
