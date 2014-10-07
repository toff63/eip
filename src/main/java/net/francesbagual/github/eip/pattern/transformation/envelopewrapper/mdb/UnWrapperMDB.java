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

@MessageDriven(name = "UnWrapperMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/unwrapper"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class UnWrapperMDB implements MessageListener {

	@Inject
	JMSContext context;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;
	@Resource(lookup = "jms/queue/recipient")
	private Queue recipient;
	
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof ObjectMessage) {
				context.createProducer().send(recipient, unwrap((ObjectMessage) message));
			} else {
				context.createProducer().send(invalidMessageQueue, "Wrapper currently only knows how to wrap Text messages. Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private TextMessage unwrap(ObjectMessage message) throws JMSException {
		return context.createTextMessage((String)message.getBody(Envelope.class).body);
	}
}
