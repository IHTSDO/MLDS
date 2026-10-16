package ca.intelliware.ihtsdo.mlds.domain.json;

import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CurrentSecurityContext;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.PropertyWriter;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple filter that excludes the listed properties unless the user is staff or admin.
 */
public class InternalPrivacyFilter extends SimpleBeanPropertyFilter {

    private final Set<String> privateFields;

    /**
     * @param privateFields the list of fields to hide from affiliate users
     */
    public InternalPrivacyFilter(String... privateFields) {
        this.privateFields = new HashSet<>(Arrays.asList(privateFields));
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
        return include((PropertyWriter) writer);
    }
}
