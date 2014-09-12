package net.francesbagual.github.eip.pattern.router.agregator.mdb;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;

@JMSDestinationDefinitions(
		value = {
				@JMSDestinationDefinition(
						name = "jms/queue/agregator",
						interfaceName = "javax.jms.Queue",
						destinationName = "agregator"
				)
		})
@WebServlet("/router/agregator")
public class AgregatorRouterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private JMSContext context;

	@Resource(lookup = "jms/queue/agregator")
	private Queue router;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h1>Content based Router</h1>");
		try {
			out.write("<p>Sending several messages to the agregator router. It should send 2 messages to the echo worker</p>");
			Collection<Message> messages = createMessages(Arrays.asList("\"The path of the righteous man is beset on all sides by the iniquities of the selfish and the tyranny of evil men. Blessed is he who, in the name of charity and good will, shepherds the weak through the valley of darkness, for he is truly his brother's keeper and the finder of lost children. And I will strike down upon thee with great".split(" ")), Arrays.asList("vengeance and furious anger those who would attempt to poison and destroy My brothers. And you will know My name is the Lord when I lay My vengeance upon thee.".split(" ")));
			for(Message msg:messages){
				context.createProducer().send(router, msg);	
			}
			out.write("<p><i>Go to your WildFly Server console or Server log to see the result of messages processing</i></p>");
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private Collection<Message> createMessages(List<String> firstListOfMessages, List<String> secondListOfMessages) {
		try {
			int firstSize = firstListOfMessages.size();
			String firstCorrelationId = UUID.randomUUID().toString();
			int secondSize = secondListOfMessages.size();
			String secondCorrelationId = UUID.randomUUID().toString();
			ImmutableMap<List<String>, String> correlationIdByList = ImmutableMap.of(firstListOfMessages, firstCorrelationId, secondListOfMessages, secondCorrelationId);
			Collection<Message> allMessages = new ArrayList<Message>();
			for (int i = 0; i < Math.min(firstSize, secondSize); i++) {
				allMessages.add(createMessage(firstListOfMessages, firstSize, firstCorrelationId, i));
				allMessages.add(createMessage(secondListOfMessages, secondSize, secondCorrelationId, i));
			}
			if(firstSize == secondSize) return allMessages;
			List<String> largerMessage = getLargerList(firstListOfMessages, secondListOfMessages);
			for(int i = Math.min(firstSize, secondSize); i < largerMessage.size(); i++){
				allMessages.add(createMessage(firstListOfMessages, largerMessage.size(), correlationIdByList.get(largerMessage), i));
			}
			System.out.println("Number of message to agregate: " + allMessages.size());
			 return allMessages;
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<String> getLargerList(List<String> firstListOfMessages, List<String> secondListOfMessages){
		return firstListOfMessages.size() > secondListOfMessages.size() ? firstListOfMessages : secondListOfMessages;
	}
	
	private Message createMessage(List<String> firstListOfMessages, int firstSize, String firstCorrelationId, int i) throws JMSException {
		Message msg1 = context.createTextMessage(firstListOfMessages.get(i));
		msg1.setJMSCorrelationID(firstCorrelationId);
		msg1.setLongProperty("numberOfMessages", firstSize);
		return msg1;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
