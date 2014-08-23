package net.francesbagual.github.eip.pattern.router.contentbased.mdb;

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
import javax.jms.Queue;
import javax.jms.TextMessage;

@JMSDestinationDefinitions(
		value = {
				@JMSDestinationDefinition(
						name = "jms/queue/contentbasedgreeting",
						interfaceName = "javax.jms.Queue",
						destinationName = "contentbasedgreeting"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/contentbasedecho",
						interfaceName = "javax.jms.Queue",
						destinationName = "contentbasedecho"
				)
		})
@MessageDriven(name = "ContentBasedRouterMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/contentbasedrouter"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ContentBasedRouterMDB implements MessageListener {

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/contentbasedgreeting")
	private Queue greeting;
	@Resource(lookup = "jms/queue/contentbasedecho")
	private Queue echo;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				String text = ((TextMessage) message).getText();
				if (text.startsWith("greeting:")) context.createProducer().send(greeting, message);
				else if (text.startsWith("echo:")) context.createProducer().send(echo, message);
				else context.createProducer().send(invalidMessageQueue, message);
			}
		} catch (JMSException exc) {
			exc.printStackTrace();
		}
	}
}
