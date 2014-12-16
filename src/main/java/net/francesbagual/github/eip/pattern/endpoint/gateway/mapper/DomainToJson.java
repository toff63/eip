package net.francesbagual.github.eip.pattern.endpoint.gateway.mapper;

import javax.json.Json;
import javax.json.JsonObject;

public class DomainToJson {

	public JsonObject map(String greet){
		return Json.createObjectBuilder()
				.add("result", greet)
				.build();
	}
}
