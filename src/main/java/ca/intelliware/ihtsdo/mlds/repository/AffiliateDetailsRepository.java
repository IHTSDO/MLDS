package ca.intelliware.ihtsdo.mlds.repository;

import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AffiliateDetailsRepository extends JpaRepository<AffiliateDetails, Long> {

    @Query(value = "select * from affiliate_details where email= :email",nativeQuery = true)
    List<AffiliateDetails> getAllAffiliateDetailsByEmail(String email);

}
