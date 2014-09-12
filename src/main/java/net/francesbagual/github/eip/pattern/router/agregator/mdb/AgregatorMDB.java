package net.francesbagual.github.eip.pattern.router.agregator.mdb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;

import com.google.common.base.Joiner;

/**
 * This is a dummy agregator that could just blow up the jvm heap. To be avoid
 * that, should have an EJB timer that frequently check if there is no sequence
 * of message that have missing message AND we should wait for anymore message
 * for it (time out)
 * 
 * As the agregator is stateful, be very careful to run several instances. You
 * might want to use Akka and its actor model instead of EJB, or at least scala
 * sequences. The code below is absolutely not thread safe, even though I took
 * some precautions.
 * 
 * @author Christophe Marchal
 *
 */

@MessageDriven(name = "AgregatorMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/agregator"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "MaxPoolSize", propertyValue = "1") })
public class AgregatorMDB implements MessageListener {

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/contentbasedecho")
	private Queue echo;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;

	private static ConcurrentMap<String, List<String>> state = null;

	public AgregatorMDB() {
		synchronized (AgregatorMDB.class) {
			if (AgregatorMDB.state == null) AgregatorMDB.state = new ConcurrentHashMap<String, List<String>>();
		}
	}

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				String correlationId = message.getJMSCorrelationID();
				Long expectedNumberOfMessage = message.getLongProperty("numberOfMessages");
				final String text = ((TextMessage) message).getText();
				synchronized (AgregatorMDB.class) {
					if (state.containsKey(correlationId)) updateMessageList(message, correlationId, text);
					else createMessageList(correlationId, text);
					if (isComplete(correlationId, expectedNumberOfMessage)) context.createProducer().send(echo, aggregate(correlationId));
				}

			} else {
				context.createProducer().send(invalidMessageQueue, "Splitter only accept text messages. Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("serial")
	private void createMessageList(String correlationId, final String text) {
		state.put(correlationId, new ArrayList<String>() {
			{
				add(text);
			}
		});
	}

	private void updateMessageList(Message message, String correlationId, final String text) {
		List<String> originalMessages = null;
		List<String> destinationMessages = null;
		originalMessages = state.get(correlationId);
		destinationMessages = new ArrayList<String>(originalMessages);
		destinationMessages.add(text);
		state.put(correlationId, destinationMessages);
	}

	private String aggregate(String correlationId) {
		return Joiner.on(" ").join(state.get(correlationId));
	}

	private Boolean isComplete(String correlationId, Long expectedNumberOfMessage) {
		return state.containsKey(correlationId) ? state.get(correlationId).size() >= expectedNumberOfMessage : true;
	}

}
