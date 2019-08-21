package beans;

import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
 
public class DemoMDB implements MessageListener {
 Logger logger = Logger.getLogger("test");
 
 public void onMessage(Message msg) {
     try {
         String name = msg.getStringProperty("name");
         logger.info("Received msg " + msg + ", from " + name);
         System.out.println("Received msg " + msg + ", from " + name);
     } catch (JMSException e) {
         throw new RuntimeException(e);
     }
 }
}
