package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;

import dao.LogoEncoderCRUD;


import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SuppressWarnings("unused")
@Path("/file")
@Stateless
@Named
public class UploadFileService {
 

	// @Resource(lookup = "java:/ConnectionFactory")
    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
 
    @Resource(mappedName = "java:/queue/LogoEncoderMDBqueue")
    private Queue testQueue;
 

    @GET
    @Path("/helloworld")
    public String helloworld() {
        return "Hello World!";
    }
	
    @POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	public Response uploadFile(@MultipartForm FileUploadForm form) {
 
		String location = "C:\\LogoEncoderImages\\";
		String name = form.getName();
		String username = form.getUsername();
		String password = form.getPassword();
		
		if(!authenticate(username,password)){
			return Response.status(401)
				    .entity("Invalid username and/or passord received, processing denied.").build();
		}
		
		
		String fileNameOrig = location+username+"\\"+name+"_orig.jpg";
		String fileNameLogo = location+username+"\\"+name+"_logo.jpg";
		boolean encode = form.getEncode();
		int maxChange=form.getMaxChange();
		
 
		try {
			writeFile(form.getDataOrig(), fileNameOrig);
			byte[] tempLogo = form.getDataLogo();
			if(tempLogo!=null&&tempLogo.length>2){
				writeFile(form.getDataLogo(), fileNameLogo);
				sendMsg(encode, name,false,maxChange,username);
			}
			else{
				File logo =new File(location+username+"\\default_logo.jpg");
				if(logo.isFile()){
					fileNameLogo = location+username+"\\"+"default_logo.jpg";
					sendMsg(encode, name,true,maxChange,username);
					return Response.status(200)
						    .entity("uploadFile is called, Uploaded file name : " + fileNameOrig).build();
				}
				return Response.status(500)
					    .entity("No logo uploaded and no default available").build();
			}
		} catch (IOException e) {
 
			e.printStackTrace();
		}
 
		System.out.println("Done");
 
		return Response.status(200)
		    .entity("uploadFiles are called, Uploaded file name : " + fileNameOrig+" & "+fileNameLogo).build();
	}
	
	// save to somewhere
	private void writeFile(byte[] content, String filename) throws IOException {
 
		File file = new File(filename);
 
		if (!file.exists()) {
			file.createNewFile();
		}
 
		FileOutputStream fop = new FileOutputStream(file);
 
		fop.write(content);
		fop.flush();
		fop.close();
 
	}
	
    public String sendMsg(boolean encode,String name,boolean useDefault,int maxChange,String username) {
        Connection connection = null;
        Session session = null;
        try {
 
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final MessageProducer publisher = session.createProducer(testQueue);
 
            final TextMessage msg = session.createTextMessage();
            
            
			if(encode){
				msg.setText("");
				msg.setStringProperty("username", username);
				msg.setBooleanProperty("encode",true);
				msg.setStringProperty("ID",name);
				if(useDefault){
					msg.setStringProperty("logoFile","default_logo.jpg");
				}
				else{
					msg.setStringProperty("logoFile",name+"_logo.jpg");
				}
				msg.setStringProperty("origFile",name+"_orig.jpg");
				msg.setIntProperty("maxChange",maxChange);
				msg.setFloatProperty("maxPercent",1);
			}

			else{
				msg.setText("");
				msg.setStringProperty("username", username);
				msg.setBooleanProperty("encode",false);
				msg.setStringProperty("ID",name);
				msg.setStringProperty("origFile",name+"_orig.jpg");
				msg.setIntProperty("maxChange",maxChange);
				msg.setFloatProperty("maxPercent",1);
			}
            
            
            publisher.send(msg);
 
        } catch (final JMSException exc) {
        } finally {
            try {
                if (null != session) {
                    session.close();
                }
            } catch (JMSException exc) {
            }
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (JMSException exc) {
            }
        }
        return "test";
    }
    
    private boolean authenticate(String username,String password){
    	LogoEncoderCRUD crud = new LogoEncoderCRUD();
    	
    	String dbPassword =crud.retrievePassword(username);
    	
    	if(dbPassword!=null&&dbPassword.equals(password)){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
}