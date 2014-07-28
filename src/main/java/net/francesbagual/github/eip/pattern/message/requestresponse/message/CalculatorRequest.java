package net.francesbagual.github.eip.pattern.message.requestresponse.message;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

public class CalculatorRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	private Collection<BigDecimal> numbers;

	public CalculatorRequest(Collection<BigDecimal> numbers) {
		super();
		this.numbers = numbers;
	}

	public Collection<BigDecimal> getNumbers() {
		return numbers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numbers == null) ? 0 : numbers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CalculatorRequest other = (CalculatorRequest) obj;
		if (numbers == null) {
			if (other.numbers != null) return false;
		} else if (!numbers.equals(other.numbers)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "CalculatorRequest [numbers=" + numbers + "]";
	}

}
