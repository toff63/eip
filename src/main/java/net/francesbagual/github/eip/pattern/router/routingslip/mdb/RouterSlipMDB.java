package net.francesbagual.github.eip.pattern.router.routingslip.mdb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;

import com.google.common.collect.Lists;

@MessageDriven(name = "RouterSlipMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/routerslip"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class RouterSlipMDB implements MessageListener {

	@Inject
	JMSContext context;

	@Resource(lookup = "jms/queue/routersliphello")
	private Destination hello;
	@Resource(lookup = "jms/queue/routerslipfourtytwo")
	private Destination fourtytwo;
	@Resource(lookup = "jms/queue/contentbasedecho")
	private Destination echo;

	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				SlipRoutedMessage slipRoutedMessage = new SlipRoutedMessage(((TextMessage) message).getText());
				slipRoutedMessage.setPath(Lists.newArrayList(hello, fourtytwo, echo));
				Message msg = context.createObjectMessage(slipRoutedMessage);
				context.createProducer().send(hello, msg);
			}
			else sentToInvalidMessageQueue(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sentToInvalidMessageQueue(Message message) {
		context.createProducer().send(invalidMessageQueue, "Splitter only accept text messages. Received: " + message);
	}
}
