package entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the Message database table.
 * 
 */
@Embeddable
public class MessagePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int idMessages;

	@Column(name="User_UserId", insertable=false, updatable=false)
	private String user_UserId;

	@Column(name="User_UserId1", insertable=false, updatable=false)
	private String user_UserId1;

	public MessagePK() {
	}
	public int getIdMessages() {
		return this.idMessages;
	}
	public void setIdMessages(int idMessages) {
		this.idMessages = idMessages;
	}
	public String getUser_UserId() {
		return this.user_UserId;
	}
	public void setUser_UserId(String user_UserId) {
		this.user_UserId = user_UserId;
	}
	public String getUser_UserId1() {
		return this.user_UserId1;
	}
	public void setUser_UserId1(String user_UserId1) {
		this.user_UserId1 = user_UserId1;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MessagePK)) {
			return false;
		}
		MessagePK castOther = (MessagePK)other;
		return 
			(this.idMessages == castOther.idMessages)
			&& this.user_UserId.equals(castOther.user_UserId)
			&& this.user_UserId1.equals(castOther.user_UserId1);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.idMessages;
		hash = hash * prime + this.user_UserId.hashCode();
		hash = hash * prime + this.user_UserId1.hashCode();
		
		return hash;
	}
}