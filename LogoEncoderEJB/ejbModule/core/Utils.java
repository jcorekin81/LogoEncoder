package core;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

public class Utils {
	
	private Values vals = new Values();
	
	public Values getVals() {
		return vals;
	}

	public void setVals(Values vals) {
		this.vals = vals;
	}

	public void rgb2ycbcr(int r, int g, int b, int[] ycbcr) {
        int Y = ((66 * r + 129 * g + 25 * b) >> 8) + 16;
        int Cb = ((-38 * r - 74 * g + 112 * b) >> 8) + 128;
        int Cr = ((112 * r - 94 * g - 18 * b) >> 8) + 128;


        ycbcr[0] = Y;
        ycbcr[1] = Cb;
        ycbcr[2] = Cr;         	
	}
	
	public void ycbcr2rgb(int Y, int Cb, int Cr, int[] rgb) {

        int C = Y - 16;
        int D = Cb - 128;
        int E = Cr - 128;


        int r = (int)((298 * C + 409 * E + 128) >> 8);
        int g = (int)((298 * C - 100 * D - 208 * E + 128) >> 8);
        int b = (int)((298 * C + 516 * D + 128) >> 8);



        if (r < 0)
            r = 0;
        else if (r > 255)
            r = 255;
        if (g < 0)
            g = 0;
        else if (g > 255)
            g = 255;
        if (b < 0)
            b = 0;
        else if (b > 255)
            b = 255;


        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;         
	}
	
	public void setVals(ImagePlus originalImage){
		vals.setX(originalImage.getWidth());
		vals.setY(originalImage.getHeight());
		vals.setBlockSize(findNextPower((int)(vals.getX()*.05),3));
		System.out.println("Initial BlockSize is:"+vals.getBlockSize());
	}
	
	public Values initialize(ImagePlus originalImage,int image[][][]){
		int x=vals.getX();
		int y=vals.getY();
		
		ImageProcessor originalImage_ip=originalImage.getProcessor();

		int pixel[]=new int[3];

		for(int i=0;i<x;i++){
			for(int j=0;j<y;j++){
				originalImage_ip.getPixel(i, j, pixel);
				rgb2ycbcr(pixel[0],pixel[1],pixel[2],image[i][j]);
			}
		}
		
		return vals;
	}

	public  ImagePlus finalization(int image[][][]){
		
		int x = vals.getX();
		int y = vals.getY();
		
		ImagePlus finalImage=NewImage.createRGBImage ("Final image", x , y, 1, 0);
		ImageProcessor finalImage_ip=finalImage.getProcessor();
		int pixel[]=new int[3];


		for(int i=0;i<x;i++){
			for(int j=0;j<y;j++){
				ycbcr2rgb(image[i][j][0],image[i][j][1],image[i][j][2],pixel);
				finalImage_ip.putPixel(i, j,pixel);
			}
		}

		finalImage.draw();


		return finalImage;
	}

	public  void conversionTest(String testFile){
		ImagePlus testImage = IJ.openImage(testFile);
		testImage.setTitle("TestFile");

		testImage.show();

		int imageArray[][][]=new int[testImage.getWidth()][testImage.getHeight()][3];
		initialize(testImage,imageArray);

		ImagePlus finalImage=finalization(imageArray);

		finalImage.show();

		writeFile(finalImage);
		return;
	}

	public  void writeFile (ImagePlus image){

		ij.io.FileSaver file = new ij.io.FileSaver(image);
		FileSaver.setJpegQuality(100);
		file.saveAsJpeg();
	}

	public  void writeFileBmp (ImagePlus image){

		ij.io.FileSaver file = new ij.io.FileSaver(image);
		file.saveAsBmp();
	}

	public  void writeFileSilent (ImagePlus image,String inputName,String tackOn){
		ij.io.FileSaver file = new ij.io.FileSaver(image);
		String outputName=inputName.substring(0,inputName.length()-4)+"_"+tackOn+".jpg";
		FileSaver.setJpegQuality(100);
		file.saveAsJpeg(outputName);
	}
	
