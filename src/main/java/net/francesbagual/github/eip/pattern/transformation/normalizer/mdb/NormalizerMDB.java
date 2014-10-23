package net.francesbagual.github.eip.pattern.transformation.normalizer.mdb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;

import net.francesbagual.github.eip.pattern.transformation.normalizer.NormalizedMessage;

@MessageDriven(name = "NormalizerMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/normalizer"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class NormalizerMDB implements MessageListener {

	@Inject
	JMSContext context;
	@Resource(lookup = "jms/queue/normalizer")
	private Queue normalizer;
	@Resource(lookup = "jms/queue/normalizerconsumer")
	private Queue consuer;

	@Override
	public void onMessage(Message message) {
		try {
			if (message.isBodyAssignableTo(NormalizedMessage.class)) {
				context.createProducer().send(consuer, message.getBody(NormalizedMessage.class).content);
			} else {
				context.createProducer().send(normalizer, new NormalizedMessage(message.getBody(Object.class).toString()));
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

}
