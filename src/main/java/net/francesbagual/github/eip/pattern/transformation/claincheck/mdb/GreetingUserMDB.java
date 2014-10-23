package net.francesbagual.github.eip.pattern.transformation.claincheck.mdb;

import java.util.logging.Logger;

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

@MessageDriven(name = "GreetingUserMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/claimcheckgreeting"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class GreetingUserMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(GreetingUserMDB.class.toString());

	@Inject
	JMSContext context;
	@Resource(lookup = "jms/queue/claimcheckenricher")
	private Queue enricher;

	@Override
	public void onMessage(Message receivedMessage) {
		try {
			if (receivedMessage instanceof TextMessage) {
				String name = "Hello " + ((TextMessage) receivedMessage).getText();
				context.createProducer().send(enricher, createResponseMessage(receivedMessage, name));
			} else {
				LOGGER.info(receivedMessage.toString());
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	private Message createResponseMessage(Message originalMessage, String name) throws JMSException {
		Long userId = originalMessage.getLongProperty("userId");
		Message msg = context.createTextMessage(name);
		msg.setLongProperty("userId", userId);
		return msg;
	}
}
