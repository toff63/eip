package net.francesbagual.github.eip.pattern.message.requestresponse.ws;

import java.math.BigDecimal;
import java.util.Collection;

import net.francesbagual.github.eip.pattern.message.requestresponse.mdb.RequestResponseProfile;
import net.francesbagual.github.eip.pattern.message.requestresponse.message.CalculatorRequest;
import net.francesbagual.github.eip.pattern.message.requestresponse.ws.operation.Add;
import net.francesbagual.github.eip.pattern.message.requestresponse.ws.operation.Mult;
import net.francesbagual.github.eip.pattern.message.requestresponse.ws.operation.Operator;

import org.apache.commons.collections4.CollectionUtils;

@RequestResponseProfile
public class CalculatorImpl implements CalculatorAPI {

	private Add add = new Add();
	private Mult mult = new Mult();

	@Override
	public BigDecimal add(CalculatorRequest request) {
		return operate(request.getNumbers(), add);
	}

	@Override
	public BigDecimal multiply(CalculatorRequest request) {
		return operate(request.getNumbers(), mult);
	}

	private BigDecimal operate(Collection<BigDecimal> numbers, Operator op) {
		if (CollectionUtils.isEmpty(numbers)) return null;

		BigDecimal result = BigDecimal.valueOf(0);
		for (BigDecimal number : numbers)
			result = op.execute(result, number);
		return result;
	}

}
