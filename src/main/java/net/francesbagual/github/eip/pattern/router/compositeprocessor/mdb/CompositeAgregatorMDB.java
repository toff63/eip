package net.francesbagual.github.eip.pattern.router.compositeprocessor.mdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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

import net.francesbagual.github.eip.pattern.router.agregator.mdb.AgregatorMDB;

import com.google.common.base.Joiner;

@MessageDriven(name = "CompositeAgregatorMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/compositeaggregator"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class CompositeAgregatorMDB implements MessageListener {

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/contentbasedecho")
	private Queue echo;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;

	private static ConcurrentMap<String, List<String>> state = null;

	public CompositeAgregatorMDB() {
		synchronized (AgregatorMDB.class) {
			if (CompositeAgregatorMDB.state == null) CompositeAgregatorMDB.state = new ConcurrentHashMap<String, List<String>>();
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
		List<String> destinationMessages = state.get(correlationId);
		destinationMessages.add(text);
		state.put(correlationId, destinationMessages);
	}

	private String aggregate(String correlationId) {
		return Joiner.on(" ").join(resequence(correlationId));
	}

	private List<String> resequence(String correlationId){
		List<String> orderedMessages = new ArrayList<String>(state.get(correlationId));
		Collections.sort(orderedMessages);
		List<String> result = new LinkedList<>();
		for(String message : orderedMessages)result.add(message);
		return result;
	}
	private Boolean isComplete(String correlationId, Long expectedNumberOfMessage) {
		return state.containsKey(correlationId) ? state.get(correlationId).size() >= expectedNumberOfMessage : true;
	}

}
