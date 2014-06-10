package ca.intelliware.ihtsdo.commons.event;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ca.intelliware.ihtsdo.mlds.domain.Event;

@Repository
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

}
