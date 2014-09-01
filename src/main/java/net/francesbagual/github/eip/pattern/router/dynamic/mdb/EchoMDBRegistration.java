package net.francesbagual.github.eip.pattern.router.dynamic.mdb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Destination;
import javax.jms.Queue;

import net.francesbagual.github.eip.pattern.router.dynamic.mdb.registration.MDBRegistration;

@MessageDriven(name = "EchoMDBRegistration", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "topic/dynamicregistration"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class EchoMDBRegistration extends MDBRegistration {

	@Resource(lookup = "jms/queue/dynamicecho")
	private Queue echoQueue;

	@Override
	protected Destination getDestination() {
		return echoQueue;
	}

	@Override
	protected String getPrefix() {
		return "echo";
	}
}
