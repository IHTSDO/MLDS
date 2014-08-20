package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;

@Service
public class UriDownloader {

	public void download(String downloadUrl, HttpServletRequest clientRequest, HttpServletResponse clientResponse) {
		try {
	    	ClientHttpRequest hostingRequest = createHttpRequestFactory().createRequest(new URI(downloadUrl), HttpMethod.GET);
	    	copyClientHeadersToHostingRequest(clientRequest, hostingRequest);
	    	ClientHttpResponse hostingResponse = hostingRequest.execute();
	    	try {
		    	copyHostingHeadersToClientResponse(hostingResponse, clientResponse);
		    	IOUtils.copyLarge(hostingResponse.getBody(), clientResponse.getOutputStream());
	    	} finally {
	    		//FIXME should we really be closing at this stage?
	    		hostingResponse.close();
	    		//FIXME do we need to destroy the httpRequestFactory?
	    	}
		} catch (IOException ex) {
			throw new RuntimeException("Error while downloading file");
		} catch (URISyntaxException ex) {
			throw new RuntimeException("Error while downloading file (URI issue)");
		}
	}

	private void copyClientHeadersToHostingRequest(HttpServletRequest clientRequest, ClientHttpRequest httpRequest) {
		// TODO Auto-generated method stub
		
	}

	private void copyHostingHeadersToClientResponse(ClientHttpResponse hostingResponse, HttpServletResponse clientResponse) {
		HttpHeaders hostingHeaders = hostingResponse.getHeaders();
		
		if (hostingHeaders.getContentType() != null) {
			clientResponse.setContentType(hostingHeaders.getContentType().toString());
		}
		if (hostingHeaders.getContentLength() != -1) {
			//FIXME is there a long content length rather than int casting?
			clientResponse.setContentLength((int) hostingHeaders.getContentLength());
		}
	}

	private HttpComponentsClientHttpRequestFactory createHttpRequestFactory() {
		//FIXME how should we really be finding/configuring HttpComponentsClientHttpRequestFactory?
		return new HttpComponentsClientHttpRequestFactory();
	}

}
