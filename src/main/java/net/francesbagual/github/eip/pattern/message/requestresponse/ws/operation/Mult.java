package net.francesbagual.github.eip.pattern.message.requestresponse.ws.operation;

import java.math.BigDecimal;

public class Mult implements Operator {

	@Override
	public BigDecimal execute(BigDecimal a, BigDecimal b) {
		return a.multiply(b);
	}

}
