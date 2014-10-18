package de.tooldev;

import java.awt.image.BufferedImage;

public class ClusterChunk {
	private BufferedImage image;
	private int x, z;
	
	public ClusterChunk(BufferedImage img, int x, int z) {
		this.image = img;
		this.x = x;
		this.z = z;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage value) {
		this.image = value;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int value) {
		this.x = value;
	}
	
	public int getZ() {
		return z;
	}
	public void setZ(int value) {
		this.z = value;
	}
	
}
