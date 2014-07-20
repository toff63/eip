package net.francesbagual.github.eip.pattern.invalidmessage.message;

import java.io.Serializable;

import net.francesbagual.github.eip.pattern.common.EIPMessage;

public class PriceQuote extends EIPMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	public PriceQuote(String label) {
		this.body = label;
	}

	@Override
	public String toString() {
		return "PriceQuote [header=" + header + ", body=" + body + "]";
	}

}
