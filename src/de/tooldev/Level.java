package de.tooldev;

import java.io.*;

import com.mojang.nbt.*;


public class Level {
	
	CompoundTag dataTag;
	
	public Level(String world) {
		File file = new File(world, "level.dat");
		if(!file.exists()) {
			Program.exitWithError("Level file does not exist!");
		}
		
		try {
			CompoundTag root;
			root = NbtIo.readCompressed(new FileInputStream(file));
			dataTag = root.getCompound("Data");
			
		} catch (Exception e) {
			e.printStackTrace();
		}    
	}
	
	public String getWorldName() {
		return dataTag.getString("LevelName");
	}
	
	public int getSpawnX() {
		return dataTag.getInt("SpawnX");
	}
	
	public int getSpawnZ() {
		return dataTag.getInt("SpawnZ");
	}
	
	
}
