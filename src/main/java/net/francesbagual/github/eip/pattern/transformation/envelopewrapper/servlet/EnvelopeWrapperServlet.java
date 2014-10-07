package net.francesbagual.github.eip.pattern.transformation.envelopewrapper.servlet;

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
						name = "jms/queue/recipient",
						interfaceName = "javax.jms.Queue",
						destinationName = "recipient"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/someothermdb",
						interfaceName = "javax.jms.Queue",
						destinationName = "someothermdb"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/unwrapper",
						interfaceName = "javax.jms.Queue",
						destinationName = "unwrapper"
				),
				@JMSDestinationDefinition(
						name = "jms/queue/wrapper",
						interfaceName = "javax.jms.Queue",
						destinationName = "wrapper"
				),
		})
@WebServlet("/transformation/envelopewrapper")

public class EnvelopeWrapperServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;

		@Inject
		private JMSContext context;

		@Resource(lookup = "jms/queue/wrapper")
		private Queue wrapper;
		
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setContentType("text/html");
			PrintWriter out = resp.getWriter();
			out.write("<h1>Content based Router</h1>");
			try {
				out.write("<p>Sending a message to a wrapper so messages going into the integration system are all of envelope type. Then it is unwrapped and message appears in the console</p>");
				context.createProducer().send(wrapper, "Hello Envelope Wrapper pattern");
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
