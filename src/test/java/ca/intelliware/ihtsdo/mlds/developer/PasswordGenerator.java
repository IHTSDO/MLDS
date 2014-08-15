package ca.intelliware.ihtsdo.mlds.developer;

import org.springframework.security.crypto.password.PasswordEncoder;

import ca.intelliware.ihtsdo.mlds.config.SecurityConfiguration;

public class PasswordGenerator {

	public static void main(String[] args) {
		PasswordEncoder passwordEncoder = new SecurityConfiguration().passwordEncoder();
		String encodedPassword = passwordEncoder.encode("staff");
		System.out.println("Encoded password: " + encodedPassword);
	}
}
