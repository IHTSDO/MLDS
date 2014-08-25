package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ca.intelliware.ihtsdo.mlds.domain.File;


@Repository
public interface FileRepository extends PagingAndSortingRepository<File, Long>{
}
