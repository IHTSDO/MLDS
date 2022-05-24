package ca.intelliware.ihtsdo.mlds.search;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;

import java.util.Collection;

/**
 * Supports displaying search result information in the browser.
 */
public class AffiliateSearchResult {
	private Collection<Affiliate> affiliates; // affiliates found with a certain search (paged)
	private long totalResults; // total number of results for the search
	private int totalPages; // total number of pages for the search

	public Collection<Affiliate> getAffiliates() {
		return affiliates;
	}

	public void setAffiliates(Collection<Affiliate> affiliates) {
		this.affiliates = affiliates;
	}

	public long getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(long totalResults) {
		this.totalResults = totalResults;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
}
