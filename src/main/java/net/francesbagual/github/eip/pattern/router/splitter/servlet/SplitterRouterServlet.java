package net.francesbagual.github.eip.pattern.router.splitter.servlet;

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
						name = "jms/queue/splitter",
						interfaceName = "javax.jms.Queue",
						destinationName = "splitter"
				)
		})
@WebServlet("/router/splitter")
public class SplitterRouterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/splitter")
	private Queue router;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h1>Content based Router</h1>");
		try {
			out.write("<p>Sending one message to the splitter router. It should split the message words and send them to the echo worker</p>");
			context.createProducer().send(router, "The path of the righteous man is beset on all sides by the iniquities of the selfish and the tyranny of evil men. Blessed is he who, in the name of charity and good will, shepherds the weak through the valley of darkness, for he is truly his brother's keeper and the finder of lost children. And I will strike down upon thee with great vengeance and furious anger those who would attempt to poison and destroy My brothers. And you will know My name is the Lord when I lay My vengeance upon thee.");
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
