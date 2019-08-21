package clients;

import java.io.IOException;
import javax.annotation.Resource;
import javax.jms.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
 
@WebServlet(urlPatterns = "/MessageServlet")
public class MessageServlet extends HttpServlet {
 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

@Resource(mappedName = "java:/queue/testQueue2")
 private Queue queue;
 
 @Resource(mappedName = "java:/ConnectionFactory")
 private QueueConnectionFactory queueConnectionFactory;
 
 @Override
 protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
     QueueConnection queueConnection = null;
     try {
         queueConnection = queueConnectionFactory.createQueueConnection();
         queueConnection.start();
         QueueSession queueSession = queueConnection.createQueueSession(false,
                 Session.AUTO_ACKNOWLEDGE);
         QueueSender sender = queueSession.createSender(queue);
 
         TextMessage msg = queueSession.createTextMessage();
         msg.setText("A message from MessageServlet");
         msg.setStringProperty("name", "MessageServlet");
 
         sender.send(msg);
     } catch (JMSException e) {
         throw new RuntimeException(e);
     } finally {
         try {
             if (queueConnection != null) {
                 queueConnection.close();
             }
         } catch (JMSException e) { //ignore
         }
     }
 }
}
