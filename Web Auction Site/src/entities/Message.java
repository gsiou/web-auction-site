package entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the Message database table.
 * 
 */
@Entity
@NamedQuery(name="Message.findAll", query="SELECT m FROM Message m")
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MessagePK id;

	@Column(name="Is_read")
	private byte is_read;

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
	@JoinColumn(name="User_UserId")
	private User user1;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="User_UserId1")
	private User user2;

	public Message() {
	}

	public MessagePK getId() {
		return this.id;
	}

	public void setId(MessagePK id) {
		this.id = id;
	}

	public byte getIs_read() {
		return this.is_read;
	}

	public void setIs_read(byte is_read) {
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

	public User getUser1() {
		return this.user1;
	}

	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return this.user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}

}