package net.francesbagual.github.eip.pattern.datatypechannel.mdb;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import net.francesbagual.github.eip.pattern.datatypechannel.message.Query;

@MessageDriven(name = "QueryMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/query"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class QueryMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(QueryMDB.class.toString());

	@Override
	public void onMessage(Message receivedMessage) {
		try {
			if (receivedMessage instanceof ObjectMessage) {
				ObjectMessage msg = (ObjectMessage) receivedMessage;
				Query query = (Query) msg.getObject();
				LOGGER.info("Received Message from queue: " + query.toString());
			} else {
				LOGGER.warning("Message of wrong type: " + receivedMessage.getClass().getName());
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
}
