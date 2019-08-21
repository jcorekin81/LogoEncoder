package clients;

import java.io.IOException;
import javax.annotation.Resource;
import javax.jms.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
 
@WebServlet(urlPatterns = "/DecodeClientServlet")
public class DecodeClientServlet extends HttpServlet {
 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

@Resource(mappedName = "java:/queue/LogoEncoderMDBqueue")
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

         msg.setText("decode photo:MVTeamPhoto_encoded.jpg, maxChange is 6, max percent 1");
         msg.setBooleanProperty("encode",false);
         msg.setStringProperty("origFile","MVTeamPhoto_encoded.jpg");
         msg.setIntProperty("maxChange",6);
         msg.setFloatProperty("maxPercent",1);
 
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
