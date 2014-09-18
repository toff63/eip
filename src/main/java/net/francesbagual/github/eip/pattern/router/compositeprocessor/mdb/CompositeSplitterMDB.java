package net.francesbagual.github.eip.pattern.router.compositeprocessor.mdb;

import java.util.UUID;

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

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

@MessageDriven(name = "CompositeSplitterMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/compositesplitter"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class CompositeSplitterMDB implements MessageListener {

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/compositecontentrouter")
	private Queue contentRouter;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) splitAndSendMessages(message);
			else sentToInvalidMessageQueue(message);
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	private void sentToInvalidMessageQueue(Message message) {
		context.createProducer().send(invalidMessageQueue, "Splitter only accept text messages. Received: " + message);
	}

	private void splitAndSendMessages(Message message) throws JMSException {
		Iterable<String> splittedMessages = split(((TextMessage) message).getText());
		String correlationId = UUID.randomUUID().toString();
		Long numberOfMessages = new Long(Iterables.size(splittedMessages));
		for (String splittedMessage : splittedMessages)
			context.createProducer().send(contentRouter, createMessage(splittedMessage, correlationId, numberOfMessages));
	}

	private Message createMessage(String content, String correlationId, Long numberOfMessages) throws JMSException {
		Message message = context.createTextMessage(content);
		message.setJMSCorrelationID(correlationId);
		message.setLongProperty("numberOfMessages", numberOfMessages);
		return message;
	}

	private Iterable<String> split(String text) {
		return Splitter.on(" ")
				.trimResults()
				.omitEmptyStrings()
				.split(text);
	}
}
