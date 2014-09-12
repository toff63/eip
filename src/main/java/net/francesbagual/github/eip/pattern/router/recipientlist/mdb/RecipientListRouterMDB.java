package net.francesbagual.github.eip.pattern.router.recipientlist.mdb;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(name = "RecipientListRouterMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/recipientlistrouter"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class RecipientListRouterMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(RecipientListRouterMDB.class.toString());

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/contentbasedgreeting")
	private Destination greeting;
	@Resource(lookup = "jms/queue/contentbasedecho")
	private Destination echo;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Destination invalidMessageQueue;

	private Collection<Destination> getRecipients(String message){
		if(message.startsWith("greeting:")) return Arrays.asList(greeting, echo);
		if(message.startsWith("echo:")) return Arrays.asList(echo);
		return Arrays.asList(invalidMessageQueue);
	}
	
	@Override
	public void onMessage(Message message) {
		LOGGER.info("received message " + message);
		try {
			if (message instanceof TextMessage) {
				String text = ((TextMessage) message).getText();
				Collection<Destination> recipients = getRecipients(text);
				LOGGER.info("Computed the following destinations: " + recipients);
				for(Destination recipient : recipients) context.createProducer().send(recipient, message);
			}
		} catch (JMSException exc) {
			exc.printStackTrace();
		}
	}
}
