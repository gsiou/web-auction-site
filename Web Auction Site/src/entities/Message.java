package entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the Message database table.
 * 
 */
@Entity
@Table(name="Message")
@NamedQueries({
	@NamedQuery(name="Message.findAll", query="SELECT m FROM Message m"),
	@NamedQuery(name="Message.getSentOf", 
		query="SELECT m FROM Message m WHERE m.user_from = :user AND m.showSent = TRUE ORDER BY m.time DESC"),
	@NamedQuery(name="Message.getReceivedOf",
		query="SELECT m FROM Message m WHERE m.user_to = :user AND m.showReceived = TRUE ORDER BY m.time DESC"),
	@NamedQuery(name="Message.countSent",
		query="SELECT COUNT(m.id) FROM Message m WHERE m.user_from = :user AND m.showSent = TRUE"),
	@NamedQuery(name="Message.countReceived",
		query="SELECT COUNT(m.id) FROM Message m WHERE m.user_to = :user AND m.showReceived = TRUE"),
	@NamedQuery(name="Message.getUnreadOf",
	query="SELECT COUNT(m) FROM Message m WHERE m.user_to = :user AND m.showReceived = TRUE AND m.is_read = FALSE"),
})
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idMessages")
	private int id;

	@Column(name="Is_read")
	private boolean is_read;

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

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="User_from")
	private User user_from;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="User_to")
	private User user_to;

	public Message() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int idMessages) {
		this.id = idMessages;
	}

	public boolean getIs_read() {
		return this.is_read;
	}

	public void setIs_read(boolean is_read) {
		this.is_read = is_read;
	}

	public boolean getShowReceived() {
		return this.showReceived;
	}

	public void setShowReceived(boolean showReceived) {
		this.showReceived = showReceived;
	}

	public boolean getShowSent() {
		return this.showSent;
	}

	public void setShowSent(boolean showSent) {
		this.showSent = showSent;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getTime() {
		return this.time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public User getUserFrom() {
		return this.user_from;
	}

	public void setUserFrom(User user1) {
		this.user_from = user1;
	}

	public User getUserTo() {
		return this.user_to;
	}

	public void setUserTo(User user2) {
		this.user_to = user2;
	}

}