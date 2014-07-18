package net.francesbagual.github.eip.pattern.pointtopoint;

import javax.jms.JMSContext;
import javax.jms.Queue;

//@RequestScoped
public class MyJMSConsumer {
	// @Resource(mappedName = "java:jboss/jms/queue/pointtopoint")
	private Queue queueExample;

	// @Inject
	JMSContext context;

	public void sendMessage(String txt) {
		try {
			context.createProducer().send(queueExample, txt);
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}
}
