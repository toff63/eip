package net.francesbagual.github.eip.pattern.transformation.claincheck.mdb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import net.francesbagual.github.eip.pattern.transformation.claincheck.ClaimCheckMessage;

@MessageDriven(name = "ClaimCheckFilterMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/claimcheckfilter"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })

public class FilterMDB implements MessageListener {

	@Inject
	JMSContext context;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;
	@Resource(lookup = "jms/queue/claimcheckgreeting")
	private Queue echo;
	
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof ObjectMessage) {
				ClaimCheckMessage claimcheckMessage = ((ObjectMessage) message).getBody(ClaimCheckMessage.class);
				Long id = UserDao.getInstance().storeUser(claimcheckMessage.user);
				context.createProducer().send(echo, filter(claimcheckMessage, id));
			} else {
				context.createProducer().send(invalidMessageQueue, "Wrapper currently only knows how to wrap Text messages. Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private Message filter(ClaimCheckMessage originalMessage, Long userId) throws JMSException {
		Message msg = context.createTextMessage(originalMessage.user.name);
		msg.setLongProperty("userId", userId);
		return msg;
	}
}
