package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.assertj.core.util.Lists;

import com.google.common.base.Charsets;
import com.google.common.io.Closeables;

public class HttpAuthAdaptor {

	private String queryUrl;

	public HttpAuthAdaptor(String url) {
		queryUrl = url;
	}

	boolean checkUsernameAndPasswordValid(String username, String password) throws IOException, IllegalStateException, ClientProtocolException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost userPassWordRequest = new HttpPost(queryUrl);
		
		List<NameValuePair> nameValuePairs = Lists.newArrayList();
		nameValuePairs.add(new BasicNameValuePair("queryName", "getUserByNameAuth"));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
	    userPassWordRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, Charsets.ISO_8859_1));
	    
		CloseableHttpResponse response = httpClient.execute(userPassWordRequest);
		
		int statusCode = response.getStatusLine().getStatusCode();
		boolean result;
		if (statusCode == 200) {
			result = true;
		} else if (statusCode == 401) {
			result = false;
		} else {
			throw new IOException("Authentication service returned unexpected value: " + statusCode);
		}
		
		Closeables.close(response, true);
		Closeables.close(httpClient, true);
		
		return result;
	}
	
}