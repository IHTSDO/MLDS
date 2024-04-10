package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.StandingState;
import jakarta.persistence.EntityManager;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.schema.management.SearchSchemaManager;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class AffiliateSearchRepository {

    @Autowired
    EntityManager entityManager;

    public Page<Affiliate> findFullTextAndMember(String q, Member homeMember, StandingState standingState, boolean standingStateNot, Pageable pageable) throws InterruptedException {

        SearchSession searchSession = Search.session(entityManager);

        SearchSchemaManager schemaManager = searchSession.schemaManager();

        schemaManager.createIfMissing();
        MassIndexer indexer = searchSession.massIndexer(Affiliate.class)
            .threadsToLoadObjects(4);
        indexer.startAndWait();

        List<Affiliate> resultList = new ArrayList<>();

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

        resultList.addAll(result.hits());

        // Apply filters based on homeMember and standingState
        Iterator<Affiliate> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            Affiliate affiliate = iterator.next();
            if (homeMember != null && !homeMember.equals(affiliate.getHomeMember())) {
                iterator.remove(); // Remove if not matching homeMember
            } else if (standingState != null) {
                if (standingStateNot && affiliate.getStandingState() == StandingState.APPLYING) {
                    iterator.remove(); // Remove APPLYING if standingStateNot is true
                } else if (!standingStateNot && !affiliate.getStandingState().equals(standingState)) {
                    iterator.remove(); // Remove if not matching standingState and standingStateNot is false
                }
            }
        }

        return new PageImpl<>(resultList, pageable, resultList.size());
    }


    // Method to check if a string is numeric
    private boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }
}
