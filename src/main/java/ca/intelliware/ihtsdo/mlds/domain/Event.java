package ca.intelliware.ihtsdo.mlds.domain;

import jakarta.persistence.*;


import java.sql.Timestamp;

@Entity
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Event extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name="event_id")
	private Long id;

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

	@Override
	protected Object getPK() {
		return id;
	}
}
