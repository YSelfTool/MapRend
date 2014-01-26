package de.unitedminecraft;

import java.io.FileInputStream;

import org.json.*;

import java.io.File;

public class JSONColorSource implements ColorSource {
	
	int[][] colors;
	boolean[][] green;
	ColorF[] biomColors;
	
	public JSONColorSource(String ressourceFolder) {
		JSONObject colorsJSON = null;
		try {
			File jsonFile = new File(ressourceFolder, "colors.json");
			FileInputStream jsonFis;
			jsonFis = new FileInputStream(jsonFile);
			byte[] data = new byte[(int)jsonFile.length()];
		    jsonFis.read(data);
		    jsonFis.close();
		    String source = new String(data, "UTF-8");
			colorsJSON = new JSONObject(source);
			
		} catch (Exception e) {
			e.printStackTrace();
			Program.exitWithError("Cannot open colors.json!");
		}
		
		JSONArray arr = colorsJSON.getJSONArray("colors");
		int maxID = 0;
		for(int i = 0; i < arr.length(); i++) {
			int id = arr.getJSONObject(i).getInt("i");
			if(id > maxID) maxID = id;
		}
		colors = new int[maxID+1][16];
		green = new boolean[maxID+1][16];
		for(int i = 0; i < arr.length(); i++) {
			JSONObject cur = arr.getJSONObject(i);
			if(cur.getInt("m") == -1) {
				for(int j = 0; j < 16; j++) {
					colors[cur.getInt("i")][j] = cur.getInt("c");
					green[cur.getInt("i")][j] = (cur.getInt("g") == 1);
				}
			} else {
				colors[cur.getInt("i")][cur.getInt("m")] = cur.getInt("c");
				green[cur.getInt("i")][cur.getInt("m")] = (cur.getInt("g") == 1);
			}
		}
		JSONObject biomesJSON = null;
		try {
			File jsonFile = new File(ressourceFolder, "biomes.json");
			FileInputStream jsonFis;
			jsonFis = new FileInputStream(jsonFile);
			byte[] data = new byte[(int)jsonFile.length()];
		    jsonFis.read(data);
		    jsonFis.close();
		    String source = new String(data, "UTF-8");
			biomesJSON = new JSONObject(source);
			
		} catch (Exception e) {
			e.printStackTrace();
			Program.exitWithError("Cannot open biomes.json!");
		}
		JSONArray biomesArr = biomesJSON.getJSONArray("biomes");
		int maxBiomID = 0; 
		for(int i = 0; i < biomesArr.length(); i++) {
			JSONObject cur = biomesArr.getJSONObject(i);
			if(cur.getInt("id") > maxBiomID) maxBiomID = cur.getInt("id");
		}
		biomColors = new ColorF[maxBiomID+1];
		for(int i = 0; i < biomesArr.length(); i++) {
			JSONObject cur = biomesArr.getJSONObject(i);
			biomColors[cur.getInt("id")] = new ColorF(1.0f, (float)cur.getDouble("r"), (float)cur.getDouble("g"), (float)cur.getDouble("b"));
		}
	}
	
	@Override
	public int getColor(int block, int meta, int height, int light, int biome) {
		
		int c = getBlockColor(block, meta);
		int a = 0xff;
		int r = getR(c);
		int g = getG(c);
		int b = getB(c);
		float heightCoef = (((float)height) / 255) + 0.5f;// * 0.25f + 0.75f;
		r = (int)(r * heightCoef);
		g = (int)(g * heightCoef);
		b = (int)(b * heightCoef);
		float lightCoef = ((float)light / 15) * 0.5f + 0.5f;
		r = (int)(r * lightCoef);
		g = (int)(g * lightCoef);
		b = (int)(b * lightCoef);
		if(green[block][meta]) {
			ColorF biomeCoef = this.biomeCoef(biome);
			r = (int)(r * biomeCoef.getR());
			g = (int)(g * biomeCoef.getG());
			b = (int)(b * biomeCoef.getB());
		}
		if(r >= 255) r = 255;
		if(g >= 255) g = 255;
		if(b >= 255) b = 255;
		
		return getRGBA(r, g, b, a);
	}
	
	private int getBlockColor(int id, int meta) {
		return colors[id][meta];
	}
	
