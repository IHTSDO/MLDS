package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;

public interface ReleasePackageRepository extends JpaRepository<ReleasePackage, Long> {

	List<ReleasePackage> findByMemberOrderByPriorityDesc(Member member);	
}
