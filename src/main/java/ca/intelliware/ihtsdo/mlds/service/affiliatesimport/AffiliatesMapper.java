package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.Country;
import ca.intelliware.ihtsdo.mlds.domain.Member;

/**
 * Holds the mappings from the CSV file columns to entity objects
 */
@Service
public class AffiliatesMapper {
	
	@Resource MemberValueConverter memberValueConverter;
	@Resource CountryValueConverter countryValueConverter;

	private List<FieldMapping> mappings;
	
	public AffiliatesMapper() {
	}

	public List<FieldMapping> getMappings() {
		//FIXME Do not know how to trigger "constructor" initialization with our spring configuration...
		if (mappings == null) {
			mappings = new ArrayList<FieldMapping>();
			populateMappings();
		}
		return mappings;
	}

	void populateMappings() {
		getMappings().add(createField("member", Application.class).required());
		getMappings().add(createField("importKey", Affiliate.class).required());
		getMappings().add(createField("type", AffiliateDetails.class));
		getMappings().add(createField("subType", AffiliateDetails.class));
		getMappings().add(createField("otherText", AffiliateDetails.class));
		getMappings().add(createField("firstName", AffiliateDetails.class));
		getMappings().add(createField("lastName", AffiliateDetails.class));
		getMappings().add(createField("email", AffiliateDetails.class));
		getMappings().add(createField("alternateEmail", AffiliateDetails.class));
		getMappings().add(createField("thirdEmail", AffiliateDetails.class));
		getMappings().add(createField("landlineNumber", AffiliateDetails.class));
		getMappings().add(createField("landlineExtension", AffiliateDetails.class));
		getMappings().add(createField("mobileNumber", AffiliateDetails.class));
		getMappings().add(createField("organizationType", AffiliateDetails.class));
		getMappings().add(createField("organizationTypeOther", AffiliateDetails.class));
		getMappings().add(createField("organizationName", AffiliateDetails.class));
		getMappings().add(createField("addressStreet", AffiliateDetails.class, "address.street"));
		getMappings().add(createField("addressCity", AffiliateDetails.class, "address.city"));
		getMappings().add(createField("addressPost", AffiliateDetails.class, "address.post"));
		getMappings().add(createField("addressCountry", AffiliateDetails.class, "address.country"));
		getMappings().add(createField("billingStreet", AffiliateDetails.class, "billingAddress.street"));
		getMappings().add(createField("billingCity", AffiliateDetails.class, "billingAddress.city"));
		getMappings().add(createField("billingPost", AffiliateDetails.class, "billingAddress.post"));
		getMappings().add(createField("billingCountry", AffiliateDetails.class, "billingAddress.country"));
		// FIXME MLDS-303 Should we add agreementType here?  Or default it
		
	}
	
	ValueConverter createValueConvert(Class<?> attributeClazz) {
		if (attributeClazz.isEnum()) {
			return new EnumValueConverter(attributeClazz);
		} else if (attributeClazz.equals(Member.class)) {
			return memberValueConverter;
		} else if (attributeClazz.equals(Country.class)) {
			return countryValueConverter;
		} else {
			return new ValueConverter();
		}

	}
	
	FieldMapping createField(String columnName, Class<?> rootClazz) {
		return createField(columnName, rootClazz, columnName);
	}

	FieldMapping createField(String columnName, Class<?> rootClazz, String attributePath) {
		Accessor accessor = new Accessor(rootClazz, attributePath);
		ValueConverter valueConverter = createValueConvert(accessor.getAttributeClass());
		int columnIndex = mappings.size();
		return new FieldMapping(columnIndex, columnName, rootClazz, accessor, valueConverter);
	}

}
