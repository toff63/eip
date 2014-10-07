package net.francesbagual.github.eip.pattern.transformation.envelopewrapper.message;

import java.io.Serializable;
import java.util.Map;

/**
 * @author toff63
 *
 * @param <T>
 */
public class Envelope<T extends Serializable> implements Serializable{

	private static final long serialVersionUID = 1L;

	public Map<String, String> headers;
	public T body;

	public Envelope(T body) {
		this.body = body;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Envelope other = (Envelope) obj;
		if (body == null) {
			if (other.body != null) return false;
		} else if (!body.equals(other.body)) return false;
		if (headers == null) {
			if (other.headers != null) return false;
		} else if (!headers.equals(other.headers)) return false;
		return true;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
