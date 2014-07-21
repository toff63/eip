package net.francesbagual.github.eip.pattern.channel.datatype.message;

import net.francesbagual.github.eip.pattern.common.EIPMessage;

public class Query extends EIPMessage {
	private static final long serialVersionUID = 1L;

	public Query(String query) {
		this.body = query;
	}

	@Override
	public String toString() {
		return "Query [header=" + header + ", body=" + body + "]";
	}

}