	public  int findNextPower(int start,int powerOf){
		int output=powerOf;
		
		while(output<start){
			output*=powerOf;
		}
		
		return output;
	}

	public float[][][] findDifferences(ImagePlus orig,int image[][][]){

		int x = vals.getX();
		int y = vals.getY();
		int squareSize= vals.getSquareSize();
		int blockSize = vals.getBlockSize();
		int lowerLimit=vals.getLowerLimit();
		int upperLimit=vals.getUpperLimit();
		
		int numberOfDiffs=0;
		int diff=0;

		float output[][][]=new float [x][y][5];
		float expectedAverageDiff=0;
		float averageDifference=0;

		int mModBlockSize=0,nModBlockSize=0;

		int localSum=0;
		int localMax=0;
		int neighborsDiffsSum=0;
		int numberOfAverageDiffs=0;
		int neighborsAverageDiff=0;
		int expectedValueSum=0;
		int neighborColor=0;
		int myLumenence=0;
		int neighbor1=0,neighbor2=0;

		int neighbors[][]=new int[squareSize][squareSize];

		float outputColumn[][][]=new float[blockSize][y][4];
		float block[][][]=new float[blockSize][blockSize][2];

		int backupImage[][][]=new int[x][y][3];
		for(int i=0;i<x;i++){
			for(int j=0;j<y;j++){
				backupImage[i][j][0]=image[i][j][0];
				backupImage[i][j][1]=image[i][j][1];
				backupImage[i][j][2]=image[i][j][2];
			}
		}


		for(int i=0;i<x;i=i+blockSize){

			for(int j=0;j<y;j=j+blockSize){


				for(int m=i;m<i+blockSize&&m<x;m++){
					//System.out.println("i:"+i+"j:"+j+"m:"+m);
					for(int n=j;n<j+blockSize&&n<y;n++){							
						if(m>0){
							mModBlockSize=(m%blockSize);
						}
						else{
							mModBlockSize=(m);
						}

						if(n>0){
							nModBlockSize=(n%blockSize);
						}
						else{
							nModBlockSize=(n);
						}

						for(int k=0;k<squareSize;k++){
							for(int l=0;l<squareSize;l++){

								neighbors[k][l]=0;
							}
						}

						numberOfDiffs=0;
						averageDifference=0;

						localSum=0;

						neighborsDiffsSum=0;
						expectedAverageDiff=0;

						localMax=0;

						int a=0,b=0,c=0,d=0,k=0,l=0,o=0,p=0;

						myLumenence=image[m][n][0];

						for(a=m-lowerLimit;a<m+upperLimit;a++){
							if(a<0)
								k=m+upperLimit+a;
							else if(a>x)
								k=m-(a-x);
							else if(a<x)
								k=a;
							for(b=n-lowerLimit;b<n+upperLimit;b++){
								if(b<0)
									l=n+upperLimit+b;
								else if(b>y)
									l=n-(b-y);
								else if(b<y)
									l=b;
								if(k!=m&&l!=n)
								{
									neighborColor=image[k][l][0];

									neighbors[(k+lowerLimit)%squareSize][(l+lowerLimit)%squareSize]=neighborColor;

									diff=neighborColor-myLumenence;
									if(diff!=0){
										localSum+=diff;
										numberOfDiffs++;
										if(localMax<diff){
											localMax=diff;
										}
									}
								}

							}
						}
					
						//Calculate the average difference between the current 
						//pixel and it neighbors for each color.
						if(numberOfDiffs>0){
							averageDifference=localSum/numberOfDiffs;}


						for(a=m-lowerLimit;a<m+upperLimit;a++){
							if(a<0)
								k=m+upperLimit+a;
							else if(a>x)
								k=m-(a-x);
							else if(a<x)
								k=a;
							for(b=n-lowerLimit;b<n+upperLimit;b++){
								if(b<0)
									l=n+upperLimit+b;
								else if(b>y)
									l=n-(b-y);
								else if(b<y)
									l=b;{

										neighbor1=image[k][l][0];
										for(c=m-lowerLimit;c<m+upperLimit;c++){
											if(c<0)
												o=m+upperLimit+c;
											else if(c>x)
												o=m-(c-x);
											else if(c<x)
												o=c;
											for(d=n-lowerLimit;d<n+upperLimit;d++){
												if(d<0)
													p=n+upperLimit+d;
												else if(d>y)
													p=n-(d-y);
												else if(d<y)
													p=d;{
														neighbor2=image[o][p][0];
														diff=neighbor1-neighbor2;
														if(diff!=0){
															neighborsDiffsSum+=diff;
														}
													}
											}
										}
										if(numberOfDiffs>0){
											neighborsAverageDiff=neighborsDiffsSum/(numberOfDiffs);
											expectedValueSum+=neighborsAverageDiff;
											numberOfAverageDiffs++;
										}
									}
							}
						}
						if(numberOfAverageDiffs>0){
							expectedAverageDiff=expectedValueSum/(numberOfAverageDiffs);
						}
						/*Write the average difference into the	 outputColumn array for temporary storage.*/
						if(m!=0){					
							outputColumn[mModBlockSize][n][1]=averageDifference;
							outputColumn[mModBlockSize][n][0]=expectedAverageDiff;
							block[mModBlockSize][nModBlockSize][1]=averageDifference;
							block[mModBlockSize][nModBlockSize][0]=expectedAverageDiff;
						}
					}
				}


				/*DEBUG CODE*/
				//				System.out.println("\tFor the (x,y) coordiant "+(i)+","+(j)+", The sumOfTheAverages value is: "+
				//						sumOfTheAverages[0]+", "+sumOfTheAverages[1]+", "+sumOfTheAverages[2]);
				//				System.out.println("\tFor the (x,y) coordiant "+(i)+","+(j)+", The totalPixels value is: "+
				//						totalPixels[0]+", "+totalPixels[1]+", "+totalPixels[2]);	



				//				System.out.println("\tFor the (x,y) coordinent "+i+","+j+", The maxDifference value is: "+
				//						output[i][j][0][0]+", "+output[i][j][0][1]+", "+output[i][j][0][2]);
				//				System.out.println("\tFor the (x,y) coordinent "+ i+","+j+ ", the expectedDifference value is: "+
				//						output[i][j][1][0]+", "+output[i][j][1][1]+", "+output[i][j][1][2]);
				//				System.gc();


				findRangeToWrite(block,outputColumn,j);
			}



			for(int m=i;m<i+blockSize&&m<x;m++){
				for(int n=0;n<y;n++){

					//			System.out.println("X: "+m);
					if(m>0){
						mModBlockSize=(m%blockSize);
					}
					else{
						mModBlockSize=(m);
					}

					/*This is the actual nine writes into the data values for each pixel as stored
					 * in the output array.  This is the only place that output should ever be written to.*/
					output[m][n][0]=outputColumn[mModBlockSize][n][0];
					output[m][n][1]=outputColumn[mModBlockSize][n][1];
					output[m][n][2]=outputColumn[mModBlockSize][n][2];
					output[m][n][3]=outputColumn[mModBlockSize][n][3];

				}
			}
		}

		for(int i=0;i<x;i++){
			for(int j=0;j<y;j++){
				image[i][j][0]=backupImage[i][j][0];
				image[i][j][1]=backupImage[i][j][1];
				image[i][j][2]=backupImage[i][j][2];
			}
		}


		/*Attempting to force garbage collection to help with memory management.*/
		System.gc();

		/*Hiding the progress bar so that the next method can bring it back.*/
		//IJ.getBase().setVisible(false);	

		return output;

	}
	
