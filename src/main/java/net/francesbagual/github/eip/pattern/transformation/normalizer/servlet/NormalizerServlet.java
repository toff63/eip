package net.francesbagual.github.eip.pattern.transformation.normalizer.servlet;

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
						name = "jms/queue/normalizerconsumer",
						interfaceName = "javax.jms.Queue",
						destinationName = "normalizerconsumer"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/normalizer",
						interfaceName = "javax.jms.Queue",
						destinationName = "normalizer"
				)
		})
@WebServlet("/transformation/normalizer")
public class NormalizerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/normalizer")
	private Queue normalizer;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h1>Content based Router</h1>");
		try {
			out.write("<p>Normalizer ensure that all messages in the system are of the same type. You transform messages at the edges.</p>");
			context.createProducer().send(normalizer, "This message will be normalizer and un-normalized before getting print.");
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
