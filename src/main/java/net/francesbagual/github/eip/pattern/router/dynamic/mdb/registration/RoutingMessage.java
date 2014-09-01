package net.francesbagual.github.eip.pattern.router.dynamic.mdb.registration;

import java.io.Serializable;

import javax.jms.Destination;

public class RoutingMessage implements Serializable{


	private static final long serialVersionUID = 1L;
	
	public Destination destination;
	public String messagePrefix;
	public RoutingMessage(Destination destination, String messagePrefix) {
		super();
		this.destination = destination;
		this.messagePrefix = messagePrefix;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((messagePrefix == null) ? 0 : messagePrefix.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RoutingMessage other = (RoutingMessage) obj;
		if (destination == null) {
			if (other.destination != null) return false;
		} else if (!destination.equals(other.destination)) return false;
		if (messagePrefix == null) {
			if (other.messagePrefix != null) return false;
		} else if (!messagePrefix.equals(other.messagePrefix)) return false;
		return true;
	}
	@Override
	public String toString() {
		return "RoutingMessage [destination=" + destination + ", messagePrefix=" + messagePrefix + "]";
	}
	
	
}
