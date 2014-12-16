package net.francesbagual.github.eip.pattern.endpoint.gateway;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.francesbagual.github.eip.pattern.channel.adapter.ws.Default;
import net.francesbagual.github.eip.pattern.endpoint.gateway.mapper.DomainToJson;

@Path("/greeting")
public class RestGreeting {

	@Inject
	@Default
	GreetingAPI greetingService;
	
	@Inject
	DomainToJson mapper;

	@GET
	@Path("/{name}")
	@Produces({ "application/json" })
	public JsonObject greet(@PathParam("name") @DefaultValue("World") String name) {
		return mapper.map(greetingService.greet(name));
	}

}
