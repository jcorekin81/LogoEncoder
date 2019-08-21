package beans;

import ij.IJ;
import ij.ImagePlus;

import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import core.Decoder;
import core.Encoder;
import core.Utils;
import core.Values;

@MessageDriven(name = "LogoEncoderMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/LogoEncoderMDBqueue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class LogoEncoderMDB implements MessageListener {

	private String baseDir = "C:\\JBOSS\\jbpm\\jboss-as-7.1.1.Final\\";
	
	private final static Logger logger = Logger.getLogger(LogoEncoderMDB.class
			.toString());

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message msg) {
		try {
			logger.info("Received msg " + msg);       
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
 
				String logoName = msg.getStringProperty("logoFile");
				String origName = msg.getStringProperty("origFile");
				
				System.out.println("Opening: " + logoName);
				
				ImagePlus logo = IJ.openImage(baseDir+logoName);

				System.out.println("Opening: "+ origName);
				
				ImagePlus orig = IJ.openImage(baseDir+origName);;
		
				System.out.println("orig.getFileInfo: ");
				System.out.println(orig.getFileInfo());

				System.out.println("Going to encode the image file!");				
				ImagePlus output=enc.addFiles(logo,orig);

				util.writeFileSilent(output,baseDir+origName,"encoded");
				output.hide();
			}
			else{
				
				String origName = msg.getStringProperty("origFile");
				
				System.out.println("Going to decode the image file!");
				ImagePlus logo=dec.decodeNoOriginal(baseDir+origName);

				
				util.writeFileSilent(logo,baseDir+origName,"decoded");
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
}
