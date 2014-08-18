package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
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

	public Page<Affiliate> findFullTextAndMember(String q, Member homeMember, Pageable pageable) {
		
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
	
		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Affiliate.class).get();
		
		Query query;
		Query textQuery = queryBuilder.keyword()
				.onField("ALL").matching(q)
				.createQuery();
		if (homeMember !=null) {
			Query homeMemberQuery = queryBuilder.keyword()
					.onField("homeMember").matching(homeMember.getKey())
					.createQuery();
			
			query = queryBuilder
					.bool()
					.should(textQuery)
					.must(homeMemberQuery)
					.createQuery();
		} else {
			query = textQuery;
		}
		
		FullTextQuery ftQuery = fullTextEntityManager.createFullTextQuery(query, Affiliate.class);
		
		// FIXME 0 based?
		ftQuery.setFirstResult((pageable.getPageNumber() * pageable.getPageSize()) + pageable.getOffset());
		ftQuery.setMaxResults(pageable.getPageSize());
		
		List<Affiliate> resultList = ftQuery.getResultList();
		
		return new PageImpl<>(resultList, pageable, ftQuery.getResultSize());
	}

}
