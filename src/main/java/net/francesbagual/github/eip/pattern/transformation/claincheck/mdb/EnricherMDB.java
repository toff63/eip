package net.francesbagual.github.eip.pattern.transformation.claincheck.mdb;

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

import net.francesbagual.github.eip.pattern.transformation.claincheck.ClaimCheckMessage;

@MessageDriven(name = "ClaimCheckEnricherMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/claimcheckenricher"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })

public class EnricherMDB implements MessageListener {

	@Inject
	JMSContext context;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;
	@Resource(lookup = "jms/queue/claimcheckecho")
	private Queue echoClaimCheck;
	
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				User user = UserDao.getInstance().getUser(message.getLongProperty("userId"));
				ClaimCheckMessage claimCheckMessage = new ClaimCheckMessage( user, ((TextMessage) message).getText());
				context.createProducer().send(echoClaimCheck, claimCheckMessage);
			} else {
				context.createProducer().send(invalidMessageQueue, "Wrapper currently only knows how to wrap Text messages. Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
