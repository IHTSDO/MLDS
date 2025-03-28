package ca.intelliware.ihtsdo.mlds.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;


/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "T_AUTHORITY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Authority implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@NotNull
    @Size(min = 0, max = 50)
    @Id
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Authority authority = (Authority) o;

        if (name != null ? !name.equals(authority.name) : authority.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "name='" + name + '\'' +
                "}";
    }
}
