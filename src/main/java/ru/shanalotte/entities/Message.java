package ru.shanalotte.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="Message")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NamedQueries({
	@NamedQuery(name="Message.findAll", query="SELECT m FROM Message m"),
	@NamedQuery(name="Message.getSentOf", 
		query="SELECT m FROM Message m WHERE m.userFrom = :user AND m.showSent = TRUE ORDER BY m.time DESC"),
	@NamedQuery(name="Message.getReceivedOf",
		query="SELECT m FROM Message m WHERE m.userTo = :user AND m.showReceived = TRUE ORDER BY m.time DESC"),
	@NamedQuery(name="Message.countSent",
		query="SELECT COUNT(m.id) FROM Message m WHERE m.userFrom = :user AND m.showSent = TRUE"),
	@NamedQuery(name="Message.countReceived",
		query="SELECT COUNT(m.id) FROM Message m WHERE m.userTo = :user AND m.showReceived = TRUE"),
	@NamedQuery(name="Message.getUnreadOf",
	query="SELECT COUNT(m) FROM Message m WHERE m.userTo = :user AND m.showReceived = TRUE AND m.isRead = FALSE"),
})
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idMessages")
	private int id;

	@Column(name="Is_read")
	private boolean isRead;

	@Column(name="show_received")
	private boolean showReceived;

	@Column(name="show_sent")
	private boolean showSent;

	@Lob
	@Column(name="Subject")
	private String subject;

	@Lob
	@Column(name="Text")
	private String text;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="Time")
	private Date time;

	@ManyToOne
	@JoinColumn(name="User_from")
	private User userFrom;

	@ManyToOne
	@JoinColumn(name="User_to")
	private User userTo;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Message message = (Message) o;
		return id == message.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}