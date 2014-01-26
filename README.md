## MapRend
MapRend is a tool to render top down maps of Minecraft worlds saved in the MCAnvil format. 
To render a world some files are required:

* colors.json
  
  Contains which color a block has, if it is visible and if it is modified by the biome green.
  With the argument textures MapRend generates this file based on a ressource pack and textures.json.
  
* biomes.json
  
  Contains the modifiers for leaves, grass, etc. per biome.

#### Textures
MapRend can create a set of used colors from a ressource pack. This is done by using the argument textures. It needs the file textures.json, which contains the texture name for all blocks, if they are visible and if the the biome green affects them.  
   
MapRend is written in Java.

#### Reference

MapRend uses Code by MojangAB for reading NBT files. (https://mojang.com)
MapRend uses Code by Douglas Crockford for reading and writing JSON files. (https://github.com/douglascrockford) 
 