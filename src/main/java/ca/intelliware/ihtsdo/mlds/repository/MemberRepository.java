package ca.intelliware.ihtsdo.mlds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.intelliware.ihtsdo.mlds.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	public Member findOneByKey(String key);
}
