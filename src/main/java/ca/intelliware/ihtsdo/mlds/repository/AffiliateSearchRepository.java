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

        List resultList = new ArrayList();


        SearchResult<Affiliate> result = Search.session(entityManager)
            .search(Affiliate.class)
            .where(f -> f.simpleQueryString()
                .fields("affiliateDetails.firstName")
                .fields("affiliateDetails.lastName")
                .fields("affiliateDetails.email")
                .fields("affiliateDetails.alternateEmail")
                .fields("affiliateDetails.thirdEmail")
                .fields("affiliateDetails.organizationName")
                .fields("affiliateDetails.organizationType")
                .matching(q + "*"))
            .fetch(pageable.getPageSize());
        resultList.addAll(result.hits());

// filter homemeber
        if (homeMember != null && standingState == null) {
            for (int i = resultList.size() - 1; i >= 0; i--) {
                Affiliate affiliate = (Affiliate) resultList.get(i);
                if (affiliate.getHomeMember() == null || !affiliate.getHomeMember().equals(homeMember)) {
                    resultList.remove(i);
                }
            }
        }

// filter standingstate
        if (homeMember == null && standingState != null) {
            for (int i = resultList.size() - 1; i >= 0; i--) {
                Affiliate affiliate = (Affiliate) resultList.get(i);
                if (affiliate.getStandingState() == null || !affiliate.getStandingState().equals(standingState)) {
                    resultList.remove(i);
                }
            }
        }

//filter homemember & standingstate
        if (homeMember != null && standingState != null) {
            for (int i = resultList.size() - 1; i >= 0; i--) {
                Affiliate affiliate = (Affiliate) resultList.get(i);
                if (affiliate.getHomeMember() == null || !affiliate.getHomeMember().equals(homeMember) ||
                    affiliate.getStandingState() == null || !affiliate.getStandingState().equals(standingState)) {
                    resultList.remove(i);
                }
            }
        }

        return new PageImpl<>(resultList, pageable, resultList.size());
    }
}
