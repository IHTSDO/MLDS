package ca.intelliware.ihtsdo.mlds.registration;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class UserRegistrationService {
	@Resource
	UserRegistrationRepository userRegistrationRepository;

	public UserRegistration createRegistration(String email) {
		// FIXME MLDS-01 MB validation?
		UserRegistration userRegistration = new UserRegistration();
		userRegistration.setEmail(email);
		
		UserRegistration result = userRegistrationRepository.save(userRegistration);
		
		return result;
	}
}
