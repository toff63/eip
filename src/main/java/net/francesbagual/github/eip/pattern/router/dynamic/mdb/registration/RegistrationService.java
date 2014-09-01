package net.francesbagual.github.eip.pattern.router.dynamic.mdb.registration;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.Topic;

@JMSDestinationDefinitions(
		value = {
				@JMSDestinationDefinition(
						name = "jms/topic/dynamicregistration",
						interfaceName = "javax.jms.Topic",
						destinationName = "dynamicregistration"
				)
		})
@Singleton
public class RegistrationService {

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/topic/dynamicregistration")
	private Topic router;

	@Schedule(second = "*/1", minute = "*", hour = "*", persistent = false)
	public void doWork() {
		context.createProducer().send(router, "registerRoute");
	}
}
