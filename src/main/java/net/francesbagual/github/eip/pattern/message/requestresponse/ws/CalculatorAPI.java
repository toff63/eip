package net.francesbagual.github.eip.pattern.message.requestresponse.ws;

import java.math.BigDecimal;

import net.francesbagual.github.eip.pattern.message.requestresponse.message.CalculatorRequest;

public interface CalculatorAPI {

	public BigDecimal add(CalculatorRequest request);

	public BigDecimal multiply(CalculatorRequest request);
}
