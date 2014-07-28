package net.francesbagual.github.eip.pattern.message.requestresponse.mdb;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

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

	@Override
	public void onMessage(Message receivedMessage) {
		try {
			if (receivedMessage instanceof ObjectMessage) {
				ObjectMessage msg = (ObjectMessage) receivedMessage;
				CalculatorRequestMessage calculatorRequestMessage = (CalculatorRequestMessage) msg.getObject();
				String operation = (String) calculatorRequestMessage.headers().get("operation");
				CalculatorRequest request = (CalculatorRequest) calculatorRequestMessage.body();
				switch (operation) {
					case "add":
						sendResponse(calculator.add(request));
						break;
					case "mult":
						sendResponse(calculator.multiply(request));
						break;
					default:
						LOGGER.warning("Unknown operation: " + operation);
						break;
				}
			} else {
				LOGGER.warning("Message of wrong type: " + receivedMessage.getClass().getName());
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	private void sendResponse(BigDecimal result) {
		LOGGER.info("Result: " + result);
	}
}
