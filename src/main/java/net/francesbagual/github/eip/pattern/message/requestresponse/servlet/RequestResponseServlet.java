package net.francesbagual.github.eip.pattern.message.requestresponse.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

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

import net.francesbagual.github.eip.pattern.message.requestresponse.message.CalculatorRequest;
import net.francesbagual.github.eip.pattern.message.requestresponse.message.CalculatorRequestMessage;

import org.apache.commons.collections4.map.HashedMap;

@JMSDestinationDefinitions(
		value = {
				@JMSDestinationDefinition(
						name = "jms/queue/calculator",
						interfaceName = "javax.jms.Queue",
						destinationName = "calculator"
				)
		})
@WebServlet("/requestresponse")
public class RequestResponseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Resource(lookup = "jms/queue/calculator")
	private Queue calculatorRequestQueue;

	@Inject
	private JMSContext context;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("Request response example");
		out.write("<br />");
		out.write("Sending an addition of 1 + 1 using temporary queue: the temporary queue on live until the connection is closed. There is no guaranteed delivery with this technique.");
		context.createProducer().send(calculatorRequestQueue, create1Plus1Message(context.createTemporaryQueue()));
		out.write("<p><i>Go to your WildFly Server console or Server log to see the result of messages processing</i></p>");

	}

	private CalculatorRequestMessage create1Plus1Message(Queue responseQueue){
		CalculatorRequest request = new CalculatorRequest(Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(1)));
		Map<String, Object> headers  = new HashedMap<String, Object>();
		headers.put("operation", "add");
		headers.put("response", responseQueue);
		return new CalculatorRequestMessage(headers, request);
	}
}
