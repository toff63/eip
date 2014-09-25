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

@MessageDriven(name = "FourtyTwoMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/routerslipfourtytwo"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class FourtyTwoMDB implements MessageListener {

	@Inject JMSContext context;
	@Resource(lookup = "jms/queue/routerslipfourtytwo")
	private Queue fourtytwo;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;
	
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof ObjectMessage) {
				SlipRoutedMessage routedMessage = (SlipRoutedMessage) ((ObjectMessage)message).getObject();
				List<Destination> path =routedMessage.path();
				Destination nextDestination = null;
				for(int i=0; i < path.size(); i++) if(fourtytwo.equals(path.get(i))) nextDestination = path.get(i+1);
				Message msg = context.createObjectMessage(nextMessage(routedMessage));
				context.createProducer().send(nextDestination, msg);
			}
			else sentToInvalidMessageQueue(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SlipRoutedMessage nextMessage(SlipRoutedMessage previousMessage){
		previousMessage.setBody(previousMessage.body() + "\n The response is 42 :)");
		return previousMessage;
	}
	
	private void sentToInvalidMessageQueue(Message message) {
		context.createProducer().send(invalidMessageQueue, "Splitter only accept text messages. Received: " + message);
	}


}
