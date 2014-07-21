package net.francesbagual.github.eip.pattern.channel.adapter.ws;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/hello")
public class RestHelloWorld {

	@Inject
	@Default
	HelloWorldAPI helloService;

	@GET
	@Path("/")
	@Produces({ "application/json" })
	public JsonObject getHelloWorldJSON() {
		return Json.createObjectBuilder()
				.add("result", helloService.createHelloMessage("World"))
				.build();
	}

	@GET
	@Path("/")
	@Produces({ "application/xml" })
	public String getHelloWorldXML() {
		return "<xml><result>" + helloService.createHelloMessage("World") + "</result></xml>";
	}
}
