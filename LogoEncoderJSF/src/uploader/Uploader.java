package uploader;

import java.io.File;

import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;

import dao.LogoEncoderCRUD;

@ManagedBean
@RequestScoped
public class Uploader {
	
	private String name;
	private String encodeString;
	private int maxChange=6;
	
	@ManagedProperty(value = "#{loginBean.username}")
	private String username;

	@Resource(mappedName = "java:/queue/LogoEncoderMDBqueue")
	private Queue queue;

	@Resource(mappedName = "java:/ConnectionFactory")
	private QueueConnectionFactory queueConnectionFactory;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEncodeString() {
		return encodeString;
	}

	public void setEncodeString(String encodeString) {
		this.encodeString = encodeString;
	}
		 
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getMaxChange() {
		return maxChange;
	}

	public void setMaxChange(int maxChange) {
		this.maxChange = maxChange;
	}

	public String go() {

		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
		String location = (String) req.getAttribute("location");
		String origIndvTag = (String) req.getAttribute("origIndvTag");
		String logoIndvTag = (String) req.getAttribute("logoIndvTag");
		
		System.out.println("maxChange is: "+maxChange);
		System.out.println("origIndvTag is: "+origIndvTag);
		System.out.println("logoIndvTag is: "+logoIndvTag);
		
		boolean encode = true;
		
    	try{
    		
     	   File orig =new File(location+origIndvTag+"_orig.jpg");
     	   File logo =new File(location+logoIndvTag+"_logo.jpg");
     	   String newOrigName = location+username+"\\"+name+"_orig.jpg";
     	   String newLogoName = location+username+"\\"+name+"_logo.jpg";
     	   
     	   System.err.println("Moving "+location+origIndvTag+"_orig.jpg to "+newOrigName);
     	   System.err.println("Moving "+location+logoIndvTag+"_logo.jpg to "+newLogoName);
     	   
     	   if((orig.renameTo(new File(newOrigName)))&&(logo.renameTo(new File(newLogoName)))){
     		System.out.println("File moved successfully!");
     	   }else{
     		System.err.println("File failed to move!");
     	   }
  
     	}catch(Exception e){
     		e.printStackTrace();
     	}
		
		if(encodeString.equals("false")){
			encode=false;
		}
		
		System.out.println("Calling encoder...");
		try {
			sendMsg(encode,name,username);
		} catch (JMSException e) {
			e.getMessage();
			e.printStackTrace();
		}
			
		LogoEncoderCRUD crud = new LogoEncoderCRUD();
		crud.submitCurrJob(username, name);
		
		return "thankyou";

	}
	
	
	public void sendMsg(boolean encode,String imageID,String username) throws JMSException{

		QueueConnection queueConnection = null;

		try {
			queueConnection = queueConnectionFactory.createQueueConnection();
			queueConnection.start();
			QueueSession queueSession = queueConnection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
			QueueSender sender = queueSession.createSender(queue);

			TextMessage msg = queueSession.createTextMessage();

			
			if(encode){
				msg.setText("");
				msg.setStringProperty("username", username);
				msg.setBooleanProperty("encode",true);
				msg.setStringProperty("ID",imageID);
				msg.setStringProperty("logoFile",imageID+"_logo.jpg");
				msg.setStringProperty("origFile",imageID+"_orig.jpg");
				msg.setIntProperty("maxChange",maxChange);
				msg.setFloatProperty("maxPercent",1);
			}

			else{
				msg.setText("");
				msg.setBooleanProperty("encode",false);
				msg.setStringProperty("ID",imageID);
				msg.setStringProperty("origFile",imageID+"_orig.jpg");
				msg.setIntProperty("maxChange",maxChange);
				msg.setFloatProperty("maxPercent",1);
			}

			sender.send(msg);

		} catch (JMSException e) {
			e.getMessage();
			e.printStackTrace();
		}
		finally
		{
		   if (queueConnection != null)
		   {
				queueConnection.close();
		   }
		}
	}
}
