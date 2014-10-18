package de.tooldev;

public class SimpleColorSource implements ColorSource {

	@Override
	public int getColor(int block, int meta, int height, int light, int biome) {
		//return new Color(255, block % 256, height % 256, light*16);
		return 255;
	}
		
}
