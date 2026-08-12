package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import ca.intelliware.ihtsdo.mlds.domain.Country;

public class CountryDTO {

    private String isoCode2;
    private String isoCode3;
    private String commonName;
    private boolean excludeUsage;
    private String alternateRegistrationUrl;

    private MemberDTO member;

    public CountryDTO(Country country) {
        this.isoCode2 = country.getIsoCode2();
        this.isoCode3 = country.getIsoCode3();
        this.commonName = country.getCommonName();
        this.excludeUsage = country.isExcludeUsage();
        this.alternateRegistrationUrl = country.getAlternateRegistrationUrl();

        if (country.getMember() != null) {
            this.member = new MemberDTO(country.getMember());
        }
    }

    public String getIsoCode2() {
        return isoCode2;
    }

    public String getIsoCode3() {
        return isoCode3;
    }

    public String getCommonName() {
        return commonName;
    }

    public boolean isExcludeUsage() {
        return excludeUsage;
    }

    public String getAlternateRegistrationUrl() {
        return alternateRegistrationUrl;
    }

    public MemberDTO getMember() {
        return member;
    }
}
