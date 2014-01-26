package de.unitedminecraft;

import java.io.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.json.*;

public class Program {
	
	// render options
	static String worldFolder = "";
	static String ressourceFolder = "";
	static String worldName = "";
	static String outputFolder = "";
	static String clusterFolder = "";
	static String jsonOutputFolder = "";
	public static boolean renderNight = false;
	public static int chunkRadius = 100;
	public static boolean drawRect = false;
	static int spawnX = 0, spawnZ = 0;
	static int spawnChunkX = 0, spawnChunkZ = 0;
	public static boolean[] blockVisibility;
	public static int clusterSize = 8;
	public static boolean renderCluster = false;
	public static int chunkSize = 16;
	public static int regionSize = 32;
	
	public static void main(String[] args) throws IOException {
		if(args.length >= 1) {
			if(args[0].equals("map")) {
				long startTime = System.nanoTime();
				generateMap(args);
				long endTime = System.nanoTime();
				long duration = endTime - startTime;
				long nanoseconds = duration % 1000; duration /= 1000;
				long mikroseconds = duration % 1000; duration /= 1000;
				long milliseconds = duration % 1000; duration /= 1000;
				long seconds = duration % 60; duration /= 60;
				long minutes = duration % 60; duration /= 60;
				long hours = duration % 24; duration /= 24;
				long days = duration;
				
				System.out.println("Needed " + 
					(days > 0 ? days + "d" : "") + 
					(hours > 0 ? hours + "h" : "") + 
					(minutes > 0 ? minutes + "m" : "") + 
					(seconds > 0 ? seconds + "s" : "") + 
					(milliseconds > 0 ? milliseconds + "" : ""));/* + 
					(mikroseconds > 0 ? mikroseconds + "u" : "") + 
					(nanoseconds > 0 ? nanoseconds + "n" : ""));*/
					
			} else if(args[0].equals("textures")) {
				generateTextures(args);
			} else {
				exitWithError("Arguments are invalid: " + args[0]);
			}
		} else {
			System.out.println("Missing Arguments! Expected form: ");
			System.out.println("either");
			System.out.println(" map world [ressource [output [day/night [radius [circle/rect [cluster [clusterpath [jsonoutput]]]]]]]]");
			System.out.println(" -> Renders a map of the world");
			System.out.println(" -> world is the world folder (e.g. .minecraft/worlds/Amar)");
			System.out.println(" -> ressource is the ressource folder, default is . (e.g. ./ressource)");
			System.out.println(" -> output is the output folder, default is . (e.g. /var/www/maps/)");
			System.out.println(" -> day/night modifies the daytime, default is day (e.g. day)");
			System.out.println(" -> radius is the radius of the drawn circle or rectangle in chunks, default is 150 (e.g. 157)");
			System.out.println(" -> circle/rect modifies the shape of the rendered map, default is circle (e.g. circle)");
			System.out.println(" -> cluster lets the program save images of clusters of chunks, default is off (e.g. cluster)");
			System.out.println(" -> clusterpath is the folder where the cluster images will be saved, default is output (e.g. /var/www/maps/cluster/)");
			System.out.println(" -> jsonoutput is the folder where a JSON file containing data is saved, default is clusterpath or, if that's not set, output (e.g. /var/www/maps/json/)");
			System.out.println(" The world folder must contain an minecraft anvil world");
			System.out.println(" The ressource folder must contain a colors.json and a biomes.json");
			System.out.println("or");
			System.out.println(" textures ressource");
			System.out.println(" -> Generated colors.json");
			System.out.println(" -> ressource is the ressource folder, default is . (e.g. ./ressource)");
			System.out.println(" The ressource folder must contain a textures.json and a raw folder containing all files from assets/minecraft/textures/blocks/ of a ressource pack.");
				
		}
	}
	
