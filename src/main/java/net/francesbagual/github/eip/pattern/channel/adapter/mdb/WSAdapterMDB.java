package net.francesbagual.github.eip.pattern.channel.adapter.mdb;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.http.client.fluent.Request;

@MessageDriven(name = "WSAdapterMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/hellows"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class WSAdapterMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(WSAdapterMDB.class.toString());

	@Override
	public void onMessage(Message message) {
		LOGGER.info(transform(callWebservice()));
	}

	private String callWebservice() {
		String result = "";
		try {
			result = Request.Get("http://localhost:8080/eip/rest/hello").addHeader("Accept", "application/xml")
					.connectTimeout(1000)
					.socketTimeout(1000)
					.execute().returnContent().asString();
			return result;
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return result;
	}

	private String transform(String wsResponse) {
		return wsResponse.replace("World", "Universe");
	}
}
