package ca.intelliware.ihtsdo.mlds.service;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.intelliware.ihtsdo.mlds.domain.PasswordResetToken;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.PasswordResetTokenRepository;

@Service
@Transactional
public class PasswordResetService {
	@Resource PasswordResetTokenRepository passwordResetTokenRepository;
	@Resource UserService userService;
	
	public void resetPassword(String tokenKey, String newPassword) {
		Validate.notEmpty(tokenKey);
		
		PasswordResetToken resetToken = passwordResetTokenRepository.getOne(tokenKey);
		if (resetToken == null) {
			throw new IllegalArgumentException("No token matching " + tokenKey + " found.");
		}
		userService.changePassword(resetToken.getUser(), newPassword);
	}

	public String createTokenForUser(User user) {
		PasswordResetToken result = PasswordResetToken.createFor(user);
		passwordResetTokenRepository.save(result);
		
		return result.getPasswordResetTokenId();
	}

}
