## MapRend
MapRend is a tool to render top down maps of Minecraft worlds saved in the MCAnvil format.
It can use all ressource packs, render day and night maps, shows biomes, height, height under oceans and is easy to update and customize! 

![Example, rendered by MapRend](http://tooldev.de/downloads/Arandur-day.png)
 
To render a world some files are required:

* colors.json
  
  Contains which color a block has, if it is visible and if it is modified by the biome green.
  With the argument textures MapRend generates this file based on a ressource pack and textures.json.
  
* biomes.json
  
  Contains the modifiers for leaves, grass, etc. per biome.

#### Textures
MapRend can create a set of used colors from a ressource pack. This is done by using the argument textures. It needs the file textures.json, which contains the texture name for all blocks, if they are visible and if the the biome green affects them.  
   
#### Compiling
MapRend is written in Java. The repository contains an Ant build file (build.xml), to compile it use:

    ant

#### Installation and Usage - Windows

See [issue #1](https://github.com/YSelfTool/MapRend/issues/1) for an explanation.

#### Installation and Usage - Mac OSX

Clone the repository using `git` and cd into it using Terminal (don't copy the `$`, it's just there to show you the default prompt):

```sh-session
$ git clone https://github.com/YSelfTool/MapRend.git
$ cd MapRend
```

Install `ant` (using [Homebrew](http://brew.sh/) it's just `brew install ant`) and run it:

```sh-session
$ brew install ant
$ ant
```

Locate your resource pack and extract it. Here's a magic command to do it (make sure to run it from within your `MapRend` folder). Replace the two `1.8.1` folders with whatever your version of Minecraft is.

```sh-session
$ jar xf ~/Library/Application\ Support/minecraft/versions/1.8.1/1.8.1.jar assets/minecraft/textures/blocks && mv assets/minecraft/textures/blocks raw && rm -rf assets
```

Generate your `colors.json`:

```sh-session
$ java -jar MapRend.jar textures . .
```

Make a folder to put all your images in:

```sh-session
$ mkdir images
```

Now, run the command to generate your map. Change `MyWorld` to whatever your world name is.

```sh-session
$ java -jar MapRend.jar map ~/Library/Application\ Support/minecraft/saves/MyWorld . images
$ open images
```

Finder will pop up and you should see your map!

**Contributed by [Jamon Holmgren](https://github.com/jamonholmgren)**

#### Frontends

Frontends for easier use are found in `frontends`. Currently there are:

* A graphical MapRend frontend for Mac OS X, contributed by **[Cameron Nichols](cameron.nichols@me.com)**

#### Reference

MapRend uses Code by [MojangAB](https://mojang.com) for reading NBT files.
MapRend uses Code by [Douglas Crockford](https://github.com/douglascrockford) for reading and writing JSON files. 
 
