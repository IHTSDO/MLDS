package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ca.intelliware.ihtsdo.mlds.domain.LicenseFile;


@Repository
public interface LicenseFileRepository extends PagingAndSortingRepository<LicenseFile, Long>{
		
}
