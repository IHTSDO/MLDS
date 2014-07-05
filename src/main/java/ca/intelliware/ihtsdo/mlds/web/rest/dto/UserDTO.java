package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import java.util.List;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsagePeriod;

public class UserDTO {

    private String login;
    
    private String password;
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String langKey;

    private List<String> roles;

    private boolean emailVerified;

	private boolean applicationApproved;
	
	private boolean applicationMade;

	private CommercialUsagePeriod initialUsagePeriod;
    

	public UserDTO() {
    }

    
    public UserDTO(String login, String password, String firstName, String lastName, String email, String langKey,
                   List<String> roles, boolean emailVerified, boolean applicationMade, boolean applicationApproved, CommercialUsagePeriod submissionPeriod) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.langKey = langKey;
        this.roles = roles;
        this.emailVerified = emailVerified;
        this.applicationMade = applicationMade;
        this.applicationApproved = applicationApproved;
        this.setInitialUsagePeriod(submissionPeriod);
    }

    public boolean isApplicationMade() {
    	return applicationMade;
    }
    
    public boolean isEmailVerified() {
    	return emailVerified;
    }
    
    public boolean isApplicationApproved() {
    	return applicationApproved;
    }
    
    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getLangKey() {
        return langKey;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserDTO{");
        sb.append("login='").append(login).append('\'');
        sb.append(", password='").append(password.length()).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", langKey='").append(langKey).append('\'');
        sb.append(", roles=").append(roles);
        sb.append('}');
        return sb.toString();
    }


	public CommercialUsagePeriod getInitialUsagePeriod() {
		return initialUsagePeriod;
	}


	public void setInitialUsagePeriod(CommercialUsagePeriod initialUsagePeriod) {
		this.initialUsagePeriod = initialUsagePeriod;
	}
}
