package ca.intelliware.ihtsdo.mlds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ca.intelliware.ihtsdo.mlds.domain.Licensee;

public interface LicenseeRepository extends JpaRepository<Licensee, Long> {
	List<Licensee> findByCreator(String userName);

	@Query(value="select l.* from licensee l join application a on a.application_id = l.application_id "
			+ "where lower(a.full_name) like '%' || ?1 || '%' "
			+ "or lower(a.organization_name) like  '%' || ?1 || '%' "
			+ "or lower(a.street) like  '%' || ?1 || '%'", nativeQuery=true)
	List<Licensee> findByTextQuery(String q);
}
