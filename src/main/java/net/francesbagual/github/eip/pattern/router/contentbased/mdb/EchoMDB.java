package net.francesbagual.github.eip.pattern.router.contentbased.mdb;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;


@MessageDriven(name = "ContentBasedEchoMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/contentbasedecho"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class EchoMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(EchoMDB.class.toString());

	@Override
	public void onMessage(Message receivedMessage) {
		try {
			if (receivedMessage instanceof TextMessage) {
				TextMessage msg = (TextMessage) receivedMessage;
				String text = msg.getText();
				LOGGER.info("EchoMDB received " + text);
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
}
