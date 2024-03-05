package ca.intelliware.ihtsdo.mlds.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;


@Entity
@Immutable
@Table(name = "VW_AFFILIATE_APPROVED_APPLICATION_COUNTRIES")
public class ViewAffiliateApplicationCountries {

    @Id
    @Column(name = "affiliate_id", updatable = false, nullable = false)
    private Long affiliateId;

//    @Fields({ @Field(name="ALL"), @Field()})
    @Column(name = "countries", updatable = false, nullable = false)
    private String countries;

}
