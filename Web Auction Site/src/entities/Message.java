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
		query="SELECT m FROM Message m WHERE m.user_from = :user AND m.show_sent = TRUE ORDER BY m.time DESC"),
	@NamedQuery(name="Message.getReceivedOf",
		query="SELECT m FROM Message m WHERE m.user_to = :user AND m.show_received = TRUE ORDER BY m.time DESC"),
	@NamedQuery(name="Message.countSent",
		query="SELECT COUNT(m.id) FROM Message m WHERE m.user_from = :user AND m.show_sent = TRUE"),
	@NamedQuery(name="Message.countReceived",
		query="SELECT COUNT(m.id) FROM Message m WHERE m.user_to = :user AND m.show_received = TRUE"),
})
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idMessages")
	private int id;

	@Column(name="Is_read")
	private boolean is_read;
	
	@Column(name="show_sent")
	private boolean show_sent;

	@Column(name="show_received")
	private boolean show_received;
	
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

	public void setId(int id) {
		this.id = id;
	}

	public boolean getIs_read() {
		return this.is_read;
	}

	public void setIs_read(boolean is_read) {
		this.is_read = is_read;
	}
	
	public boolean getShow_sent() {
		return this.show_sent;
	}
	
	public void setShow_sent(boolean show_sent) {
		this.show_sent = show_sent;
	}
	
	public boolean getShow_received() {
		return this.show_received;
	}
	
	public void setShow_received(boolean show_received) {
		this.show_received = show_received;
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

	public User getUser_from() {
		return this.user_from;
	}

	public void setUser_from(User user_from) {
		this.user_from = user_from;
	}

	public User getUser_to() {
		return this.user_to;
	}

	public void setUser_to(User user_to) {
		this.user_to = user_to;
	}

}