	public  void findRangeToWrite(float[][][] block,float[][][] differences,int vert){

		int range[]={-1000,-1000};

		findRangeToWriteHelper(block,differences,vert,range,vals.getBlockSize());

		return;
	}

	private  void findRangeToWriteHelper(float[][][] block,float[][][] differences,
			int vert,int range[],int externalBlockSize){
		int x = vals.getX();
		int y = vals.getY();
		
		int maxChange = vals.getMaxChange();
		float maxPercent = vals.getMaxPercent();
		
		
		int arraySize=findNextPower(maxChange*2,2);
		int arraySizeDiv2=arraySize/2;
		float rangeFloat=0,oldRangeFloat=0;
		int numberOf[]=new int[arraySize];
		int totalPixels=0;
		int changeValue=0;
		int i=0;



		for(i=0;i<externalBlockSize;i++){
			for(int j=0;j<externalBlockSize;j++){
				changeValue=(int)(block[i][j][1]-block[i][j][0])+arraySizeDiv2;
				if(changeValue>=0&&changeValue<maxChange*2){
					numberOf[changeValue]++;
				}
				totalPixels++;
			}
		}

		for(i=arraySizeDiv2;i<arraySizeDiv2+maxChange;i++){
			if(((float)numberOf[i]/(float)totalPixels)*100>maxPercent){
				break;
			}
		}
		for(;i<arraySizeDiv2+maxChange;i++){
			rangeFloat=((float)numberOf[i]/(float)totalPixels)*100;
			if(rangeFloat<maxPercent){
				range[0]=i-arraySizeDiv2;
				break;
			}
		}
		for(;i<arraySizeDiv2+maxChange;i++){
			rangeFloat=((float)numberOf[i]/(float)totalPixels)*100;
			if(rangeFloat<maxPercent&&i>=arraySizeDiv2+maxChange-1){
				oldRangeFloat=rangeFloat;
				range[1]=i-arraySizeDiv2;
				break;
			}
			else if(rangeFloat>maxPercent&&oldRangeFloat<maxPercent&&i-1<arraySizeDiv2+maxChange){
				range[1]=i-1-arraySizeDiv2;
				break;
			}
		}
		if(i>=arraySizeDiv2+maxChange){
			range[0]=-2000;
			range[1]=-2000;
		}

		if(range[0]==-2000&&range[1]==-2000){

			for(i=arraySizeDiv2;i>arraySizeDiv2-maxChange;i--){
				if(((float)numberOf[i]/(float)totalPixels)*100>maxPercent){
					break;
				}
			}
			for(;i>arraySizeDiv2-maxChange;i--){
				rangeFloat=((float)numberOf[i]/(float)totalPixels)*100;
				if(rangeFloat<maxPercent){
					range[0]=i-arraySizeDiv2;
					break;
				}
			}
			for(;i<arraySizeDiv2-maxChange;i--){
				rangeFloat=((float)numberOf[i]/(float)totalPixels)*100;
				if(rangeFloat<maxPercent&&i>arraySizeDiv2-maxChange+1){
					oldRangeFloat=rangeFloat;
					range[1]=i-arraySizeDiv2;
					break;
				}
				else if(rangeFloat>maxPercent&&oldRangeFloat<maxPercent&&i-1<arraySizeDiv2-maxChange){
					range[1]=i-arraySizeDiv2-maxChange+1;
					break;
				}
			}
			if(i>=arraySizeDiv2-maxChange-1){
				range[0]=-2000;
				range[1]=-2000;
			}
		}

		if(((range[0]==range[1])||(range[0]==-1000&&range[1]==-1000)||(range[0]==-2000&&range[1]==-2000))&&
				externalBlockSize>3){

			int internalBlockSize=externalBlockSize/3;

			float newBlock[][][]=new float[internalBlockSize][internalBlockSize][2];			

			int origVert=vert;

			for(int oi=0;oi<externalBlockSize;oi+=internalBlockSize){
				for(int oj=0;oj<externalBlockSize;oj+=internalBlockSize){
					for(i=oi;i<oi+internalBlockSize&&i<externalBlockSize;i++){
						for(int j=oj;j<oj+internalBlockSize&&j<externalBlockSize;j++){
							newBlock[i%internalBlockSize][j%internalBlockSize][0]=block[i][j][0];
							newBlock[i%internalBlockSize][j%internalBlockSize][1]=block[i][j][1];
						}
					}
					findRangeToWriteHelper(newBlock,differences,vert,range,internalBlockSize);
					vert+=internalBlockSize;
				}
				vert=origVert;
			}
		}
		for(i=0;i<externalBlockSize&&i<x;i++){
			for(int j=vert;j<vert+externalBlockSize&&j<y;j++){
				differences[i][j][2]=range[0];
				differences[i][j][3]=range[1];
			}
		}

		return;
	}
	
