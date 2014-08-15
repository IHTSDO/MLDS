package ca.intelliware.ihtsdo.mlds.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

abstract public class BaseEntity {

	abstract protected Object getPK();

	@Override
	public int hashCode() {
		int result = 31 + ((getPK() == null) ? 0 : getPK().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BaseEntity other = (BaseEntity) obj;
		if (getPK() == null) {
			if (other.getPK() != null) {
				return false;
			}
		} else if (!getPK().equals(other.getPK())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
