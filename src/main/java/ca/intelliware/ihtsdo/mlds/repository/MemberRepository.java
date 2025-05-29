package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	public Member findOneByKey(String key);

    @Query(value = "select * from member where member_id = :memberId",nativeQuery = true)
    Member findMemberById(Long memberId);
}
