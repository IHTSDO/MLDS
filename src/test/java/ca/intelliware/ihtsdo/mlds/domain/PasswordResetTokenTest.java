package ca.intelliware.ihtsdo.mlds.domain;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PasswordResetTokenTest {

	private User user;

	@Before
	public void setup() {
		user = new User();
		user.setUserId(1L);
	}

	@Test
	public void shouldPopulateTokenWithUserAndTokenId() {
		
		PasswordResetToken resetToken = PasswordResetToken.createFor(user);
		
		Assert.assertSame(user, resetToken.getUser());
		Assert.assertTrue(resetToken.getPasswordResetTokenId().length() > 8);
	}
	
	@Test
	public void shouldGenerateDifferentTokenIdsOnEachCall() {
		Set<String> tokens = new HashSet<String>();
		
		int attempts = 100;
		for (int i = 0; i < attempts; i++) {
			PasswordResetToken resetToken = PasswordResetToken.createFor(user);
			tokens.add(resetToken.getPasswordResetTokenId());
		}
		Assert.assertEquals(attempts, tokens.size());
	}
	
	@Test
	public void shouldGenerateEquals() {
		PasswordResetToken resetToken0 = PasswordResetToken.createFor(user);
		PasswordResetToken resetToken1 = PasswordResetToken.createFor(user);
		
		Assert.assertTrue(resetToken0.equals(resetToken0));
		Assert.assertTrue(resetToken0.hashCode() == resetToken0.hashCode());
		
		Assert.assertFalse(resetToken0.equals(resetToken1));
		Assert.assertFalse(resetToken0.equals(null));
		Assert.assertFalse(resetToken0.equals("not a reset"));
		
	}
}
