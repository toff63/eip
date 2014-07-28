package net.francesbagual.github.eip.pattern.message;

/**
 * 
 * An event message inform about an event. It has a short period of life as it
 * is intended to be process online. It can contains a document or not. The main
 * difference with a Document message is the time variable. A receiver can
 * accept to loose events while a document is usually needed to be treated and
 * shouldn't be lost.
 *
 */
public interface EventMessage extends Message {

}
