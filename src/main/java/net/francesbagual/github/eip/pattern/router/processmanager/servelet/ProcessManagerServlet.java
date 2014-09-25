package net.francesbagual.github.eip.pattern.router.processmanager.servelet;

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
						name = "jms/queue/processmanagerhello",
						interfaceName = "javax.jms.Queue",
						destinationName = "processmanagerhello"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/processmanagerfourtytwo",
						interfaceName = "javax.jms.Queue",
						destinationName = "processmanagerfourtytwo"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/processmanager",
						interfaceName = "javax.jms.Queue",
						destinationName = "processmanager"
				)
		})

@WebServlet("/router/processmanager")
public class ProcessManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/processmanager")
	private Queue router;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h1>Process Manager Router</h1>");
		try {
			out.write("<p>Sending one message to a Process Manager Router. It will make the message go through hello, then 42 and finaly to echo so we can see the result in the console. The message always get back to the process manager, so it decides which destination is next.</p>");
			context.createProducer()
					.send(router, "what is the meaning of Life?");
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
