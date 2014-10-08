package net.francesbagual.github.eip.pattern.transformation.filter.mdb;

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

@MessageDriven(name = "FilterMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/filter"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })

public class FilterMDB implements MessageListener {

	@Inject
	JMSContext context;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;
	@Resource(lookup = "jms/queue/contentbasedecho")
	private Queue echo;
	
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof ObjectMessage) {
				User user = ((ObjectMessage) message).getBody(User.class);
				context.createProducer().send(echo, filter(user));
			} else {
				context.createProducer().send(invalidMessageQueue, "Wrapper currently only knows how to wrap Text messages. Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private Message filter(User user) throws JMSException {
		return context.createTextMessage(user.id.toString());
	}
}
