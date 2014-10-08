package net.francesbagual.github.eip.pattern.transformation.enricher.servlet;

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
						name = "jms/queue/enricher",
						interfaceName = "javax.jms.Queue",
						destinationName = "enricher"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/echouser",
						interfaceName = "javax.jms.Queue",
						destinationName = "echouser"
				)
		})
@WebServlet("/transformation/enricher")
public class EnricherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/enricher")
	private Queue enricher;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h1>Content based Router</h1>");
		try {
			out.write("<p>Sending a message to an enricher, will make it add information to comply with other system expectation. For example one system can send the user id while the other system expect the user object in the message. In order to have it working, you add an enricher component in the middle that will retrieve the user information.</p>");
			context.createProducer().send(enricher, "123");
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
