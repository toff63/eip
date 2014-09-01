package net.francesbagual.github.eip.pattern.router.dynamic.servlet;

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
						name = "jms/queue/dynamicrouter",
						interfaceName = "javax.jms.Queue",
						destinationName = "dynamicrouter"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/dynamicgreeting",
						interfaceName = "javax.jms.Queue",
						destinationName = "dynamicgreeting"
				)
		})
@WebServlet("/router/dynamicrouter")
public class ContentBasedRouterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/dynamicrouter")
	private Queue router;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h1>Content based Router</h1>");
		try {
			out.write("<p>Sending one message to greeting and another one to echo using the router. They should be routed properly based on the content. Here a prefix in the text message</p>");
			context.createProducer().send(router, "greeting: Hello World");
			context.createProducer().send(router, "echo: Reply me something useful :)");
			out.write("<p><i>Go to your WildFly Server console or Server log to see the result of messages processing</i></p>");
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
