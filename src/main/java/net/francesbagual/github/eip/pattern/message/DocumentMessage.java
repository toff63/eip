package net.francesbagual.github.eip.pattern.message;

/**
 * 
 * Document message contain information that should be considered as immutable
 * to avoid concurrency issues. A request/response service will usually return a
 * Document message
 *
 */
public interface DocumentMessage extends Message {

}
