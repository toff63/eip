package net.francesbagual.github.eip.pattern.channel.adapter.ws;

@Default
public class HelloWorldImpl implements HelloWorldAPI {
	public String createHelloMessage(String name) {
		return "Hello " + name + "!";
	}

}
