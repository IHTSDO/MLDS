package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReleasePackageRepository extends JpaRepository<ReleasePackage, Long> {

	List<ReleasePackage> findByMemberOrderByPriorityDesc(Member member);
}
