package net.francesbagual.github.eip.pattern.endpoint.gateway.hello;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.json.Json;

import net.francesbagual.github.eip.pattern.channel.adapter.ws.Default;

import org.apache.http.client.fluent.Request;

@Default
public class RestHelloWorldClient implements HelloWorldGateway {

	private final static Logger LOGGER = Logger.getLogger(RestHelloWorldClient.class.toString());

	@Override
	public String hello() {
		String result = "";
		
		try {
			InputStream in = Request.Get("http://localhost:8080/eip/rest/hello").addHeader("Accept", "application/json")
					.connectTimeout(1000)
					.socketTimeout(1000)
					.execute().returnContent().asStream();
			result = Json.createReader(in).readObject().getString("result");
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return result;
	}

}
