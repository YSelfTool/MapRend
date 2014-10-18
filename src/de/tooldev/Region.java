package de.tooldev;

import java.io.*;
import com.mojang.nbt.*;
import net.minecraft.world.level.chunk.storage.RegionFile;

public class Region {
	
	RegionFile regionFile;
	
	public Region(File file) {
		if(!file.exists()) {
			Program.exitWithError("Region file does not exist!");
		}
		try {
			regionFile = new RegionFile(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Region(String world, int x, int z) {
		this(new File(new File(world, "region"), "r." + x + "." + z + ".mca"));
	}
	
	public CompoundTag getChunk(int x, int z) throws IOException {
		if(regionFile.hasChunk(x, z)) {
			return NbtIo.read(regionFile.getChunkDataInputStream(x, z));
		}
		return null;
	}
	
	public boolean hasChunk(int x, int z) {
		return regionFile.hasChunk(x, z);
	}
	
	
}
