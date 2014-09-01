package net.francesbagual.github.eip.pattern.router.dynamic.mdb.router;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Destination;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import net.francesbagual.github.eip.pattern.router.dynamic.mdb.registration.RoutingMessage;

@JMSDestinationDefinitions(
		value = {
				@JMSDestinationDefinition(
						name = "jms/queue/routingdb",
						interfaceName = "javax.jms.Queue",
						destinationName = "routingdb"
				)
		})
@MessageDriven(name = "RoutingDb", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/routingdb"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "MaxPoolSize", propertyValue = "1") })
public class RoutingDb implements MessageListener {

	public static ConcurrentMap<String, Destination> db = new ConcurrentHashMap<>();

	@Override
	public void onMessage(Message message) {
		try {
			RoutingMessage routing = (RoutingMessage) ((ObjectMessage) message).getObject();
			db.put(routing.messagePrefix, routing.destination);
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

}
