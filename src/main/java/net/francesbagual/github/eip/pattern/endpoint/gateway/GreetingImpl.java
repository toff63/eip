package net.francesbagual.github.eip.pattern.endpoint.gateway;


import javax.inject.Inject;

import net.francesbagual.github.eip.pattern.channel.adapter.ws.Default;
import net.francesbagual.github.eip.pattern.endpoint.gateway.hello.HelloWorldGateway;

@Default
public class GreetingImpl implements GreetingAPI {

	@Inject
	@Default
	private HelloWorldGateway helloWorld;
	
	@Override
	public String greet(String name) {
		return helloWorld.hello().replaceAll("World", name);
	}

}
