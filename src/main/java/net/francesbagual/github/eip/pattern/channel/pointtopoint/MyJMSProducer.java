package net.francesbagual.github.eip.pattern.channel.pointtopoint;

import javax.jms.JMSContext;
import javax.jms.Queue;

//@Stateless
public class MyJMSProducer {
	// @Inject
	private JMSContext context;

	// @Resource(mappedName = "java:jboss/jms/queue/pointtopoint")
	Queue myQueue;

	public String startReceiver() {
		String message = context.createConsumer(myQueue).receiveBody(String.class);
		return message;
	}
}
