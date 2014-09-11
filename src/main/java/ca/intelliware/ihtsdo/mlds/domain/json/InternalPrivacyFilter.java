package ca.intelliware.ihtsdo.mlds.domain.json;

import java.util.Set;

import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.google.common.collect.Sets;

/**
 * Simple filter that excludes the listed properties unless the user is staff or admin.
 */
public class InternalPrivacyFilter extends SimpleBeanPropertyFilter {
	final Set<String> privateFields;

	/**
	 * @param privateFields the list of fields to hide from the affiliate user
	 */
	public InternalPrivacyFilter(String... privateFields) {
		this.privateFields = Sets.newHashSet(privateFields);
	}
	@Override
	protected boolean include(PropertyWriter writer) {
		
		if (new CurrentSecurityContext().isStaffOrAdmin()) {
			return true;
		}
		return !privateFields.contains(writer.getName());
	}

	@Override
	protected boolean include(BeanPropertyWriter writer) {
		return this.include((PropertyWriter)writer);
	}
}