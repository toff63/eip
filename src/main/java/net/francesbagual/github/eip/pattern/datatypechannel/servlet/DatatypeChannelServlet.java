package net.francesbagual.github.eip.pattern.datatypechannel.servlet;

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

import net.francesbagual.github.eip.pattern.datatypechannel.message.PriceQuote;
import net.francesbagual.github.eip.pattern.datatypechannel.message.Query;

@JMSDestinationDefinitions(
		value = {
				@JMSDestinationDefinition(
						name = "jms/queue/query",
						interfaceName = "javax.jms.Queue",
						destinationName = "query"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/pricequote",
						interfaceName = "javax.jms.Queue",
						destinationName = "pricequote"
				)
		})
@WebServlet("/datatypechannel")
public class DatatypeChannelServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/query")
	private Queue queryQueue;

	@Resource(lookup = "jms/queue/pricequote")
	private Queue priceQuoteQueue;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h1>Quickstart: Example demonstrates the use of <strong>JMS 2.0</strong> and <strong>EJB 3.2 Message-Driven Bean</strong> in WildFly 8.</h1>");
		try {
			out.write("<p>Sending Query messages to <em>" + queryQueue + "</em></p>");
			context.createProducer().send(queryQueue, new Query("My query"));
			out.write("<p>Sending Price quote messages to <em>" + priceQuoteQueue + "</em></p>");
			context.createProducer().send(priceQuoteQueue, new PriceQuote("GOOG"));
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
