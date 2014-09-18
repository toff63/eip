package net.francesbagual.github.eip.pattern.router.compositeprocessor.mdb;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
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

@MessageDriven(name = "CompositeContentRouterMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/compositecontentrouter"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class CompositeContentRouterMDB implements MessageListener {

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/compositeecho")
	private Queue echo;
	@Resource(lookup = "jms/queue/compositehello")
	private Queue hello;
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) splitAndSendMessages(((TextMessage) message));
			else sentToInvalidMessageQueue(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sentToInvalidMessageQueue(Message message) {
		context.createProducer().send(invalidMessageQueue, "Splitter only accept text messages. Received: " + message);
	}

	private void splitAndSendMessages(TextMessage message) throws NamingException, JMSException {
		String content = message.getText();
		String correlationId = message.getJMSCorrelationID();
		Long numberOfMessages = message.getLongProperty("numberOfMessages");
		if (startWithAVogal(content)) sendToEcho(content, correlationId, numberOfMessages);
		else sendToHello(content, correlationId, numberOfMessages);
	}

	private void sendToHello(String content, String correlationId, Long numberOfMessages) throws JMSException, NamingException {
		send(hello, content, correlationId, numberOfMessages);
	}

	private void send(Destination dst, String content, String correlationId, Long numberOfMessages) throws NamingException, JMSException {
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("java:/ConnectionFactory");
			conn = connectionFactory.createConnection();
			conn.start();
			Session consumerSession = conn.createSession(false, AUTO_ACKNOWLEDGE);
			consumerSession.createProducer(dst).send(createMessage(consumerSession, content, correlationId, numberOfMessages));
			consumerSession.close();
		} finally {
			try {
				conn.close();
			} catch (Exception ignore) {}
		}
	}

	private void sendToEcho(String content, String correlationId, Long numberOfMessages) throws NamingException, JMSException {
		send(echo, content, correlationId, numberOfMessages);
	}

	private boolean startWithAVogal(String content) {
		return content.startsWith("a") || content.startsWith("o") || content.startsWith("u") || content.startsWith("i") || content.startsWith("y") || content.startsWith("e");
	}

	private Message createMessage(Session session, String content, String correlationId, Long numberOfMessages) throws JMSException {
		Message message = session.createTextMessage(content);
		message.setJMSCorrelationID(correlationId);
		message.setLongProperty("numberOfMessages", numberOfMessages);
		return message;
	}
}
