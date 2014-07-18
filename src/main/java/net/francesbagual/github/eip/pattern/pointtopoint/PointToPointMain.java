package net.francesbagual.github.eip.pattern.pointtopoint;

import java.util.Properties;
import java.util.logging.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PointToPointMain {
	private static final Logger log = Logger.getLogger(PointToPointMain.class.getName());

	private static final String DEFAULT_MESSAGE = "Hello, World!";
	private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
	private static final String DEFAULT_DESTINATION = "jms/queue/pointtopoint";
	private static final String DEFAULT_MESSAGE_COUNT = "1";
	private static final String DEFAULT_USERNAME = "quickstartUser";
	private static final String DEFAULT_PASSWORD = "quickstartPwd1";
	private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
	private static final String PROVIDER_URL = "http-remoting://127.0.0.1:8080";

	public static void main(String[] args) {
		Context namingContext = null;
		try {
			String userName = System.getProperty("username", DEFAULT_USERNAME);
			String password = System.getProperty("password", DEFAULT_PASSWORD);

			namingContext = setUpNamingContext(userName, password);
			ConnectionFactory connectionFactory = getConnectionFactory(namingContext);
			Destination destination = getDestination(namingContext);

			int count = Integer.parseInt(System.getProperty("message.count",DEFAULT_MESSAGE_COUNT));
			String content = System.getProperty("message.content", DEFAULT_MESSAGE);

			try (JMSContext context = connectionFactory.createContext(userName, password)) {
				log.info("Sending " + count + " messages with content: " + content);
				// Send the specified number of messages
				for (int i = 0; i < count; i++) context.createProducer().send(destination, content);

				// Create the JMS consumer
				JMSConsumer consumer = context.createConsumer(destination);
				// Then receive the same number of messages that were sent
				for (int i = 0; i < count; i++) {
					String text = consumer.receiveBody(String.class, 5000);
					log.info("Received message with content " + text);
				}
			}

		} catch (Exception e) {
			log.severe(e.getMessage());
		} finally {
			if (namingContext != null) {
				try {
					namingContext.close();
				} catch (NamingException e) {
					log.severe(e.getMessage());
				}
			}
		}

	}

	private static Context setUpNamingContext(String userName, String password)
			throws NamingException {
		Context namingContext;
		final Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL,System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
		env.put(Context.SECURITY_PRINCIPAL, userName);
		env.put(Context.SECURITY_CREDENTIALS, password);
		namingContext = new InitialContext(env);
		return namingContext;
	}

	private static Destination getDestination(Context namingContext)
			throws NamingException {
		String destinationString = System.getProperty("destination",DEFAULT_DESTINATION);
		log.info("Attempting to acquire destination \"" + destinationString + "\"");
		Destination destination = (Destination) namingContext.lookup(destinationString);
		log.info("Found destination \"" + destinationString + "\" in JNDI");
		return destination;
	}

	private static ConnectionFactory getConnectionFactory(Context namingContext)throws NamingException {
		String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
		log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
		ConnectionFactory connectionFactory = (ConnectionFactory) namingContext.lookup(connectionFactoryString);
		log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");
		return connectionFactory;
	}

}
