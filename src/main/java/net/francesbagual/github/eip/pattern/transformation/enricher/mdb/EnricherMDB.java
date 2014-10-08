package net.francesbagual.github.eip.pattern.transformation.enricher.mdb;

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

@MessageDriven(name = "EnricherMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/enricher"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })

public class EnricherMDB implements MessageListener {

	@Inject
	JMSContext context;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;
	@Resource(lookup = "jms/queue/echouser")
	private Queue echoUser;
	
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				context.createProducer().send(echoUser, enrich(Long.valueOf(((TextMessage) message).getText())));
			} else {
				context.createProducer().send(invalidMessageQueue, "Wrapper currently only knows how to wrap Text messages. Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private ObjectMessage enrich(Long id) throws JMSException {
		User user = new UserDao().getUser(id);
		return context.createObjectMessage(user);
	}
}
