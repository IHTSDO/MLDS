package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.domain.Pageable;

public class PageableUtil {
	public int getStartPosition(Pageable pageable) {
		return (pageable.getPageNumber() * pageable.getPageSize()) + pageable.getOffset();
	}
	
}