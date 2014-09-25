package net.francesbagual.github.eip.pattern.router.processmanager.mdb;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;

import org.apache.commons.collections4.map.HashedMap;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@MessageDriven(name = "ProcessManagerMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/processmanager"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ProcessManagerMDB implements MessageListener {

	@Inject
	JMSContext context;

	@Resource(lookup = "jms/queue/processmanagerhello")
	private Destination hello;
	@Resource(lookup = "jms/queue/processmanagerfourtytwo")
	private Destination fourtytwo;
	@Resource(lookup = "jms/queue/contentbasedecho")
	private Destination echo;
	@Resource(lookup = "jms/queue/processmanager")
	private Queue processManager;
	
	@Resource(lookup = "jms/queue/invalidmessage")
	private Queue invalidMessageQueue;

	private List<Destination> process = null;
	private Map<String, Integer> processInstances = new HashedMap<String, Integer>();

	@Override
	public void onMessage(Message message) {
		lazyInit();
		try {
			if (message instanceof TextMessage) {
				String correlationID = getJMSCorrelationId(message);
				Integer processStep = nextStepId(correlationID);
				Message nextMessage = createNextMessage(correlationID, ((TextMessage) message).getText());
				context.createProducer().send(process.get(processStep), nextMessage);
			}
			else sentToInvalidMessageQueue(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void lazyInit() {
		if(process == null) process = Lists.newArrayList(hello, fourtytwo, echo);
	}

	private Message createNextMessage(String correlationID, String text)  throws JMSException {
		Message nextMessage = context.createTextMessage(text);
		nextMessage.setJMSCorrelationID(correlationID);
		nextMessage.setJMSReplyTo(processManager);
		return nextMessage;
	}

	private String getJMSCorrelationId(Message message) throws JMSException {
		return Strings.isNullOrEmpty(message.getJMSCorrelationID()) ? UUID.randomUUID().toString() : message.getJMSCorrelationID();
	}

	private Integer nextStepId(String jmsCorrelationID) {
		if(!processInstances.containsKey(jmsCorrelationID)) return createProcessInstance(jmsCorrelationID);
		return findProcessInstance(jmsCorrelationID);
	}

	private Integer findProcessInstance(String jmsCorrelationID) {
		Integer processInstance =  processInstances.get(jmsCorrelationID) + 1;
		processInstances.put(jmsCorrelationID, processInstance);
		return processInstance;
	}

	private Integer createProcessInstance(String jmsCorrelationID) {
		processInstances.put(jmsCorrelationID, 0);
		return 0;
	}

	private void sentToInvalidMessageQueue(Message message) {
		context.createProducer().send(invalidMessageQueue, "Splitter only accept text messages. Received: " + message);
	}

}