	private int getRGBA(int r, int g, int b, int a) {
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	private int getA(int color) {
		color = color & 0xff000000;
		return color >> 24;
	}
	
	private int getR(int color) {
		color = color & 0x00ff0000;
		return color >> 16;
	}
	
	private int getG(int color) {
		color = color & 0x0000ff00;
		return color >> 8;
	}
	
	private int getB(int color) {
		color = color & 0x000000ff;
		return color;
	}
	
	private ColorF biomeCoef(int biome) {
		
		return biomColors[biome];
		/*
		switch(biome) {
		case 0:
			return new ColorF(1.0f, 0.25f, 1.0f, 0.25f);
		case 1:
			return new ColorF(1.0f, 0.30f, 1.0f, 0.30f);
		case 2:
			return new ColorF(1.0f, 1.0f, 1.0f, 0.25f);
		case 3:
			return new ColorF(1.0f, 0.30f, 0.9f, 0.30f);
		case 4:
			return new ColorF(1.0f, 0.15f, 0.75f, 0.15f);
		case 5:
			return new ColorF(1.0f, 0.50f, 0.75f, 0.50f);
		case 6:
			return new ColorF(1.0f, 0.75f, 1.0f, 0.25f);
		case 7:
			return new ColorF(1.0f, 0.25f, 1.0f, 0.25f);
		case 8:
			return new ColorF(1.0f, 1.0f, 0.1f, 0.1f);
		case 9:
			return new ColorF(1.0f, 1.5f, 1.5f, 0.75f);
		case 10:
			return new ColorF(1.0f, 0.25f, 1.0f, 0.25f);
		case 11:
			return new ColorF(1.0f, 0.25f, 1.0f, 0.25f);
		case 12:
			return new ColorF(1.0f, 0.30f, 1.0f, 0.30f);
		case 13:
			return new ColorF(1.0f, 0.30f, 0.9f, 0.30f);
		case 14:
			return new ColorF(1.0f, 0.75f, 0.10f, 0.75f);
		case 15:
			return new ColorF(1.0f, 0.75f, 0.10f, 0.75f);
		case 16:
			return new ColorF(1.0f, 1.0f, 1.0f, 0.25f);
		case 17:
			return new ColorF(1.0f, 1.0f, 1.0f, 0.25f);
		case 18:
			return new ColorF(1.0f, 0.15f, 0.75f, 0.15f);
		case 19:
			return new ColorF(1.0f, 0.50f, 0.75f, 0.50f);
		case 20:
			return new ColorF(1.0f, 0.30f, 0.9f, 0.30f);
		case 21:
			return new ColorF(1.0f, 0.0f, 1.5f, 0.0f);
		case 22:
			return new ColorF(1.0f, 0.0f, 1.5f, 0.0f);
		case 23:
			return new ColorF(1.0f, 0.0f, 1.5f, 0.0f);
		case 24:
			return new ColorF(1.0f, 0.25f, 1.0f, 0.25f);
		case 25:
			return new ColorF(1.0f, 1.0f, 1.0f, 0.25f);
		case 26:
			return new ColorF(1.0f, 1.0f, 1.0f, 0.25f);
		case 27:
			return new ColorF(1.0f, 0.15f, 0.75f, 0.15f);
		case 28:
			return new ColorF(1.0f, 0.15f, 0.75f, 0.15f);
		case 29:
			return new ColorF(1.0f, 0.15f, 0.75f, 0.15f);
		case 30:
			return new ColorF(1.0f, 0.50f, 0.75f, 0.50f);
		case 31:
			return new ColorF(1.0f, 0.50f, 0.75f, 0.50f);
		case 32:
			return new ColorF(1.0f, 0.50f, 0.75f, 0.50f);
		case 33:
			return new ColorF(1.0f, 0.50f, 0.75f, 0.50f);
		case 34:
			return new ColorF(1.0f, 0.30f, 0.9f, 0.30f);
		case 35:
			return new ColorF(1.0f, 0.60f, 1.0f, 0.25f);
		case 36:
			return new ColorF(1.0f, 0.60f, 1.0f, 0.25f);
		case 37:
			return new ColorF(1.0f, 0.25f, 1.0f, 1.25f);
		case 38:
			return new ColorF(1.0f, 0.25f, 1.0f, 1.25f);
		case 39:
			return new ColorF(1.0f, 0.25f, 1.0f, 1.25f);
		case 129:
			return new ColorF(1.0f, 0.30f, 1.0f, 0.30f);
		case 130:
			return new ColorF(1.0f, 1.0f, 1.0f, 0.25f);
		case 131:
			return new ColorF(1.0f, 0.30f, 0.9f, 0.30f);
		case 132:
			return new ColorF(1.0f, 0.15f, 0.75f, 0.15f);
		case 133:
			return new ColorF(1.0f, 0.50f, 0.75f, 0.50f);
		case 134:
			return new ColorF(1.0f, 0.75f, 1.0f, 0.25f);
		case 140:
			return new ColorF(1.0f, 0.30f, 1.0f, 0.30f);
		case 149:
			return new ColorF(1.0f, 0.0f, 1.5f, 0.0f);
		case 151:
			return new ColorF(1.0f, 0.0f, 1.5f, 0.0f);
		case 155:
			return new ColorF(1.0f, 0.15f, 0.75f, 0.15f);
		case 156:
			return new ColorF(1.0f, 0.15f, 0.75f, 0.15f);
		case 157:
			return new ColorF(1.0f, 0.15f, 0.75f, 0.15f);
		case 158:
			return new ColorF(1.0f, 0.50f, 0.75f, 0.50f);
		case 160:
			return new ColorF(1.0f, 0.50f, 0.75f, 0.50f);
		case 161:
			return new ColorF(1.0f, 0.50f, 0.75f, 0.50f);
		case 162:
			return new ColorF(1.0f, 0.30f, 0.9f, 0.30f);
		case 163:
			return new ColorF(1.0f, 0.60f, 1.0f, 0.25f);
		case 164:
			return new ColorF(1.0f, 0.60f, 1.0f, 0.25f);
		case 165:
			return new ColorF(1.0f, 0.25f, 1.0f, 1.25f);
		case 166:
			return new ColorF(1.0f, 0.25f, 1.0f, 1.25f);
		case 167:
			return new ColorF(1.0f, 0.25f, 1.0f, 1.25f);
			
		}
		
		return new ColorF(1.0f, 1.0f, 1.0f, 1.0f);*/
	}
	
}
