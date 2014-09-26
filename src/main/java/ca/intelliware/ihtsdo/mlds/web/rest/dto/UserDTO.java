package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsagePeriod;
import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.domain.Member;

@JsonIgnoreProperties({"confirmemail"})
public class UserDTO {

    private String login;
    
    private String password;
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String langKey;

    private List<String> roles;

	private CommercialUsagePeriod initialUsagePeriod;
	
	private Country country;

	/** The member this staff or administrative user represents */
	private Member member;
    

	public UserDTO() {
    }

    
    public UserDTO(String login, String password, String firstName, String lastName, String email, String langKey,
                   List<String> roles, CommercialUsagePeriod submissionPeriod,
                   Member staffOrAdminMember) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.langKey = langKey;
        this.roles = roles;
        this.initialUsagePeriod = submissionPeriod;
        this.member = staffOrAdminMember;
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


	public Country getCountry() {
		return country;
	}


	public void setCountry(Country country) {
		this.country = country;
	}


	public Member getMember() {
		return member;
	}
}
