package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.User;

import java.util.List;

public class AffiliateDetailsResponseDTO {
    private User user;
    private AffiliateDetails affiliateDetails;
    private List<Affiliate> affiliate;

    // Constructors
    public AffiliateDetailsResponseDTO() {}

    public AffiliateDetailsResponseDTO(User user, AffiliateDetails affiliateDetails, List<Affiliate> affiliate) {
        this.user = user;
        this.affiliateDetails = affiliateDetails;
        this.affiliate = affiliate;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AffiliateDetails getAffiliateDetails() {
        return affiliateDetails;
    }

    public void setAffiliateDetails(AffiliateDetails affiliateDetails) {
        this.affiliateDetails = affiliateDetails;
    }

    public List<Affiliate> getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(List<Affiliate> affiliate) {
        this.affiliate = affiliate;
    }
}
