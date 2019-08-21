package core;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class Encoder {

	Utils util=null;
	
	Values vals = null;
	
	private int changedCount;

	int squareSize;
	int devisor;

	int blockSize;

	int black[]={0,0,0};
	int maxCount=1;

	int maxChange;
	float maxPercent;
	
	public Encoder(Utils util) {
		this.util = util;
		vals = util.getVals();
		squareSize = vals.getSquareSize();
		devisor=(squareSize*squareSize)-1;
		blockSize=vals.getBlockSize();
		maxChange=vals.getMaxChange();
		maxPercent=vals.getMaxPercent();
	}

	public ImagePlus convertLogo (ImagePlus logo){
		ImageProcessor logo_ip = logo.getProcessor();
		int x=vals.getX(), y=vals.getY();
		
		int logoArray[][][]=new int[x][y][3];
		int pixel[]=new int[3];
		int white[]={255,255,255};

		System.out.println("x is: "+x+", y is: "+y);

		System.out.println("logo's x is: "+logo.getWidth()+", logo's y is: "+logo.getHeight());

		if(x<y){
			logo_ip=logo_ip.rotateLeft();
		}

		System.out.println("logo_ip's x is: "+logo_ip.getWidth()+", logo_ip's y is: "+logo_ip.getHeight());
		System.out.println("logo's x is: "+logo.getWidth()+", logo's y is: "+logo.getHeight());

		for(int i=0;i<x;i++){
			for(int j=0;j<y;j++){
				logo_ip.getPixel(i, j, pixel);
				logoArray[i][j][0]=pixel[0];
				logoArray[i][j][1]=pixel[1];
				logoArray[i][j][2]=pixel[2];
			}
		}

		for(int i=0;i<x;i++){
			for(int j=0;j<y;j++){
				if(logoBlackCount(logoArray,i,j)){
					logo_ip.putPixel(i, j, 0);
				}
				else{
					logo_ip.putPixel(i, j, white);
				}
			}
		}

		logo_ip=logo_ip.resize(x,y);
		logo.setProcessor(null,logo_ip);

		logo.updateImage();
		//logo.show();

		return logo;
	}

	private boolean logoBlackCount(int[][][] logo, int m, int n){

		//		printArray(neighbors,color);

		int x=vals.getX(), y=vals.getY();
		int lowerLimit=vals.getLowerLimit();
		int upperLimit=vals.getUpperLimit();
		
		int blackCount=0;
		int count=0;
		int i=0, j=0,k=0,l=0;

		for(i=m-lowerLimit;i<m+upperLimit;i++){
			if(i<0)
				k=m+upperLimit+i;
			else if(i>x)
				k=m-(i-x);
			else if(i<x)
				k=i;
			for(j=n-lowerLimit;j<n+upperLimit;j++){
				if(j<0)
					l=n+upperLimit+j;
				else if(j>y)
					l=n-(j-y);
				else if(j<y)
					l=j;
				if(logo[k][l][0]==0){
					blackCount++;
				}
				count++;
			}
		}

		if(blackCount>0&&count/blackCount>=1.5&&count/blackCount<=4.0){
			return true;
		}

		return false;
	}
	
	public  ImagePlus addFiles(ImagePlus logo, ImagePlus orig){		
		//		ImagePlus temp  = NewImage.createRGBImage ("Encrypted image", x , y , 1, 0);

		util.setVals(orig);
		
		int x=vals.getX(), y=vals.getY();
		boolean changedArray[][]=new boolean[x][y];

		boolean changed=true;

		changedCount=0;


		int image[][][]=new int[x][y][3];

		util.initialize(orig,image);

		logo=convertLogo(logo);
		float differences[][][]=util.findDifferences(orig,image);

		util.histogramDiffOfDiffs(differences,2,1);

		System.gc();

		for(int count=0;count<maxCount&&changed;count++){
			System.out.println("Count is at: "+count);

			for(int i=0;i<x;i+=squareSize){
				for(int j=0;j<y;j+=squareSize){
					changedArray[i][j]=false;
				}
			}



			for(int i=0;i<x;i+=squareSize){

				for(int j=0;j<y;j+=squareSize){

					changeDiff(differences,logo,orig,image,changedArray,i,j);

				}
			}
			changed=false;
			for(int i=0;!changed&&i<x;i++){

				for(int j=0;!changed&&j<y;j++){
					if(changedArray[i][j]==true){
						changed=true;
					}
				}
			}
		}

		System.out.println("The number of pixels changed is: " + changedCount);

		//IJ.getBase().setVisible(false);	
		return util.finalization(image);
	}

	
	private  void changeDiff(float[][][] differences,ImagePlus logo,ImagePlus orig,
			int image[][][],boolean changedArray[][],int m, int n){


		int x=vals.getX(), y=vals.getY();
		boolean changed=false;

		int myColor[]=new int[3],myColorOrig[]=new int[3];
		int diff=0;
		ImageProcessor logo_ip = logo.getProcessor();
		int logoPixelValue[]=new int[3];
		float averageDiff=0;
		float expectedDiff=0;

		for(int a=m;a<m+squareSize&&a<x;a++){
			for(int b=n;b<n+squareSize&&b<y;b++){
				changed=false;
				myColor[0]=image[a][b][0];
				myColor[1]=image[a][b][1];
				myColor[2]=image[a][b][2];
				myColorOrig[0]=myColor[0];
				myColorOrig[1]=myColor[1];
				myColorOrig[2]=myColor[2];

				logo_ip.getPixel(a, b,logoPixelValue);
				
				if(a==286&&b==122){
					System.out.println("differences["+a+"]["+b+"][2] is: "+differences[a][b][2]);
					System.out.println("differences["+a+"]["+b+"][3] is: "+differences[a][b][3]);
					System.out.println("The logoPixelValues are: "
							+logoPixelValue[0]+", "+logoPixelValue[1]+", "+logoPixelValue[2]);
				}
				
				if((differences[a][b][2]!=-2000&&differences[a][b][3]!=-2000)&&
						logoPixelValue[0]==0&&logoPixelValue[1]==0&&logoPixelValue[2]==0){


					averageDiff=differences[a][b][1];
					expectedDiff=differences[a][b][0];
					diff=(int)(averageDiff-expectedDiff);

					if(diff!=0&&Math.abs(diff)>differences[a][b][3]){
						if(diff>0){
							myColor[0]=(int) (myColor[0]-diff+differences[a][b][3]);
						}
						else{
							myColor[0]=(int) (myColor[0]-diff-differences[a][b][3]);
						}
						changed=true;
					}
					else if(Math.abs(diff)<differences[a][b][0]){
						if(diff>0){
							myColor[0]=(int) (myColor[0]-diff+differences[a][b][2]);
						}
						else{
							myColor[0]=(int) (myColor[0]-diff-differences[a][b][2]);
						}
						changed=true;
					}
					else{
						if(diff>0){
							myColor[0]=(int) (myColor[0]-diff+differences[a][b][3]);
						}
						else{
							myColor[0]=(int) (myColor[0]-diff-differences[a][b][3]);
						}
						changed=true;
					}

					image[a][b][0]=myColor[0];
					if(changed){
						changedCount++;
					}
					changedArray[a][b]=changed;

				}
			}
		}
		return;
	}
}
