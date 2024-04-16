package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import jakarta.persistence.EntityManager;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AffiliateSearchRepository {


    public final EntityManager entityManager;

    public AffiliateSearchRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Page<Affiliate> findFullTextAndMember(String q, Member homeMember, StandingState standingState, boolean standingStateNot, Pageable pageable) {
        SearchSession searchSession = Search.session(entityManager);
        SearchResult<Affiliate> result;

        if (isNumeric(q)) {
            long affiliateId = Long.parseLong(q);
            result = searchSession.search(Affiliate.class)
                .where(f -> f.match().field("affiliateId").matching(affiliateId))
                .fetch(pageable.getPageSize());
        } else {
            result = searchSession.search(Affiliate.class)
                .where(f -> f.bool()
                    .should(f.simpleQueryString()
                        .fields("affiliateDetails.firstName", "affiliateDetails.lastName", "affiliateDetails.email", "affiliateDetails.alternateEmail", "affiliateDetails.thirdEmail", "affiliateDetails.organizationName", "affiliateDetails.organizationType")
                        .matching(q + "*"))
                )
                .fetch(pageable.getPageSize());
        }

        List<Affiliate> resultList = result.hits();

        // Apply filters based on homeMember and standingState
        List<Affiliate> filteredList = resultList.stream()
            .filter(affiliate -> isAffiliateMatching(affiliate, homeMember, standingState, standingStateNot))
            .toList(); // Convert stream to List

        return new PageImpl<>(filteredList, pageable, filteredList.size());
    }

    private boolean isAffiliateMatching(Affiliate affiliate, Member homeMember, StandingState standingState, boolean standingStateNot) {
        if (homeMember != null && !homeMember.equals(affiliate.getHomeMember())) {
            return false; // Not matching homeMember
        }
        if (standingState != null) {
            if (standingStateNot && affiliate.getStandingState() == StandingState.APPLYING) {
                return false; // Not matching if standingStateNot is true and affiliate is applying
            }
            if (!standingStateNot && !affiliate.getStandingState().equals(standingState)) {
                return false; // Not matching if not matching standingState and standingStateNot is false
            }
        }
        return true; // Matches all conditions
    }



    // Method to check if a string is numeric
    private boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }

    public void reindex(Affiliate a) {
        SearchSession searchSession = Search.session(entityManager);
        searchSession.indexingPlan().addOrUpdate(a);
    }
}
