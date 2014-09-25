package net.francesbagual.github.eip.pattern.router.routingslip.servlet;

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
						name = "jms/queue/routerslip",
						interfaceName = "javax.jms.Queue",
						destinationName = "routerslip"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/routersliphello",
						interfaceName = "javax.jms.Queue",
						destinationName = "routersliphello"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/routerslipfourtytwo",
						interfaceName = "javax.jms.Queue",
						destinationName = "routerslipfourtytwo"
				)
		})
@WebServlet("/router/routerslip")
public class RoutingSlipServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/routerslip")
	private Queue router;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h1>Content based Router</h1>");
		try {
			out.write("<p>Sending one message to a Router slip. It will define a path going through 2 MDB and ending in an Echo MDB</p>");
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
