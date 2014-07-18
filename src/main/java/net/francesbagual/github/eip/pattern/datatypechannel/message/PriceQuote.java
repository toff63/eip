package net.francesbagual.github.eip.pattern.datatypechannel.message;

import net.francesbagual.github.eip.pattern.common.EIPMessage;

public class PriceQuote extends EIPMessage {
	private static final long serialVersionUID = 1L;

	public PriceQuote(String label) {
		this.body = label;
	}

	@Override
	public String toString() {
		return "PriceQuote [header=" + header + ", body=" + body + "]";
	}

}
