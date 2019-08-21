package beans;

import ij.IJ;
import ij.ImagePlus;

import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import mail.SendMailSSL;

import core.Decoder;
import core.Encoder;
import core.Utils;
import core.Values;


@MessageDriven(name = "LogoEncoderMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/LogoEncoderMDBqueue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})
public class LogoEncoderMDB implements MessageListener {

	private String baseDir = "C:\\LogoEncoderImages\\";
	
	private final static Logger logger = Logger.getLogger(LogoEncoderMDB.class
			.toString());

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void onMessage(Message msg) {
		SendMailSSL mail = new SendMailSSL();
		String id= null;
		
		try {
			logger.info("Received msg " + msg);       
			System.out.println("username: "+msg.getStringProperty("username"));
			System.out.println("ID: "+msg.getStringProperty("ID"));
			System.out.println("encode: "+msg.getBooleanProperty("encode"));
			System.out.println("logo file name: "+msg.getStringProperty("logoFile"));
			System.out.println("original file name: "+msg.getStringProperty("origFile"));
			System.out.println("maxChange: "+msg.getIntProperty("maxChange"));
			System.out.println("maxPercent: "+msg.getFloatProperty("maxPercent"));

			Utils util = new Utils();
			Encoder enc = new Encoder(util);
			Decoder dec = new Decoder(util);
			Values vals = util.getVals();

			//If there are no commands throw an exception
			if(!(msg.getPropertyNames().hasMoreElements())){
				try {
					throw new CommandsException("The message had no commands.");
				} catch (CommandsException e) {
					e.getMessage();
					e.printStackTrace();
				}
			}


			vals.setMaxChange(msg.getIntProperty("maxChange"));
			logger.info("maxChange is: "+vals.getMaxChange());       

			vals.setMaxPercent(msg.getFloatProperty("maxPercent"));
			logger.info("maxPercent is: "+vals.getMaxPercent());       

			if(msg.getBooleanProperty("encode")){
 
				id = msg.getStringProperty("ID");
				String username = msg.getStringProperty("username");
				baseDir = baseDir + username +"\\";
				String logoName = baseDir+msg.getStringProperty("logoFile");
				String origName = baseDir+msg.getStringProperty("origFile");
				String encodedName = origName.substring(0,origName.length()-9)+"_encoded.jpg";
				String decodedName = origName.substring(0,origName.length()-9)+"_decoded.jpg";
				
				System.out.println("Opening: " + logoName);
				
				ImagePlus logo = IJ.openImage(logoName);

				System.out.println("Opening: "+ origName);
				
				ImagePlus orig = IJ.openImage(origName);;
		
				System.out.println("orig.getFileInfo: ");
				System.out.println(orig.getFileInfo());

				System.out.println("Going to encode: "+origName);				
				ImagePlus output=enc.addFiles(logo,orig);

				util.writeFileSilent(output,encodedName);
				
				System.out.println("Going to decode: "+encodedName);				
				
				output=dec.decodeNoOriginal(encodedName);
				
				System.out.println("Returned from decoding, going to write the file to disk.");
				
				util.writeFileSilent(output,decodedName);
				
				System.out.println("Wrote the file.");
				
				
				
			}
			else{
				String origName = baseDir+msg.getStringProperty("origFile");
				String decodedName = origName.substring(0,origName.length()-4)+"_decoded.jpg";
				
				System.out.println("Going to decode: "+origName);
				ImagePlus decoded=dec.decodeNoOriginal(origName);
				
				System.out.println("Returned from decoding, going to write the file to disk.");
				
				util.writeFileSilent(decoded,decodedName);
				
				System.out.println("Wrote the file.");
			}
			
			mail.sendMail("http://71.178.49.139:8080/LogoEncoderJSF/pages/display.jsf?id="+id);
			
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
}
