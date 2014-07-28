package net.francesbagual.github.eip.pattern.message.requestresponse.ws.operation;

import java.math.BigDecimal;

public interface Operator {

	public BigDecimal execute(BigDecimal a, BigDecimal b);
}
