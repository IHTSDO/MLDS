package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.ReleaseFile;

public interface ReleaseFileRepository extends JpaRepository<ReleaseFile, Long> {

}
