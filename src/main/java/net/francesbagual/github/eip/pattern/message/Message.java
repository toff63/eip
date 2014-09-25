package net.francesbagual.github.eip.pattern.message;

import java.util.Map;

/**
 * 
 * All messages should share a common type, have a header and a body.
 *
 */
public interface Message<T> {

	public Map<String, Object> headers();

	public T body();
}
