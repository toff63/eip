package net.francesbagual.github.eip.pattern.transformation.enricher.mdb;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@MessageDriven(name = "EchoUserMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/echouser"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class EchoUserMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(EchoUserMDB.class.toString());

	@Override
	public void onMessage(Message receivedMessage) {
		try {
			if (receivedMessage instanceof ObjectMessage) {
				User user = ((ObjectMessage) receivedMessage).getBody(User.class);
				LOGGER.info("EchoMDB received " + user.toString());
			} else {
				LOGGER.info(receivedMessage.toString());
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
	
}
