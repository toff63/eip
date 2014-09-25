package net.francesbagual.github.eip.pattern.router.processmanager.mdb;

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

@MessageDriven(name = "HelloProcessManagerMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/processmanagerhello"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class HelloProcessManagerMDB implements MessageListener {

	@Inject
	JMSContext context;

	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) context.createProducer().send(message.getJMSReplyTo(), createNextMessage(message));
			else sentToInvalidMessageQueue(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Message createNextMessage(Message message) throws JMSException {
		Message nextMessage = context.createTextMessage(nextMessage(((TextMessage) message).getText()));
		nextMessage.setJMSCorrelationID(message.getJMSCorrelationID());
		return nextMessage;
	}

	private String nextMessage(String entryText) {
		return "Hello " + entryText;
	}

	private void sentToInvalidMessageQueue(Message message) {
		context.createProducer().send(invalidMessageQueue, "Splitter only accept text messages. Received: " + message);
	}

}
