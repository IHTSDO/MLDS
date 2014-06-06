package ca.intelliware.ihtsdo.commons.event.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

@Entity
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Event {

	@Id @GeneratedValue @Column(name="event_id")
	private Long id;
	
	@Type(type="timestamp")
	@Column(nullable=false,updatable=false)
	private Timestamp timestamp;
	
	@Column(nullable=false,length=4096)
	private String description;

	protected Event() {
	}
	protected Event(String description) {
		this.description = description;
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}
	
	public Long getId() {
		return this.id;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public String getDescription() {
		return this.description;
	}
}
