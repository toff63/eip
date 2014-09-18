package net.francesbagual.github.eip.pattern.router.compositeprocessor.mdb;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@MessageDriven(name = "CompositeHelloMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/compositehello"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "MaxPoolSize", propertyValue = "1") })
public class CompositeHelloMDB implements MessageListener {

	@Inject JMSContext context;
	@Resource(lookup = "jms/queue/compositeaggregator")
	private Queue aggregator;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) sendToAggregator((TextMessage) message);
			else sentToInvalidMessageQueue(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sendToAggregator(TextMessage message) throws JMSException, NamingException {
		String content = "Hello " + message.getText();
		String correlationId = message.getJMSCorrelationID();
		Long numberOfMessages = message.getLongProperty("numberOfMessages");
		send(content, correlationId, numberOfMessages);
	}

	private void send(String content, String correlationId, Long numberOfMessages) throws NamingException, JMSException {
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("java:/ConnectionFactory");
			conn = connectionFactory.createConnection();
			conn.start();
			Session consumerSession = conn.createSession(false, AUTO_ACKNOWLEDGE);
			consumerSession.createProducer(aggregator).send(createMessage(consumerSession, content, correlationId, numberOfMessages));
			consumerSession.close();
		} finally {
			try {
				conn.close();
			} catch (Exception ignore) {}
		}
	}

	private Message createMessage(Session session, String content, String correlationId, Long numberOfMessages) throws JMSException {
		Message message = session.createTextMessage(content);
		message.setJMSCorrelationID(correlationId);
		message.setLongProperty("numberOfMessages", numberOfMessages);
		return message;
	}

	private void sentToInvalidMessageQueue(Message message) {
		context.createProducer().send(invalidMessageQueue, "Splitter only accept text messages. Received: " + message);
	}

}
