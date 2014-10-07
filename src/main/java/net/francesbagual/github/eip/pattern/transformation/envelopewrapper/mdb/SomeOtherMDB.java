package net.francesbagual.github.eip.pattern.transformation.envelopewrapper.mdb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

@MessageDriven(name = "SomeOtherMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/someothermdb"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class SomeOtherMDB implements MessageListener {

	@Inject
	JMSContext context;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;
	@Resource(lookup = "jms/queue/unwrapper")
	private Queue unwrapper;

	@Override
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			context.createProducer().send(unwrapper, message);
		} else {
			context.createProducer().send(invalidMessageQueue, "SomeOtherMDB only understand Envelope type of message. Received: " + message);
		}
	}

}
