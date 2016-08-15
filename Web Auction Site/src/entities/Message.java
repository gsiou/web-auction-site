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
@NamedQuery(name="Message.findAll", query="SELECT m FROM Message m")
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idMessages")
	private int id;

	@Column(name="Is_read")
	private boolean is_read;

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