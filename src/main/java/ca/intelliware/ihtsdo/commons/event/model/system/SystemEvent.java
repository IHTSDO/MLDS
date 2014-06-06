package ca.intelliware.ihtsdo.commons.event.model.system;

import javax.persistence.Entity;

import org.hibernate.annotations.AccessType;

import ca.intelliware.ihtsdo.commons.event.model.Event;

/**
 * <p>An event type that represents system events.  System events could include
 * such things as a failure to communicate with another service, an application
 * programming exception, or another type of failure.
 * 
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
@Entity
public abstract class SystemEvent extends Event {

	protected SystemEvent() {
	}
	
	protected SystemEvent(String description) {
		super(description);
	}
}
