package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Member;

@Service
public class AffiliateSearchRepository {
	@Resource
	EntityManager entityManager;
	
	PageableUtil pageableUtil = new PageableUtil();

	public Page<Affiliate> findFullTextAndMember(String q, Member homeMember, Pageable pageable) {
		
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
	
		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Affiliate.class).get();
		
		
		Query textQuery = buildWildcardQueryForTokens(queryBuilder, q);
		
		Query query;
		if (homeMember ==null) {
			query = textQuery;
		} else {
			Query homeMemberQuery = buildQueryMatchingHomeMember(queryBuilder, homeMember);
			
			query = queryBuilder
					.bool()
					.should(textQuery)
					.must(homeMemberQuery)
					.createQuery();
		}
		
		FullTextQuery ftQuery = fullTextEntityManager.createFullTextQuery(query, Affiliate.class);
		
		ftQuery.setFirstResult(pageableUtil.getStartPosition(pageable));
		ftQuery.setMaxResults(pageable.getPageSize());
		
		@SuppressWarnings("unchecked")
		List<Affiliate> resultList = ftQuery.getResultList();
		
		return new PageImpl<>(resultList, pageable, ftQuery.getResultSize());
	}

	Query buildQueryMatchingHomeMember(QueryBuilder queryBuilder, Member homeMember) {
		Query homeMemberQuery = queryBuilder.keyword()
				.onField("homeMember").matching(homeMember.getKey())
				.createQuery();
		return homeMemberQuery;
	}

	Query buildWildcardQueryForTokens(QueryBuilder queryBuilder, String q) {
		BooleanJunction<?> bool = queryBuilder.bool();
		
		String[] tokens = q.split("\\s+");
		
		for (String token : tokens) {
			bool.should(queryBuilder.keyword()
					.wildcard()
					.onField("ALL").matching(token+"*")
					.createQuery());
			bool.should(queryBuilder.keyword()
					.wildcard()
					.onField("address.country.commonName").matching(token+"*")
					.createQuery());
		}
		Query textQuery = bool.createQuery();
		
		return textQuery;
	}

}
