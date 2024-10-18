package ca.intelliware.ihtsdo.mlds.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * A user.
 */
@Entity
@Table(name = "user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Where(clause = "inactive_at IS NULL")
@SQLDelete(sql="UPDATE user SET inactive_at = now() WHERE user_id = ?")
public class User extends AbstractAuditingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
    @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "mlds.hibernate_sequence", allocationSize = 1)
	@Column(name="user_id")
	private Long userId;

    @NotNull
    @Size(min = 0, max = 100)
    private String login;

    @JsonIgnore
    @Size(min = 0, max = 100)
    private String password;

    @Size(min = 0, max = 50)
    @Column(name = "first_name")
    private String firstName;

    @Size(min = 0, max = 50)
    @Column(name = "last_name")
    private String lastName;

    @Email
    @Size(min = 0, max = 100)
    private String email;

    @NotNull
    private Boolean activated = false;

    @Size(min = 2, max = 5)
    @Column(name = "lang_key")
    private String langKey;

    @Size(min = 0, max = 20)
    @Column(name = "activation_key")
    private String activationKey;

    @Column(name = "accept_notifications")
    private Boolean acceptNotifications = true;

    @Column(name = "country_notifications_only")
    private Boolean countryNotificationsOnly = false;

	@JsonIgnore
	@Column(name="inactive_at")
	private
    Instant inactiveAt;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Authority> authorities;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<PersistentToken> persistentTokens;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Set<PersistentToken> getPersistentTokens() {
        return persistentTokens;
    }

    public void setPersistentTokens(Set<PersistentToken> persistentTokens) {
        this.persistentTokens = persistentTokens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (!Objects.equal(userId, user.userId)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", activated='" + activated + '\'' +
                ", langKey='" + langKey + '\'' +
                ", activationKey='" + activationKey + '\'' +
                "}";
    }

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Instant getInactiveAt() {
		return inactiveAt;
	}

	public void setInactiveAt(Instant inactiveAt) {
		this.inactiveAt = inactiveAt;
	}

	public Boolean getAcceptNotifications() {
		return acceptNotifications;
	}

	public void setAcceptNotifications(Boolean acceptNotifications) {
		this.acceptNotifications = acceptNotifications;
	}

    public Boolean getCountryNotificationsOnly() {
        return countryNotificationsOnly;
    }

    public void setCountryNotificationsOnly(Boolean countryNotificationsOnly) {
        this.countryNotificationsOnly = countryNotificationsOnly;
    }
}
