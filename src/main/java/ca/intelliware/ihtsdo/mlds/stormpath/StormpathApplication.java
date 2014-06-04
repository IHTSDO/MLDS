package ca.intelliware.ihtsdo.mlds.stormpath;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.client.ApiKey;
import com.stormpath.sdk.client.ApiKeys;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.resource.ResourceException;

public class StormpathApplication {
	String applicationName = "IHTSDO-MLDS-DEV";
	Client client;
	private Application application = loadApplication(applicationName);
	
	private Application loadApplication(String applicationName) {
		loadApiKey();
		
		ApplicationList applications = client.getCurrentTenant().getApplications(Applications.where(Applications.name().eqIgnoreCase(applicationName)));
		for (Application application : applications) {
			return application;
		}
		throw new RuntimeException("Failed to find application named " + applicationName);
	}

	private void loadApiKey() {
		ApiKey apiKey = ApiKeys.builder().setId("***REMOVED***")
				.setSecret("S35jsyb1CVUL0JVj6fPsTNTol8tp3Qq85Eusva631aQ")
				.build();
		client = Clients.builder().setApiKey(apiKey).build();
	}

	public Account createUser(String email, String password, String givenName, String middleName, String surname) throws ResourceException {
		Account account = client.instantiate(Account.class);
		account.setEmail(email);
		account.setPassword(password);
		account.setUsername(email);
		account.setGivenName(givenName);
		account.setMiddleName(middleName);
		account.setSurname(surname);
		return application.createAccount(account);
	}
}
