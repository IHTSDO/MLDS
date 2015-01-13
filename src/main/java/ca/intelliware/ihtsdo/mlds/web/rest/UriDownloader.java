package ca.intelliware.ihtsdo.mlds.web.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Resource;
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
import org.ihtsdo.otf.dao.s3.S3Client;
import org.ihtsdo.otf.dao.s3.helper.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class UriDownloader {

	@Resource
	S3Client s3Client;

	private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
	// TODO These should be owned by S3ClientHelper and configured at runtime, but don't want to break SRS
	private static final String S3_PROTOCOL = "s3://";
	private static final String S3_BASE_LOCATION = ".s3.amazonaws.com";

	private final Logger log = LoggerFactory.getLogger(UriDownloader.class);

	public int download(String downloadUrl, HttpServletRequest clientRequest, HttpServletResponse clientResponse) throws IOException {
		// Are we dealing with an HTTP request or S3?
		// Can we determine an S3 Bucket?
		S3Location s3Location = determineS3Location(downloadUrl);

		if (s3Location != null) {
			return downloadS3(s3Location, downloadUrl, clientRequest, clientResponse);
		} else {
			return downloadHTTP(downloadUrl, clientRequest, clientResponse);
		}
	}

	// TODO Can also move to S3ClientHelper, once configured variables are available there.
	S3Location determineS3Location(String url) {
		S3Location location = null;

		// Does the URL start with the S3 protocol?
		if (url.startsWith(S3_PROTOCOL)) {
			// eg s3://ire.published.release.ihtsdo/international/ bucket is up to first single slash
			int nextSlash = url.indexOf('/', S3_PROTOCOL.length());
			String bucket = url.substring(S3_PROTOCOL.length(), nextSlash);
			String filePath = url.substring(nextSlash + 1);
			location = new S3Location(bucket, filePath);

		} else if (url.contains(S3_BASE_LOCATION)) {
			// eg https://ire.published.release.ihtsdo.s3.amazonaws.com/international/SnomedCT_Release_INT_20140131.zip
			// In this case the bucket is the host name up to the base location
			// Remove optional protocol
			int protocolEnd = url.indexOf("//");
			if (protocolEnd != -1) {
				url = url.substring(protocolEnd + 2);
			}
			int baseLocationStart = url.indexOf(S3_BASE_LOCATION);
			String bucket = url.substring(0, baseLocationStart);
			String filePath = url.substring(baseLocationStart + S3_BASE_LOCATION.length() + 1);
			location = new S3Location(bucket, filePath);
		}
		return location;
	}

	public int downloadS3(S3Location s3Location, String downloadUrl, HttpServletRequest clientRequest, HttpServletResponse clientResponse)
			throws IOException {
		FileHelper s3Helper = new FileHelper(s3Location.bucket, s3Client);
		InputStream fileContents = s3Helper.getFileStream(s3Location.filePath);
		if (fileContents != null) {
			// Recover the filename from the path
			Path p = Paths.get(s3Location.filePath);
			String fileName = p.getFileName().toString();
			clientResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			StreamUtils.copy(fileContents, clientResponse.getOutputStream());
		} else {
			clientResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		return clientResponse.getStatus();
	}

	public int downloadHTTP(String downloadUrl, HttpServletRequest clientRequest, HttpServletResponse clientResponse) {
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

	class S3Location {
		String bucket;
		String filePath;

		public S3Location(String bucket, String filePath) {
			this.bucket = bucket;
			this.filePath = filePath;
		}

		public String getBucket() {
			return bucket;
		}

		public void setBucket(String bucket) {
			this.bucket = bucket;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}
	}

}
