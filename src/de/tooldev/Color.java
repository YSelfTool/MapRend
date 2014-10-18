package de.tooldev;

public class Color {
	public int a, r, g, b;
	
	public Color(int a, int r, int g, int b) {
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public int getColor() {
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	public int getR() {
		return r;
	}
	
	public int getG() {
		return g;
	}
	
	public int getB() {
		return b;
	}
	
	public int getA() {
		return a;
	}
	
}
