package net.francesbagual.github.eip.pattern.transformation.claincheck.servlet;

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

import net.francesbagual.github.eip.pattern.transformation.claincheck.ClaimCheckMessage;
import net.francesbagual.github.eip.pattern.transformation.claincheck.mdb.User;

@JMSDestinationDefinitions(
		value = {
				@JMSDestinationDefinition(
						name = "jms/queue/claimcheckgreeting",
						interfaceName = "javax.jms.Queue",
						destinationName = "claimcheckgreeting"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/claimcheckfilter",
						interfaceName = "javax.jms.Queue",
						destinationName = "claimcheckfilter"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/claimcheckenricher",
						interfaceName = "javax.jms.Queue",
						destinationName = "claimcheckenricher"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/claimcheckecho",
						interfaceName = "javax.jms.Queue",
						destinationName = "claimcheckecho"
				)
		})
@WebServlet("/transformation/claimcheck")
public class ClaimcheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/claimcheckfilter")
	private Queue claimcheckfilter;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h1>Content based Router</h1>");
		try {
			out.write("<p>Claim check pattern will first remove part of the message, send it to a worker and when the system get the message back it will add back the part removed.</p>");
			context.createProducer().send(claimcheckfilter, new ClaimCheckMessage(new User(1L, "John", 16), ""));
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
