package net.francesbagual.github.eip.pattern.router.dynamic.mdb.router;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

@MessageDriven(name = "DynamicRouterMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/dynamicrouter"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class DynamicRouterMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(DynamicRouterMDB.class.toString());

	@Inject
	private JMSContext context;
	
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;
	
	Pattern messagePrefixPattern = Pattern.compile("\\w+");

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				String text = ((TextMessage) message).getText();
				Matcher matcher = messagePrefixPattern.matcher(text);
				if(matcher.find()){
					String messagePrefix = matcher.group();
					if (RoutingDb.db.containsKey(messagePrefix)) context.createProducer().send(RoutingDb.db.get(messagePrefix), message);	
					else context.createProducer().send(invalidMessageQueue, message);
				}
				else {
					LOGGER.info("No prefix found on message: " + text);
					context.createProducer().send(invalidMessageQueue, message);
				}
			}
		} catch (JMSException exc) {
			exc.printStackTrace();
		}
	}
}
