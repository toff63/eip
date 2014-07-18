package net.francesbagual.github.eip.pattern.common;

import java.io.Serializable;

public abstract class EIPMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Header header;
	protected Object body;

	@Override
	public String toString() {
		return "EIPMessage [header=" + header + ", body=" + body + "]";
	}

}
