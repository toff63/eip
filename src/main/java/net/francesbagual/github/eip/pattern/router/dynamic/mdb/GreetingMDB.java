package net.francesbagual.github.eip.pattern.router.dynamic.mdb;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(name = "DynamicGreetingMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/dynamicgreeting"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class GreetingMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(GreetingMDB.class.toString());
	
	
	@Override
	public void onMessage(Message receivedMessage) {
		try {
			if (receivedMessage instanceof TextMessage) {
				TextMessage msg = (TextMessage) receivedMessage;
				String text = msg.getText();
				LOGGER.info("GreetingMDB received " + text);
			} 
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

}
