package net.francesbagual.github.eip.pattern.transformation.envelopewrapper.mdb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;

import net.francesbagual.github.eip.pattern.transformation.envelopewrapper.message.Envelope;


@MessageDriven(name = "WrapperMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/wrapper"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class WrapperMDB implements MessageListener {

	@Inject
	JMSContext context;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;
	@Resource(lookup = "jms/queue/someothermdb")
	private Queue othermdb;
	
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				context.createProducer().send(othermdb, wrap(message));
			} else {
				context.createProducer().send(invalidMessageQueue, "Wrapper currently only knows how to wrap Text messages. Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private ObjectMessage wrap(Message message) throws JMSException {
		return context.createObjectMessage(new Envelope<String>(((TextMessage) message).getText()));
	}

}
