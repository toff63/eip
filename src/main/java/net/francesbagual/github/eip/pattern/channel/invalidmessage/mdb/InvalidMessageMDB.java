package net.francesbagual.github.eip.pattern.channel.invalidmessage.mdb;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import net.francesbagual.github.eip.pattern.channel.invalidmessage.message.InvalidMessage;

@MessageDriven(name = "InvalidMessageMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/invalidmessage"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class InvalidMessageMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(InvalidMessageMDB.class.toString());

	@Override
	public void onMessage(Message receivedMessage) {
		try {
			if (receivedMessage instanceof ObjectMessage) {
				ObjectMessage msg = (ObjectMessage) receivedMessage;
				InvalidMessage invalidMessage = (InvalidMessage) msg.getObject();
				LOGGER.info("Invalid message " + invalidMessage.toString() + " has been sent to " + invalidMessage.getSender());
			} else {
				LOGGER.warning("Sending invalid message to the invalid message queue!! WTF :)");
				LOGGER.warning("Received " + receivedMessage.toString());
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

}
