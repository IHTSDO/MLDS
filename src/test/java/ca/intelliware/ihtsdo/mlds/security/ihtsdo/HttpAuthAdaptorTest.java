package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.domain.json.ObjectMapperTestBuilder;

@RunWith(MockitoJUnitRunner.class)
public class HttpAuthAdaptorTest {
	HttpAuthAdaptor httpAuthAdaptor = new HttpAuthAdaptor();
	
	@Mock HttpClient httpClient;
	BasicHttpResponse response200 = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
	BasicHttpResponse response401 = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_UNAUTHORIZED, "OK"));
	

	@Before
	public void setUp() {
		httpAuthAdaptor.httpClient = httpClient;
		httpAuthAdaptor.objectMapper = new ObjectMapperTestBuilder(null).buildObjectMapper();
		httpAuthAdaptor.setApplicationName("MLDS");
		httpAuthAdaptor.setQueryUrl("http://example.com/");
	}
	
	@Test
	public void userPermsUnmarshalsOKResponse() throws Exception {
		response200.setEntity(new StringEntity("{\"perms\":[{\"app\":\"Release\",\"role\":\"Manager\",\"member\":\"UK\"}]}", ContentType.APPLICATION_JSON));
		Mockito.stub(httpClient.execute(Mockito.any(HttpUriRequest.class))).toReturn(response200);
		
		List<CentralAuthUserPermission> userPermissions = httpAuthAdaptor.getUserPermissions("username");
		
		assertNotNull(userPermissions);
		assertThat(userPermissions, not(empty()));
	}

	@Test
	public void userDetailsUnmarshalsOKResponse() throws Exception {
		response200.setEntity(new StringEntity("{\"name\":\"Bob\",\"status\":\"ENABLED\",\"email\":\"bob@test.com\",\"givenName\":\"Bob\",\"middleName\":\"the\",\"surname\":\"Bobbin\",\"token\":\"411f228b-7e48-4449-8432-8f7416692be9\"}", ContentType.APPLICATION_JSON));
		Mockito.stub(httpClient.execute(Mockito.any(HttpUriRequest.class))).toReturn(response200);
		
		CentralAuthUserInfo userInfo = httpAuthAdaptor.getUserInfo("Bob");
		
		assertNotNull(userInfo);
		assertEquals("bob@test.com", userInfo.getEmail());
	}
	
	@Test
	public void userDetailsMapsNO_RESPONSEtoNull() throws Exception {
		response200.setEntity(new StringEntity("NO RESPONSE", ContentType.APPLICATION_JSON));
		Mockito.stub(httpClient.execute(Mockito.any(HttpUriRequest.class))).toReturn(response200);
		
		CentralAuthUserInfo userInfo = httpAuthAdaptor.getUserInfo("Bob");
		
		assertNull(userInfo);
		
	}
}
