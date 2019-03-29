package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import ca.intelliware.ihtsdo.mlds.domain.json.ObjectMapperTestBuilder;
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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class HttpAuthAdaptorTest {
	HttpAuthAdaptor httpAuthAdaptor = new HttpAuthAdaptor();
	
	@Mock HttpClient httpClient;
	BasicHttpResponse response200 = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));


	@Before
	public void setUp() {
		httpAuthAdaptor.httpClient = httpClient;
		httpAuthAdaptor.objectMapper = new ObjectMapperTestBuilder(null).buildObjectMapper();
		httpAuthAdaptor.setQueryUrl("http://example.com/");
	}
	
	@Test
	public void userPermsUnmarshalsOKResponse() throws Exception {
		response200.setEntity(new StringEntity("{\"perms\":[{\"app\":\"Release\",\"role\":\"Manager\",\"member\":\"UK\"}]}", ContentType.APPLICATION_JSON));
		Mockito.stub(httpClient.execute(Mockito.any(HttpUriRequest.class))).toReturn(response200);
		
		CentralAuthUserInfo userPermissions = httpAuthAdaptor.getUserAccountInfo("username", null);
		
		assertNotNull(userPermissions);
		assertThat(userPermissions.getRoles(), not(empty()));
	}

	@Test
	public void userDetailsUnmarshalsOKResponse() throws Exception {
		response200.setEntity(new StringEntity("{\"name\":\"Bob\",\"status\":\"ENABLED\",\"email\":\"bob@test.com\",\"givenName\":\"Bob\",\"middleName\":\"the\",\"surname\":\"Bobbin\",\"token\":\"411f228b-7e48-4449-8432-8f7416692be9\"}", ContentType.APPLICATION_JSON));
		Mockito.stub(httpClient.execute(Mockito.any(HttpUriRequest.class))).toReturn(response200);
		
		CentralAuthUserInfo userInfo = httpAuthAdaptor.getUserAccountInfo("Bob", null);
		
		assertNotNull(userInfo);
		assertEquals("bob@test.com", userInfo.getEmail());
	}
	
	@Test
	public void userDetailsMapsNO_RESPONSEtoNull() throws Exception {
		response200.setEntity(new StringEntity("NO RESPONSE", ContentType.APPLICATION_JSON));
		Mockito.stub(httpClient.execute(Mockito.any(HttpUriRequest.class))).toReturn(response200);
		
		CentralAuthUserInfo userInfo = httpAuthAdaptor.getUserAccountInfo("Bob", null);
		
		assertNull(userInfo);
		
	}
}
