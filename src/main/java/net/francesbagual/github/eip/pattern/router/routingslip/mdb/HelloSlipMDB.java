package net.francesbagual.github.eip.pattern.router.routingslip.mdb;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

@MessageDriven(name = "HelloSlipMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/routersliphello"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class HelloSlipMDB implements MessageListener {

	@Inject JMSContext context;
	@Resource(lookup = "jms/queue/routersliphello")
	private Queue hello;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;
	
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof ObjectMessage) {
				@SuppressWarnings("unchecked")
				SlipRoutedMessage msg = (SlipRoutedMessage) ((ObjectMessage) message).getObject();
				List<Destination> path = msg.path();
				Destination nextDestination = null;
				for(int i=0; i < path.size(); i++) if(hello.equals(path.get(i))) nextDestination = path.get(i+1);
				context.createProducer().send(nextDestination, context.createObjectMessage(nextMessage(msg)));
			}
			else sentToInvalidMessageQueue(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private SlipRoutedMessage nextMessage(SlipRoutedMessage entryMessage){
		entryMessage.setBody("Hello " + entryMessage.body());
		return entryMessage;
	}

	private void sentToInvalidMessageQueue(Message message) {
		context.createProducer().send(invalidMessageQueue, "Splitter only accept text messages. Received: " + message);
	}

}
