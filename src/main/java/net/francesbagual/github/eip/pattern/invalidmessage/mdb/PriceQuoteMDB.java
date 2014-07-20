package net.francesbagual.github.eip.pattern.invalidmessage.mdb;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import net.francesbagual.github.eip.pattern.datatypechannel.message.PriceQuote;
import net.francesbagual.github.eip.pattern.invalidmessage.message.InvalidMessage;

@JMSDestinationDefinitions(
		value = {
				@JMSDestinationDefinition(
						name = "jms/queue/invalidmessage",
						interfaceName = "javax.jms.Queue",
						destinationName = "invalidmessage"
				)
		})
@MessageDriven(name = "PriceQuoteInvalidMessageMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/pricequoteinvalidmessage"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class PriceQuoteMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(PriceQuoteMDB.class.toString());

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;

	@Override
	public void onMessage(Message receivedMessage) {
		try {
			if (receivedMessage instanceof ObjectMessage && ((ObjectMessage) receivedMessage).getObject() instanceof PriceQuote) {
				PriceQuote priceQuote = (PriceQuote) ((ObjectMessage) receivedMessage).getObject();
				LOGGER.info("Received Message from queue: " + priceQuote.toString());
			} else {
				LOGGER.warning("Message of wrong type: " + receivedMessage.getClass().getName());
				sendInvalidMessage(receivedMessage);
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	private void sendInvalidMessage(Message receivedMessage) throws JMSException {
		LOGGER.warning("Sending it to the invalid message queue");
		if (receivedMessage instanceof ObjectMessage) {
			context.createProducer().send(invalidMessageQueue, new InvalidMessage(PriceQuoteMDB.class.toString(), ((ObjectMessage) receivedMessage).getObject()));
		}
	}
}
