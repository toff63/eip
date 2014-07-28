package net.francesbagual.github.eip.pattern.message.requestresponse.ws.operation;

import java.math.BigDecimal;

public class Add implements Operator {
	public BigDecimal execute(BigDecimal a, BigDecimal b) {
		return a.add(b);
	}

}
