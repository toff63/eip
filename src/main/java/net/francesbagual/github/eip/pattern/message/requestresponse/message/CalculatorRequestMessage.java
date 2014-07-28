package net.francesbagual.github.eip.pattern.message.requestresponse.message;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import net.francesbagual.github.eip.pattern.message.CommandMessage;

/**
 * @author toff63
 *
 */
public class CalculatorRequestMessage implements CommandMessage, Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String, Object> headers = Collections.emptyMap();
	private CalculatorRequest request;

	public CalculatorRequestMessage(Map<String, Object> headers, CalculatorRequest request) {
		super();
		this.headers = headers;
		this.request = request;
	}

	@Override
	public Map<String, Object> headers() {
		return headers;
	}

	@Override
	public Object body() {
		return this.request;
	}

}