	protected  void histogramDiffOfDiffs(float[][][] differences, int diffArray1, int diffArray2){

		int x = vals.getX();
		int y = vals.getY();
		int maxChange = vals.getMaxChange();
		
		int arraySize=findNextPower(maxChange*2,2);
		int arraySizeDiv2=arraySize/2;
		int numberOf[]=new int[arraySize];

		int totalPixels=0;
		float mostCommonChange[]=new float[2];
		float scale=0;
		float scale100=0;
		int changeValue=0;
		for(int i=0;i<x;i++){
			for(int j=0;j<y;j++){
				changeValue=(int)(differences[i][j][diffArray1]-differences[i][j][diffArray2])+arraySizeDiv2;
				if(changeValue>=0&&changeValue<arraySize){
					numberOf[changeValue]++;
					if(numberOf[changeValue]>mostCommonChange[1]){
						mostCommonChange[0]=changeValue;
						mostCommonChange[1]=numberOf[changeValue];
					}
				}
				totalPixels++;
			}
		}

		System.out.println("The total number of pixels is: "+totalPixels);
		System.out.println("The the most common change value within the range"+(-arraySizeDiv2)+" to "+arraySizeDiv2 +"is: "
				+(mostCommonChange[0]-arraySizeDiv2)+" with a percentage of"+(mostCommonChange[1]/totalPixels)*100);
		for(int i=0;i<arraySize;i++){
			scale=(float)numberOf[i]/(float)totalPixels;
			scale100=scale*100;
			System.out.println("In the "+diffArray1+"diffArray for the diff value of: "+(i-arraySizeDiv2)
					+" there are a total of: "+numberOf[i]+" which is "+scale100+"% of the total pixels.");
			for(int j=0;j<scale100;j++){
				System.out.print("#");
			}
			System.out.println("\n");
		}
	}
	
