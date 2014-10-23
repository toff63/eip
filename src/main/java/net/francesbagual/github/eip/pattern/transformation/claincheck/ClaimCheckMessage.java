package net.francesbagual.github.eip.pattern.transformation.claincheck;

import java.io.Serializable;

import net.francesbagual.github.eip.pattern.transformation.claincheck.mdb.User;

public class ClaimCheckMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	public User user;
	public String greetings;
	
	public ClaimCheckMessage(User user, String greetings) {
		super();
		this.user = user;
		this.greetings = greetings;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((greetings == null) ? 0 : greetings.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ClaimCheckMessage other = (ClaimCheckMessage) obj;
		if (greetings == null) {
			if (other.greetings != null) return false;
		} else if (!greetings.equals(other.greetings)) return false;
		if (user == null) {
			if (other.user != null) return false;
		} else if (!user.equals(other.user)) return false;
		return true;
	}
	@Override
	public String toString() {
		return "Message [user=" + user + ", greetings=" + greetings + "]";
	}


	
}
