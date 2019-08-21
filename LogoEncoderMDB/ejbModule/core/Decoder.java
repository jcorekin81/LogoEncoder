package core;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;

public class Decoder {

	private int currentCompareArray=1;
	Values vals = null;
	Utils util = null;
	

	public Decoder(Utils util) {
		this.util = util;
	}


	public  ImagePlus decodeNoOriginal (String encodeedFile){
		
		ImagePlus encr = IJ.openImage(encodeedFile);
		util.setVals(encr);
		encr.setTitle("encoded Image");
		int image[][][]=new int[encr.getWidth()][encr.getHeight()][3];
		vals=util.initialize(encr,image);

		int x = vals.getX();
		int y = vals.getY();
		
		float differences[][][]=util.findDifferences(encr,image);

		util.histogram(differences,currentCompareArray);
		util.histogram(differences,1);

		ImagePlus decoded  = NewImage.createRGBImage ("decoded image", x , y , 1, 0xffffffff);

		int color[]=new int[3];

		System.out.println("Running main decode loop.");
		
		for(int i=0;i<x;i++){
			for(int j=0;j<y;j++){

				color[0]=0;
				color[1]=0;
				color[2]=0;

				if((differences[i][j][2]!=-2000&&differences[i][j][3]!=-2000)&&
						(Math.abs(differences[i][j][1]-differences[i][j][0])>=differences[i][j][2]&&
								Math.abs(differences[i][j][1]-differences[i][j][0])<=differences[i][j][3])){

					util.ycbcr2rgb(image[i][j][0],image[i][j][1],image[i][j][2],color);
					decoded.getProcessor().putPixel(i, j,color);
					
				}
			}	
		}

		System.out.println("Returning decoded image");
		
		return decoded;

	}
	
}
