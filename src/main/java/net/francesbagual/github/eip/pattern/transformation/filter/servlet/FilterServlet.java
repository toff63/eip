package net.francesbagual.github.eip.pattern.transformation.filter.servlet;

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

import net.francesbagual.github.eip.pattern.transformation.filter.mdb.User;

@JMSDestinationDefinitions(
		value = {
				@JMSDestinationDefinition(
						name = "jms/queue/filter",
						interfaceName = "javax.jms.Queue",
						destinationName = "filter"
				)
		})
@WebServlet("/transformation/filter")
public class FilterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/filter")
	private Queue filter;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h1>Content based Router</h1>");
		try {
			out.write("<p>Sending a message to a filter will remove data from the message to match next system expected message data..</p>");
			context.createProducer().send(filter, new User(123L, "John", 25));
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
