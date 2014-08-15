package ca.intelliware.ihtsdo.mlds.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.Instant;

/**
 * A ResetToken.
 */
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    private String passwordResetTokenId = UUID.randomUUID().toString();

    @ManyToOne
    private User user;
    
    private Instant created = Instant.now();

	public static PasswordResetToken createFor(User user) {
		PasswordResetToken result = new PasswordResetToken();
		result.user = user;
		return result;
	}

	public String getPasswordResetTokenId() {
		return passwordResetTokenId;
	}
	
	public User getUser() {
		return user;
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PasswordResetToken resettoken = (PasswordResetToken) o;

        if (!passwordResetTokenId.equals(resettoken.passwordResetTokenId)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
    	return passwordResetTokenId.hashCode();
    }

    @Override
    public String toString() {
    	return ToStringBuilder.reflectionToString(this);
    }
}
