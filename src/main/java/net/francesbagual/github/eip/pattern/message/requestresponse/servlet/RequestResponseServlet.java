package net.francesbagual.github.eip.pattern.message.requestresponse.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
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
				),
				@JMSDestinationDefinition(
						name = "jms/queue/calculatorresponse",
						interfaceName = "javax.jms.Queue",
						destinationName = "calculatorresponse"
				)
		})
@WebServlet("/requestresponse")
public class RequestResponseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Resource(lookup = "jms/queue/calculator")
	private Queue calculatorRequestQueue;

	@Resource(lookup = "jms/queue/calculatorresponse")
	private Queue calculatorResponseQueue;

	@Inject
	private JMSContext context;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("Request response example");
		out.write("<br />");
		out.write("Sending an addition of 1 + 1 using temporary queue: the temporary queue on live until the connection is closed. There is no guaranteed delivery with this technique.");
		TemporaryQueue responseQueue = context.createTemporaryQueue();
		context.createProducer().send(calculatorRequestQueue, messageWithReplyTo(responseQueue, create1Plus1Message()));
		BigDecimal result = context.createConsumer(responseQueue).receiveBody(BigDecimal.class);
		out.write("<p><i>Result received from service: " + result + "</i></p>");
		out.write("Sending an addition of 1 + 1 using a dedicated queue with correlationID");
		String messageId = UUID.randomUUID().toString();
		context.createProducer().send(calculatorRequestQueue, messageWithReplyTo(calculatorResponseQueue, messageId, create1Plus1Message()));
		Message response = context.createConsumer(calculatorResponseQueue).receive(1000L);
		try {
			if(response.getJMSCorrelationID().equals(messageId)) out.write("<p><i>Result received from service: " + response.getBody(BigDecimal.class) + "</i></p>");
			else out.write("Received the response from another request (wrong correlation id)");
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	private Message messageWithReplyTo(Queue responseQueue, String messageId, CalculatorRequestMessage request)  {
		Message msg = messageWithReplyTo(responseQueue, request);
		try {
			msg.setJMSMessageID(messageId);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return msg;
	}

	private Message messageWithReplyTo(Queue responseQueue, CalculatorRequestMessage request)  {
		Message msg = context.createObjectMessage(request);
		try {
			msg.setJMSReplyTo(responseQueue);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return msg;
	}

	private CalculatorRequestMessage create1Plus1Message() {
		CalculatorRequest request = new CalculatorRequest(Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(1)));
		Map<String, Object> headers = new HashedMap<String, Object>();
		headers.put("operation", "add");
		return new CalculatorRequestMessage(headers, request);
	}
}