	public static void generateTextures(String[] args) {
		if(args.length >= 2) {
			ressourceFolder = args[1];
		}
		printTextureRenderOptions();
		File ressourceFile = new File(ressourceFolder);
		File rawFolder = new File(ressourceFile, "raw");
		
		File jsonFile = new File(ressourceFile, "textures.json");
		if(!jsonFile.exists()) exitWithError("Cannot find textures.json!");
	    
	    JSONObject textureJSON = null;
		try {
			FileInputStream jsonFis;
			jsonFis = new FileInputStream(jsonFile);
			byte[] data = new byte[(int)jsonFile.length()];
		    jsonFis.read(data);
		    jsonFis.close();
		    String source = new String(data, "UTF-8");
			textureJSON = new JSONObject(source);
			
		} catch (Exception e) {
			e.printStackTrace();
			exitWithError("Cannot read textures.json!");
		}
		
		JSONArray texturesArray = textureJSON.getJSONArray("textures");
		JSONArray colorArray = new JSONArray();
		
		for(int i = 0; i < texturesArray.length(); i++) {
			JSONObject texture = texturesArray.getJSONObject(i);
			int blockID = texture.getInt("i");
			int metaID = texture.getInt("s");
			String name = texture.getString("n");
			boolean green = texture.getInt("g") == 1;
			boolean visible = texture.getInt("v") == 1;
			int color = 0;
			
			if(visible) {
				File textureImgFile = new File(rawFolder, name + ".png");
				if(!textureImgFile.exists()) {
					exitWithError("Cannot find texture: " + name);
				}
				try {
					BufferedImage img = ImageIO.read(textureImgFile);
					int r = 0, g = 0, b = 0;
					int c = 0;
					
					for(int x = 0; x < img.getWidth(); x++) {
						for(int y = 0; y < img.getHeight(); y++) {
							int pixelColor = img.getRGB(x, y);
							if(getA(pixelColor) != 0) {
								c++;
								r += getR(pixelColor);
								g += getG(pixelColor);
								b += getB(pixelColor);
							}
						}
					}
					if(c == 0) {
						exitWithError("Found no color: " + name);
					}
					r /= c;
					g /= c;
					b /= c;
					/*if(green) {
						r = (int)(r * 0.25);
						b = (int)(b * 0.25);
					}*/
					color = getRGBA(r, g, b, 0);
					
				} catch (Exception e) {
					e.printStackTrace();
					exitWithError("Cannot open image file:" + name);
				}
			}
			
			JSONObject colorJSON = new JSONObject();
			colorJSON.put("i", blockID);
			colorJSON.put("m", metaID);
			colorJSON.put("c", color);
			colorJSON.put("v", (visible?1:0));
			colorJSON.put("g", (green?1:0));
			colorArray.put(colorJSON);
			
		}
		
		JSONObject colorObject = new JSONObject();
		colorObject.put("colors", colorArray);
	    
		try {
			File outputFile = new File(ressourceFile, "colors.json");
			FileWriter writer = new FileWriter(outputFile);
			colorObject.write(writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			exitWithError("Error writing color file!");
		}
		
		System.out.println("Wrote color.json");
		
	}
	
	private static int getRGBA(int r, int g, int b, int a) {
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	private static int getA(int color) {
		color = color & 0xff000000;
		return color >> 24;
	}
	
	private static int getR(int color) {
		color = color & 0x00ff0000;
		return color >> 16;
	}
	
	private static int getG(int color) {
		color = color & 0x0000ff00;
		return color >> 8;
	}
	
	private static int getB(int color) {
		color = color & 0x000000ff;
		return color;
	}
	
	private static void loadVisibleBlocks() {
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
			if(id >= maxID) maxID = id; 
		}
		blockVisibility = new boolean[maxID+1];
		for(int i = 0; i < arr.length(); i++) {
			JSONObject o = arr.getJSONObject(i);
			blockVisibility[o.getInt("i")] = (o.getInt("v") == 1); 
		}
	}
	
	public static void generateMap(String[] args) throws IOException {
		if(args.length >= 2) {
			worldFolder = args[1];
			if(args.length >= 3) {
				ressourceFolder = args[2];
				if(args.length >= 4) {
					outputFolder = args[3];
					if(args.length >= 5) {
						if(args[4].equals("night"))
							renderNight = true;
						if(args.length >= 6) {
							chunkRadius = Integer.parseInt(args[5]);
							if(args.length >= 7) {
								drawRect = (args[6].equals("rect"));
								if(args.length >= 8) {
									renderCluster = (args[7].equals("cluster"));
									if(args.length >= 9) {
										clusterFolder = args[8];
										if(args.length >= 10) {
											jsonOutputFolder = args[9];
										} else {
											jsonOutputFolder = clusterFolder;
										}
									} else {
										clusterFolder = outputFolder;
										jsonOutputFolder = outputFolder;
									}
								}
							}
						}
					}
				}
			}
		}
		else {
			exitWithError("Missing World Name");
		}
		
		printCurrentFolder();
		loadVisibleBlocks();
		
        File dir = new File(worldFolder);
        if (!dir.exists()) {
        	exitWithError("World does not exist: " + dir.getCanonicalPath());
        }
        
        Level level = new Level(worldFolder);
        worldName = level.getWorldName();
        spawnX = level.getSpawnX();
        spawnZ = level.getSpawnZ();
        spawnChunkX = spawnX / 16;
        spawnChunkZ = spawnZ / 16;
        
        printRenderOptions();
        
        JSONObject coordinatesJSON = new JSONObject();
        JSONObject spawnJSON = new JSONObject();
        spawnJSON.put("x", spawnX);
        spawnJSON.put("z", spawnZ);
        coordinatesJSON.put("spawn", spawnJSON);
        if(renderCluster) coordinatesJSON.put("clusterSize", clusterSize);
        
        BufferedImage image = createImage();
        ColorSource colorSource = new JSONColorSource(ressourceFolder);
        
        File regionDir = new File(worldFolder, "region");
        for(File regionFile : regionDir.listFiles()) {
        	ClusterChunk[][] clusterImages = null;
        	if(renderCluster) {
	        	clusterImages = new ClusterChunk[32 / clusterSize][32 / clusterSize];
	        	for(int x = 0; x < 32 / clusterSize; x++) {
	        		for(int z = 0; z < 32 / clusterSize; z++) {
	        			clusterImages[x][z] = null;
	        		}
	        	}
        	}
        	Region region = loadRegion(regionFile);
            renderRegion(region, image, colorSource, clusterImages);
            if(renderCluster) {
            	for(int x = 0; x < 32 / clusterSize; x++) {
            		for(int z = 0; z < 32 / clusterSize; z++) {
            			ClusterChunk c = clusterImages[x][z];
            			if(c != null)
            				saveClusterImage(c.getImage(), c.getX(), c.getZ());
            		}
            	}
            }
        }
        saveImage(image);
        try {
        	File outputfile = new File(jsonOutputFolder, "coordinates-" + worldName + "-" + (renderNight ? "night" : "day") + ".json");
			FileWriter writer = new FileWriter(outputfile);
			coordinatesJSON.write(writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			exitWithError("Error writing coordinates.json!");
		}
	}
	
	public static void renderRegion(Region region, BufferedImage image, ColorSource colorSource, ClusterChunk[][] clusterImages)  {
		for(int cx = 0; cx < regionSize; cx++) {
        	for(int cz = 0; cz < regionSize; cz++) {
        		if(region.hasChunk(cx, cz)) {
        			try {
        				Block[][] blocks = new Block[chunkSize][chunkSize];
						Chunk chunk = new Chunk(region.getChunk(cx, cz));
						int chunkX = chunk.getX();
						int chunkZ = chunk.getZ();
						int chunkXrel = chunkX - spawnChunkX;
						int chunkZrel = chunkZ - spawnChunkZ;
						if(renderCluster && clusterImages[getClusterCoord(cx)][getClusterCoord(cz)] == null) {
							clusterImages[getClusterCoord(cx)][getClusterCoord(cz)] = new ClusterChunk(createClusterImage(), ((int)(chunkX / clusterSize)), ((int)(chunkZ / clusterSize)));
						}
						if((drawRect && (Math.abs(chunkXrel) <= chunkRadius && Math.abs(chunkZrel) <= chunkRadius)) || (!drawRect && (chunkXrel * chunkXrel + chunkZrel * chunkZrel <= chunkRadius * chunkRadius))) {
							for(int x = 0; x < chunkSize; x++) {
								for(int z = 0; z < chunkSize; z++) {
									blocks[x][z] = chunk.getBlock(x, z);
								}
							}
							for(int x = 0; x < chunkSize; x++) {
								for(int z = 0; z < chunkSize; z++) {
									int ix = (chunkX + chunkRadius) * chunkSize + x - spawnX;
									int iz = (chunkZ + chunkRadius) * chunkSize + z - spawnZ;
									Block b = blocks[x][z];
									int c = colorSource.getColor(b.getBlockType(), b.getMetaData(), b.getHeight(), b.getLight(), b.getBiome());
									if(ix >= 0 && ix < image.getWidth() && iz >= 0 && iz < image.getHeight()) {
										image.setRGB(ix, iz, c);
									}
									if(renderCluster) {
										clusterImages[getClusterCoord(cx)][getClusterCoord(cz)].getImage().setRGB(getClusterInnerCoord(cx) + x, getClusterInnerCoord(cz) + z, c);
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Program.exitWithError("Unable to load and draw chunk!");
					}
        		}
        	}
        }
	}
	
	private static int getClusterCoord(int c) {
		return c / clusterSize;
	}
	
	private static int getClusterInnerCoord(int c) {
		return (c % clusterSize) * chunkSize;
	}
	
	public static void saveImage(BufferedImage image) {
	    try {
	    	File outputfile = new File(outputFolder, worldName + "-" + (renderNight ? "night" : "day") + ".png");
			ImageIO.write(image, "png", outputfile);
			System.out.println("Successfully written image to " + outputfile.getCanonicalPath());
		} catch (Exception e) {
			e.printStackTrace();
			exitWithError("Unable to save image!");
		}
	}
	
	public static void saveClusterImage(BufferedImage image, int x, int z) {
	    try {
	    	File outputfile = new File(clusterFolder, worldName + "-" + (renderNight ? "night" : "day") + "_" + x + "," + z + ".png");
	    	if(image == null) System.out.println("Image null!");
			ImageIO.write(image, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
			exitWithError("Unable to save image!");
		}
	} 
	
	public static BufferedImage createImage() {
		return new BufferedImage(2 * chunkRadius * 16, 2 * chunkRadius * 16, BufferedImage.TYPE_INT_ARGB);
	}
	
	public static BufferedImage createClusterImage() {
		return new BufferedImage(16 * clusterSize, 16 * clusterSize, BufferedImage.TYPE_INT_ARGB);
	}
	
	public static Region loadRegion(File regionFile) {
		Region region = new Region(regionFile);
		return region;
	}
	
	public static Region loadRegion(int x, int z) {
		Region region = new Region(worldFolder, x, z);
		return region;
	}
	
	public static void exitWithError(String error) {
		System.err.println("Stopping: " + error);
		System.exit(0);
	}
	
	static void printCurrentFolder() throws IOException {
		String current = new java.io.File( "." ).getCanonicalPath();
        System.out.println("Current directory is: " + current);
        System.out.println("World directory is: " + worldFolder);
	}
	
	static void printRenderOptions() {
		System.out.println("MapRend startup messages");
		System.out.println("WorldName: " + worldName);
		System.out.println("Ressourcefolder: " + ressourceFolder);
		System.out.println("Daytime: " + (renderNight ? "Night" : "Day"));
		System.out.println("Chunkradius: " + chunkRadius);
		System.out.println("Form: " + (drawRect ? "rect" : "circle"));
	}
	
	static void printTextureRenderOptions() {
		System.out.println("MapRend Texture Rendering startup messages");
		System.out.println("Ressource folder: " + ressourceFolder);
	}

}
