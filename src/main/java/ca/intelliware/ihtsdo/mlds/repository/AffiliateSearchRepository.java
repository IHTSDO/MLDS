package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;

@Service
public class AffiliateSearchRepository {
	@Resource
	EntityManager entityManager;

	public List<Affiliate> findAffiliatesMatching(String q) {
		
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
	
		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Affiliate.class).get();
		
		Query query = queryBuilder
				.keyword()
				.onField("ALL")
				.matching(q)
				.createQuery();
		
		FullTextQuery ftQuery = fullTextEntityManager.createFullTextQuery(query, Affiliate.class);
		
		List<Affiliate> resultList = ftQuery.getResultList();
		
		return resultList;
	}

}
