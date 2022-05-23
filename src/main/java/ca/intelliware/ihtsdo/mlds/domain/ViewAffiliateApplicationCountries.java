package ca.intelliware.ihtsdo.mlds.domain;

import org.hibernate.annotations.Immutable;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Immutable
@Table(name = "VW_AFFILIATE_APPROVED_APPLICATION_COUNTRIES")
public class ViewAffiliateApplicationCountries  {

    @Id
    @Column(name = "affiliate_id", updatable = false, nullable = false)
    private Long affiliateId;

    @Fields({ @Field(name="ALL"), @Field()})
    @Column(name = "countries", updatable = false, nullable = false)
    private String countries;

}
