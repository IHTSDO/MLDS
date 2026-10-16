
package ca.intelliware.ihtsdo.mlds.developer;

import ca.intelliware.ihtsdo.mlds.config.SecurityConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        PasswordEncoder passwordEncoder =
            SecurityConfiguration.passwordEncoder();

        String encodedPassword = passwordEncoder.encode("staff");

        System.out.println("Encoded password: " + encodedPassword);
    }
}


