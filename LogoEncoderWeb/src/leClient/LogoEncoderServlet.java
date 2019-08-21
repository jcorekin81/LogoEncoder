package leClient;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/pages/upload.html")
@MultipartConfig(location="C:\\JBOSS\\jbpm\\jboss-as-7.1.1.Final\\", fileSizeThreshold=1024*1024, maxFileSize=1024*1024*5, maxRequestSize=1024*1024*5*5)
public class LogoEncoderServlet extends HttpServlet {

	@Resource(mappedName = "java:/queue/LogoEncoderMDBqueue")
	private Queue queue;

	@Resource(mappedName = "java:/ConnectionFactory")
	private QueueConnectionFactory queueConnectionFactory;

	/**
	 * 
	 */
	private static final long serialVersionUID = -475804172968072540L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/WEB-INF/pages/uploader.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");

		String name = req.getParameter("name");
		String encodeString = req.getParameter("encode");
		boolean encode = true;

		if(encodeString.equals("false")){
			encode=false;
		}

		Part origPart = req.getPart("origFile");
		origPart.write(name+"_orig.jpg");


		Part logoPart = null;

		if(encode){
			logoPart = req.getPart("logoFile");
			logoPart.write(name+"_logo.jpg");
		}

		QueueConnection queueConnection = null;
		try {
			queueConnection = queueConnectionFactory.createQueueConnection();
			queueConnection.start();
			QueueSession queueSession = queueConnection.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			QueueSender sender = queueSession.createSender(queue);

			TextMessage msg = queueSession.createTextMessage();

			if(encode){
				msg.setText("encode logo mvlogo.bmp into photo:MVTeamPhoto.jpg, maxChange is 6, max percent 1");
				msg.setBooleanProperty("encode",true);
				msg.setStringProperty("logoFile",name+"_logo.jpg");
				msg.setStringProperty("origFile",name+"_orig.jpg");
				msg.setIntProperty("maxChange",6);
				msg.setFloatProperty("maxPercent",1);
			}

			else{
				msg.setText("decode photo:MVTeamPhoto_encoded.jpg, maxChange is 6, max percent 1");
				msg.setBooleanProperty("encode",false);
				msg.setStringProperty("origFile",name+"_orig.jpg");
				msg.setIntProperty("maxChange",6);
				msg.setFloatProperty("maxPercent",1);
			}
			
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