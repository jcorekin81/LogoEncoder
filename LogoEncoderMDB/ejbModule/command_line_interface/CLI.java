/**
 * 
 */
package command_line_interface;

import core.Decoder;
import core.Encoder;
import core.Utils;
import core.Values;

import ij.IJ;

import ij.ImagePlus;

/**
 * @author Jason
 *
 */
public class CLI {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Utils util = new Utils();
		Encoder enc = new Encoder(util);
		Decoder dec = new Decoder(util);
		Values vals = util.getVals();
		CLI cli = new CLI();
		IJ ij = new IJ();
		
		boolean silent=false;
		
		
		if(args.length==0){
			cli.help();
			System.exit(0);
		}


		for(int i=0;i<args.length;i++){	
			if(args[i].equals("/max_change")){
				vals.setMaxChange(Long.valueOf(args[i+1]).intValue());
			}	
		}
		System.out.println("MAX_CHANGE is: "+vals.getMaxChange());
		for(int i=0;i<args.length;i++){	
			if(args[i].equals("/max_percent")){
				vals.setMaxPercent(Float.valueOf(args[i+1]));
			}	
		}
		System.out.println("MAX_PERCENT is: "+vals.getMaxPercent());
		for(int i=0;i<args.length;i++){	
			if(args[i].equals("/silent")){
				silent=true;
			}
		}
		System.out.println("silent is: "+silent);

		for(int i=0;i<args.length;i++){		
			if(args[i].equals("/encode")){
				System.out.println("Going to encode the image file!");

				ImagePlus logo = ij.openImage(args[i+1]);
				logo.setTitle("Logo");

				ImagePlus orig = ij.openImage(args[i+2]);
				orig.setTitle("Original Image");

				System.out.println("orig.getFileInfo: "+orig.getFileInfo());

				//			File jpegFile = new File(args[2]);
				//			try {
				//				Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
				//			} catch (JpegProcessingException e) {
				//				e.printStackTrace();
				//			}

				ImagePlus output=enc.addFiles(logo,orig);

				output.show();

				if(silent){
					String origName = args[i+2];
					String encodedName = origName.substring(0,origName.length()-4)+"_encoded.jpg";
					util.writeFileSilent(output,encodedName);
					output.hide();
				}
				else{
					util.writeFileBmp(output);
				}
				System.exit(0);
			}
			else if(args[i].equals("/convTest")){
				util.conversionTest(args[i+1]);
				System.exit(0);
			}
			else if(args[i].equals("/decode")){
				System.out.println("Going to decode the image file!");

				ImagePlus logo=dec.decodeNoOriginal(args[1]);

				logo.show();

				if(silent){
					String origName = args[i+1];
					String encodedName = origName.substring(0,origName.length()-4)+"_decoded.jpg";
					util.writeFileSilent(logo,encodedName);
				}
				else{
					util.writeFileBmp(logo);
				}
				System.exit(0);
			}
		}

		System.exit(0);
	}
	
	private  void help(){
		System.out.println("Here is how you run this program:\n" +
				"\t to encode a logo file into an image file use the following format\n" +
				"\t java -jar Encode-Decode.jar /encode LOGO_FILE_NAME.jpg IMAGE_FILE_INPUT_NAME.jpg OUTPUT_NAME.jpg\n\n\n"+
				"\t to decode a logo file into an image file use the following format\n" +
		"\t java -jar Encode-Decode.jar /decode LOGO_FILE_NAME.jpg IMAGE_FILE_INPUT_NAME.jpg OUTPUT_NAME.jpg");
	}

}

