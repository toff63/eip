package net.francesbagual.github.eip.pattern.message.requestresponse.mdb;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TemporaryQueue;

import net.francesbagual.github.eip.pattern.message.requestresponse.message.CalculatorRequest;
import net.francesbagual.github.eip.pattern.message.requestresponse.message.CalculatorRequestMessage;
import net.francesbagual.github.eip.pattern.message.requestresponse.ws.CalculatorAPI;

@MessageDriven(name = "CalculatorMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/calculator"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class CalculatorMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(CalculatorMDB.class.toString());

	@Inject @RequestResponseProfile
	private CalculatorAPI calculator;
	
	@Inject
	private JMSContext context;
	
	@Override
	public void onMessage(Message receivedMessage) {
		try {
			if (receivedMessage instanceof ObjectMessage) {
				ObjectMessage msg = (ObjectMessage) receivedMessage;
				Destination responseQueue = msg.getJMSReplyTo();
				CalculatorRequestMessage calculatorRequestMessage = (CalculatorRequestMessage) msg.getObject();
				String operation = (String) calculatorRequestMessage.headers().get("operation");
				String correlationID = msg.getJMSMessageID();
				CalculatorRequest request = (CalculatorRequest) calculatorRequestMessage.body();
				callService(responseQueue, operation, correlationID, request);
			} else {
				LOGGER.warning("Message of wrong type: " + receivedMessage.getClass().getName());
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	private void callService(Destination responseQueue, String operation, String correlationID, CalculatorRequest request) throws JMSException {
		switch (operation) {
			case "add":
				sendResponse(responseQueue, calculator.add(request), correlationID);
				break;
			case "mult":
				sendResponse(responseQueue, calculator.multiply(request), correlationID);
				break;
			default:
				LOGGER.warning("Unknown operation: " + operation);
				break;
		}
	}

	private void sendResponse(Destination responseQueue, BigDecimal result, String correlationId) throws JMSException {
		LOGGER.info("Result: " + result);
		if(responseQueue instanceof TemporaryQueue) context.createProducer().send(responseQueue, result);
		else context.createProducer().send(responseQueue, jmsMessage(responseQueue, result, correlationId));
	}

	private Message jmsMessage(Destination responseQueue, BigDecimal result, String correlationId) throws JMSException {
		Message msg = context.createObjectMessage(result);
		msg.setJMSReplyTo(responseQueue);
		msg.setJMSCorrelationID(correlationId);
		return msg;
	}
}
