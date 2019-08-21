package core;

public class Values {

	private int x;
	private int y;
	private int blockSize;
	private int squareSize=3;
	private int lowerLimit;
	private int upperLimit;
	private int maxChange;
	private float maxPercent;
	
	public Values(){
		lowerLimit=squareSize-Math.round(squareSize/2);
		upperLimit=squareSize+Math.round(squareSize/2);
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	public int getSquareSize() {
		return squareSize;
	}
	public void setSquareSize(int squareSize) {
		this.squareSize = squareSize;
	}
	public int getLowerLimit() {
		return lowerLimit;
	}
	public void setLowerLimit(int lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	public int getUpperLimit() {
		return upperLimit;
	}
	public void setUpperLimit(int upperLimit) {
		this.upperLimit = upperLimit;
	}
	public int getMaxChange() {
		return maxChange;
	}
	public void setMaxChange(int maxChange) {
		this.maxChange = maxChange;
	}
	public float getMaxPercent() {
		return maxPercent;
	}
	public void setMaxPercent(float maxPercent) {
		this.maxPercent = maxPercent;
	}
	
	
	
}