	public  void histogram(float[][][] differences, int diffArray){
		
		int x = vals.getX();
		int y = vals.getY();
		int maxChange = vals.getMaxChange();
		
		int arraySize=findNextPower(maxChange*2,2);
		int arraySizeDiv2=arraySize/2;
		int numberOf[]=new int[arraySize];
		int totalPixels=0;
		float mostCommonChange[]=new float[2];
		float scale=0;
		float scale100=0;
		int changeValue=0;
		for(int i=0;i<x;i++){
			for(int j=0;j<y;j++){
				changeValue=(int)differences[i][j][diffArray]+arraySizeDiv2;
				if(changeValue>=0&&changeValue<arraySize){
					numberOf[changeValue]++;
					if(numberOf[changeValue]>mostCommonChange[1]){
						mostCommonChange[0]=changeValue;
						mostCommonChange[1]=numberOf[changeValue];
					}
				}
				totalPixels++;
			}
		}

		System.out.println("The total number of pixels is: "+totalPixels);
		System.out.println("The the most common change value within the range "+(-arraySizeDiv2)+ " to "+arraySizeDiv2+" is: "
				+(mostCommonChange[0]-arraySizeDiv2)+" with a percentage of"+(mostCommonChange[1]/totalPixels)*100);
		//		waitManySec(20);
		for(int i=0;i<arraySize;i++){
			scale=(float)numberOf[i]/(float)totalPixels;
			scale100=scale*100;
			System.out.println("In the "+diffArray+"diffArray for the diff value of: "+(i-arraySizeDiv2)
					+" there are a total of: "+numberOf[i]+" which is "+scale100+"% of the total pixels.");
			for(int j=0;j<scale100;j++){
				System.out.print("#");
			}
			System.out.println("\n");
		}
	}
}
