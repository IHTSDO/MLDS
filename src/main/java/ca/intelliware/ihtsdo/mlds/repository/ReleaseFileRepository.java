package ca.intelliware.ihtsdo.mlds.repository;


import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReleaseFileRepository extends JpaRepository<ReleaseFile, Long> {

}
