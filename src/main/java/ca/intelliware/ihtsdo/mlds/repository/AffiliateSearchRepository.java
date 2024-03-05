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

        SearchSession searchSession = Search.session( entityManager );

        SearchSchemaManager schemaManager = searchSession.schemaManager();

        schemaManager.createIfMissing();
        MassIndexer indexer = searchSession.massIndexer(Affiliate.class)
                .threadsToLoadObjects(4);

        indexer.startAndWait();

        List resultList = new ArrayList();

        if(homeMember == null && standingState == null ){
            SearchResult<Affiliate> result =Search.session(entityManager)
                    .search(Affiliate.class)
                    .where( f -> f.simpleQueryString()
                            .fields( "affiliateDetails.firstName")
//                            .fields("affiliateId")
                            .matching( q ) )
                    .fetch( 20 );
//            resultList.add(result.hits());
            resultList.addAll(result.hits());
        }
        return new PageImpl<>(resultList, pageable, resultList.size());
    }

}
