package egovframework.scanner.service;

public class OptionVO {
	
	private int timeout = 0;
	
	private int side = 0;
	
	private int resolution = 0;
	
	private int color = 0;
	
	private int rotation = 0;
	
	private int dropout = 0;
	
	private int fdLevel = 0;
	
	private int compression = 0;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getSide() {
		return side;
	}

	public void setSide(int side) {
		this.side = side;
	}

	public int getResolution() {
		return resolution;
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public int getDropout() {
		return dropout;
	}

	public void setDropout(int dropout) {
		this.dropout = dropout;
	}

	public int getFdLevel() {
		return fdLevel;
	}

	public void setFdLevel(int fdLevel) {
		this.fdLevel = fdLevel;
	}

	public int getCompression() {
		return compression;
	}

	public void setCompression(int compression) {
		this.compression = compression;
	}

}
