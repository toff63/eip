package net.francesbagual.github.eip.pattern.transformation.claincheck.mdb;


import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import net.francesbagual.github.eip.pattern.transformation.claincheck.ClaimCheckMessage;

@MessageDriven(name = "EchoClaimCheckMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/claimcheckecho"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class EchoClaimCheckMDB  implements MessageListener{

	private final static Logger LOGGER = Logger.getLogger(EchoClaimCheckMDB.class.toString());

	@Override
	public void onMessage(Message receivedMessage) {
		try {
			if (receivedMessage instanceof ObjectMessage) {
				ClaimCheckMessage claimCheckMessage = ((ObjectMessage) receivedMessage).getBody(ClaimCheckMessage.class);
				LOGGER.info("EchoMDB received " + claimCheckMessage.toString());
			} else {
				LOGGER.info(receivedMessage.toString());
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
	
}

