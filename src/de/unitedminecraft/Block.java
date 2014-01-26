package de.unitedminecraft;

public class Block {
	
	private int blocktype;
	private int metadata;
	private int height;
	private int skyLight;
	private int blockLight;
	private int biome; 
	
	public Block(int blocktype, int metadata, int height, int skyLight, int blockLight, int biome) {
		this.blocktype = blocktype;
		this.metadata = metadata;
		this.height = height;
		this.skyLight = skyLight;
		this.blockLight = blockLight;
		this.biome = biome;
	}
	
	public int getBlockType() {
		return blocktype;
	}
	
	public int getMetaData() {
		return metadata;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getLight() {
		if(Program.renderNight) {
			return blockLight;
		}
		else {
			return blockLight > skyLight ? blockLight : skyLight; 
		}
	}
	
	public int getBiome() {
		return biome;
	}
	
}
