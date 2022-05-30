package ca.intelliware.ihtsdo.mlds.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

public class PageableUtil {
	static final Logger LOG = LoggerFactory.getLogger(PageableUtil.class);

	public int getStartPosition(Pageable pageable) {
		//return (pageable.getPageNumber() * pageable.getPageSize()) + pageable.getOffset(); // MLDS-967, start position increases too fast
		LOG.debug("Paging affiliate query results, offset (start position)={}", pageable.getPageNumber() * pageable.getPageSize());
		return (pageable.getPageNumber() * pageable.getPageSize());
	}
}
