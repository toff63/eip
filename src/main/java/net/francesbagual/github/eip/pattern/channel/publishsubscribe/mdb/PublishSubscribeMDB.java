package net.francesbagual.github.eip.pattern.channel.publishsubscribe.mdb;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(name = "PublishSubscribeMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "topic/publishsubscribe"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class PublishSubscribeMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(PublishSubscribeMDB.class.toString());

	@Override
	public void onMessage(Message receivedMessage) {
		TextMessage msg = null;
		try {
			if (receivedMessage instanceof TextMessage) {
				msg = (TextMessage) receivedMessage;
				LOGGER.info("Received Message from topic: " + msg.getText());
			} else {
				LOGGER.warning("Message of wrong type: " + receivedMessage.getClass().getName());
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
}
