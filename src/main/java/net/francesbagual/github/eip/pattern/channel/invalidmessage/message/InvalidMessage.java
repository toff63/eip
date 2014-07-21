package net.francesbagual.github.eip.pattern.channel.invalidmessage.message;

import java.io.Serializable;

import net.francesbagual.github.eip.pattern.common.EIPMessage;

public class InvalidMessage extends EIPMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private String sender;

	public InvalidMessage(String sender, Object invalidMessage) {
		this.sender = sender;
		this.body = invalidMessage;
	}

	public String getSender() {
		return sender;
	}
}
