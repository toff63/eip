package net.francesbagual.github.eip.pattern.router.dynamic.mdb.registration;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;


public abstract class MDBRegistration implements MessageListener {

	abstract protected  Destination getDestination();
	abstract protected  String getPrefix();

	@Resource(lookup = "jms/queue/routingdb")
	private Queue routingDb;

	@Inject
	private JMSContext context;

	@Override
	public void onMessage(Message receivedMessage) {
		context.createProducer().send(routingDb, new RoutingMessage(getDestination(), getPrefix()));
	}

}
