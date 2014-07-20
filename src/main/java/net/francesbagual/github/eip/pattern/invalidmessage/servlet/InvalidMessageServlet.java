package net.francesbagual.github.eip.pattern.invalidmessage.servlet;

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

import net.francesbagual.github.eip.pattern.datatypechannel.message.Query;

@JMSDestinationDefinitions(
		value = {
				@JMSDestinationDefinition(
						name = "jms/queue/pricequoteinvalidmessage",
						interfaceName = "javax.jms.Queue",
						destinationName = "pricequoteinvalidmessage"
				)
		})
@WebServlet("/invalidmessage")
public class InvalidMessageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/pricequoteinvalidmessage")
	private Queue priceQuoteQueue;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h1>Invalid message channel</h1>");
		try {
			out.write("<p>Sending a query message to the price quote queue. It should generates a message in the invalid message queue</p>");
			context.createProducer().send(priceQuoteQueue, new Query("My query"));
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
