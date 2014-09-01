package net.francesbagual.github.eip.pattern.router.dynamic.mdb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Destination;
import javax.jms.Queue;

import net.francesbagual.github.eip.pattern.router.dynamic.mdb.registration.MDBRegistration;

@MessageDriven(name = "GreetingMDBRegistration", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "topic/dynamicregistration"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class GreetingMDBRegistration extends MDBRegistration {

	@Resource(lookup = "jms/queue/dynamicgreeting")
	private Queue greetingQueue;

	@Override
	protected Destination getDestination() {
		return greetingQueue;
	}

	@Override
	protected String getPrefix() {
		return "greeting";
	}
}
