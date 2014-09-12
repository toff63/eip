package net.francesbagual.github.eip.pattern.router.splitter.mdb;

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

@MessageDriven(name = "SplitterMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/splitter"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class SplitterMDB implements MessageListener {

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/contentbasedecho")
	private Queue echo;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;
	
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				Iterable<String> splittedMessages = split(((TextMessage) message).getText());
				for(String splittedMessage : splittedMessages) context.createProducer().send(echo, splittedMessage);
			} else {
				context.createProducer().send(invalidMessageQueue, "Splitter only accept text messages. Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	private Iterable<String> split(String text) {
		return Splitter.on(" ")
				.trimResults()
				.omitEmptyStrings()
				.split(text);
	}
}
