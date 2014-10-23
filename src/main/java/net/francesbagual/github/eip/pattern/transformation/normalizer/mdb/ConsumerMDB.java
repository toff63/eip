package net.francesbagual.github.eip.pattern.transformation.normalizer.mdb;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(name = "NormalizerConsumerMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/normalizerconsumer"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ConsumerMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(ConsumerMDB.class.toString());

	@Override
	public void onMessage(Message receivedMessage) {
		try {
			if (receivedMessage instanceof TextMessage) {
				LOGGER.info("ConsumerMDB received " + ((TextMessage)receivedMessage).getText());
			} else {
				LOGGER.info(receivedMessage.toString());
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

}
