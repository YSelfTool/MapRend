package de.unitedminecraft;

import java.io.*;

import com.mojang.nbt.*;

public class Chunk {
	
	final int cs = 16;
	CompoundTag levelTag;
	int[][][] blockLight, skyLight, blocks, metadata;
	int[][] biome;
	int maxHeight;
	
	public Chunk(CompoundTag baseTag) {
		levelTag = baseTag.getCompound("Level");
		
		ListTag sections = levelTag.getList("Sections");
		maxHeight = ((int)((CompoundTag)(sections.get(sections.size() - 1))).getByte("Y") + 1) * cs;
		
		blockLight = new int[cs][maxHeight][cs];
		skyLight = new int[cs][maxHeight][cs];
		blocks = new int[cs][maxHeight][cs];
		metadata = new int[cs][maxHeight][cs];
		biome = new int[cs][cs];
		
		for(int x = 0; x < cs; x++) {
			for(int z = 0; z < cs; z++) {
				for(int y = 0; y < maxHeight; y++) {
					blockLight[x][y][z] = 0;
					skyLight[x][y][z] = 15;
					blocks[x][y][z] = 0;
					metadata[x][y][z] = 0;
				}
				biome[x][z] = 0;
			}
		}
		
		boolean calcLight = levelTag.getBoolean("LightPopulated"); 
		
		for(int i = 0; i < sections.size(); i++) {
			CompoundTag section = (CompoundTag)sections.get(i);
			int sectionY = (int)(section.getByte("Y")) * 16;
			byte[] blockData = section.getByteArray("Blocks");
			byte[] blockLightData = section.getByteArray("BlockLight");
			byte[] skyLightData = section.getByteArray("SkyLight");
			byte[] metadataData = section.getByteArray("Data");
			for(int x = 0; x < cs; x++) {
				for(int y = 0; y < cs; y++) {
					for(int z = 0; z < cs; z++) {
						try {
							blocks[x][y+sectionY][z] = getPositiveByte(blockData[getIndex(x, y, z)]);
							metadata[x][y+sectionY][z] = getHalfIndexValue(metadataData, x, y, z);
							if(calcLight) {
								blockLight[x][y+sectionY][z] = getHalfIndexValue(blockLightData, x, y, z);
								skyLight[x][y+sectionY][z] = getHalfIndexValue(skyLightData, x, y, z);
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		byte[] biomeData = levelTag.getByteArray("Biomes");
		for(int x = 0; x < cs; x++) {
			for(int z = 0; z < cs; z++) {
				biome[x][z] = getPositiveByte(biomeData[getIndex(x, z)]);
			}
		}
		
	}
	
	private int getPositiveByte(byte b) {
		return (int)(b & 0x0f) + (int)((b & 0xf0) >> 4) * 16;
	}
	
	private int getIndex(int x, int y, int z) {
		return ((y * 16 + z) * 16 + x);
	}
	
	private int getIndex(int x, int z) {
		return (z * 16 + x);
	}
	
	private int getHalfIndexValue(byte[] data, int x, int y, int z) {
		int r = getIndex(x, y, z);
		int i = r / 2;
		int h = r % 2;
		byte b = data[i];
		if(h == 0) {
		 	return (int)(b & 0x0f);
		} else {
			return (int)((b & 0xf0) >> 4);
		}
	}
	
	public Block getBlock(int x, int z) {
		for(int y = maxHeight - 1; y > 0; y--) {
			if(isBlockSolid(blocks[x][y][z])) {
				int h = y;
				if(blocks[x][y][z] == 8 || blocks[x][y][z] == 9) {
					for(; h > 0 && (blocks[x][h][z] == 8 || blocks[x][h][z] == 9); h--);
				}
				return new Block(blocks[x][y][z], metadata[x][y][z], h, skyLight[x][(y < maxHeight-1?y+1:y)][z], blockLight[x][(y < maxHeight-1?y+1:y)][z], biome[x][z]);
			}
		}
		return new Block(0, 0, 0, 0, 0, 0);
	}
	
	private boolean isBlockSolid(int block) {
		return Program.blockVisibility[block];
	}
	
	public int getX() {
		return levelTag.getInt("xPos");
	}
	
	public int getZ() {
		return levelTag.getInt("zPos");
	}
	
	
}
