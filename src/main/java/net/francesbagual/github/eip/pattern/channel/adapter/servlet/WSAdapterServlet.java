package net.francesbagual.github.eip.pattern.channel.adapter.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.Queue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@JMSDestinationDefinitions(
		value = {
				@JMSDestinationDefinition(
						name = "jms/queue/hellows",
						interfaceName = "javax.jms.Queue",
						destinationName = "hellows"
				)
		})
@WebServlet("/wsadapter")
public class WSAdapterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Resource(lookup = "jms/queue/hellows")
	private Queue hellowsQueue;

	@Inject
	private JMSContext context;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("Channel Adapters help to integrate existing application to a messaging system. It can be through UI (can be html, json, xml ...), or API (like EJB) or database");
		out.write("<br />");
		out.write("Here we are triggering a webservice through messaging using an adapter");
		context.createProducer().send(hellowsQueue, "hello");
		out.write("<p><i>Go to your WildFly Server console or Server log to see the result of messages processing</i></p>");

	}
}
