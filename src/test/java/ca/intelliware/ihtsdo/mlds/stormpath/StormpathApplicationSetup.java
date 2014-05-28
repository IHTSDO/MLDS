package ca.intelliware.ihtsdo.mlds.stormpath;

import static org.junit.Assert.*;

import org.junit.Test;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.application.CreateApplicationRequest;
import com.stormpath.sdk.application.CreateApplicationRequestBuilder;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.ApiKey;
import com.stormpath.sdk.client.ApiKeys;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.tenant.Tenant;

public class StormpathApplicationSetup {
	String applicationName = "IHTSDO-MLDS-DEV";

	Client client;

	private Tenant tenant;

	private Application application;

	public static void main(String[] args) throws Exception {
		StormpathApplicationSetup applicationSetup = new StormpathApplicationSetup();
		
		applicationSetup.init();
		applicationSetup.testConnect();
		
		applicationSetup.loadApplication();
		applicationSetup.createUserInApplication("sptest@mailinator.com","password");

		applicationSetup.authenticate("sptest@mailinator.com", "password");
		//applicationSetup.createApplication();
	}
	

	private void authenticate(String username, String password) {
		AuthenticationRequest request  = new UsernamePasswordRequest(username, password);
		AuthenticationResult authenticationResult = application.authenticateAccount(request);
		System.out.println(authenticationResult.getAccount().getGroups());
	}


	private void createUserInApplication(String username, String password) {
		Account account = client.instantiate(Account.class);
		account.setEmail(username);
		account.setUsername(username);
		account.setPassword("password");
		account.setGivenName("given");
		account.setSurname("surname");
		application.createAccount(account);
		// TODO Auto-generated method stub
		
	}


	private void loadApplication() {
		ApplicationList applications = tenant.getApplications(Applications.where(Applications.name().eqIgnoreCase(applicationName)));
		for (Application application : applications) {
			this.application = application;
			System.out.println("application " + application);
			return;
		}
		throw new RuntimeException("Failed to find application named " + applicationName);
	}


	private void init() {
		ApiKey apiKey = ApiKeys.builder()
				.setId("***REMOVED***")
				.setSecret("S35jsyb1CVUL0JVj6fPsTNTol8tp3Qq85Eusva631aQ")
				.build();
		client = Clients.builder().setApiKey(apiKey).build();
	}

	@Test
	public void testConnect() throws Exception {
		
		tenant = client.getCurrentTenant();
		
		System.out.println("Tenant "+ tenant);
	}
	
	void createApplication() {
		Application application = client.instantiate(Application.class);
		application.setName(applicationName);
		application.setDescription("MLDS application - development version");
		CreateApplicationRequest applicationRequest = Applications.newCreateRequestFor(application).createDirectoryNamed("IHTSDO-MLDS-DEV-DIRECTORY").build();
		tenant.createApplication(applicationRequest);
	}

}
