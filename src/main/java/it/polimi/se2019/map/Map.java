package it.polimi.se2019.map;

import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Map
{
	private final char[][] cliMap;
	final int mapWidth = 48;
	final int mapHeight = 24;

	private ArrayList<Room> rooms;
	private Scanner scanner;

	private Block[][] mapMatrix;

	private Map()
	{
		cliMap = new char[mapHeight][mapWidth];
		this.rooms = new ArrayList<>();
	}

	public List<Room> getMap()
	{
		return rooms;
	}

	public static Map createMap()
	{
		return new Map().initMap();
	}


	/**
	 * initializes the rooms with a random selection
	 * @return return the rooms generated
	 */
	private Map initMap()
	{
		int mapNumber = 1 + new Random().nextInt(4);
		Logger.debug("Map "+mapNumber+" selected");
		scanner = new Scanner(getClass().getResourceAsStream("/maps"));
		String line;
		do
		{
			line = scanner.nextLine();
		}
		while (!line.equals("map"+ mapNumber));
		Logger.debug("Read map "+mapNumber);
		readMap();
		readDoors();
		createPaths();
		Logger.debug("Map ready!");
		return this;
	}

	/**
	 * calculate the paths for all blocks
	 */
	private void createPaths()
	{
		List<Block> blocks = getAllBlocks();
		blocks.forEach(startBlock ->
		{
			PathFinder pathFinder = new PathFinder(startBlock);
			blocks.forEach(endBlock ->
			{
				if(!startBlock.equals(endBlock))startBlock.addPathsTo(endBlock, pathFinder.getAllPathsTo(endBlock));
			});
		});
	}

	/**
	 * reads the map from the file
	 */
	private void readMap()
	{
		for(int i = 0; i < 3; i++)
		{
			String[] line = scanner.nextLine().split(" ");
			for(int j = 0; j < line.length; j++)
			{
				if(line[j].charAt(0) != 'N')createBlock(GameColor.parseColor(line[j].charAt(0)), line[j].charAt(1) == '1', j, i);
			}
		}
	}

	/**
	 *
	 * @return All the blocks in the entire map
	 */
	public List<Block> getAllBlocks()
	{
		List<Block> blocks = new ArrayList<>();
		rooms.forEach(room -> blocks.addAll(room.getBlocks()));
		return blocks;
	}

	/**
	 * reads the doors of the rooms
	 */
	private void readDoors()
	{
		String[] doors = scanner.nextLine().split(",");
		for(String door : doors)
		{
			String[] blocks = door.split("-");
			Block block1 = findByColor(GameColor.parseColor(blocks[0].charAt(0))).getBlockAt(Integer.parseInt(""+blocks[0].charAt(1)));
			Block block2 = findByColor(GameColor.parseColor(blocks[1].charAt(0))).getBlockAt(Integer.parseInt(""+blocks[1].charAt(1)));
			block1.setDoor(block2);
			block2.setDoor(block1);
		}

	}

	/**
	 * creates the blocks of the rooms
	 * @param color color of the block
	 * @param spawnPoint spawnpoint of the block
	 * @param x coord. x of the block
	 * @param y coord. y of the block
	 */

	private void createBlock(GameColor color, boolean spawnPoint, int x, int y)
	{
		Room room = findByColor(color);
		if(room == null)room = createRoom(color);
		room.addBlock(new Block(spawnPoint, x, y, room));
	}

	/**
	 * creates a room into the rooms
	 * @param color color of the new room
	 * @return the created room
	 */
	private Room createRoom(GameColor color)
	{
		Room room = new Room(color, this);
		rooms.add(room);
		return room;
	}

	/**
	 * finds a room by its color
	 * @param color color of the room
	 * @return null if the room doesn't exist
	 */
	private Room findByColor(GameColor color)
	{
		for(Room room : rooms)if(room.getColor().equals(color))return room;
		return null;
	}

	/**
	 * creates the matrix of the rooms created before
	 * @return the map in matrix format
	 */
	public Block[][] getMapMatrix()
	{
		if(mapMatrix != null)return mapMatrix;
		mapMatrix = new Block[3][4];
		for(Room room : rooms)
		{
			for(Block block : room.getBlocks())
			{
				if(block != null)mapMatrix[block.getY()][block.getX()] = block;
			}
		}
		return mapMatrix;
	}

	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		getMapMatrix();
		for (Block[] matrix : mapMatrix)
		{
			stringBuilder.append('\n');
			for (Block block : matrix)
			{
				if (block != null) stringBuilder.append(block.toString()).append(" ");
				else stringBuilder.append("  ");
			}
		}
		return stringBuilder.toString();
	}

	private static final String EMPTY_BLOCK = "            \n            \n            \n            \n            \n            \n            \n            \n";

	public String drawMap()
	{
		StringBuilder stringBuilder = new StringBuilder();
		getMapMatrix();
		String[][] blockString = new String[3][4];

		for(int i = 0; i < mapMatrix.length; i++)
			for(int j = 0; j < mapMatrix[i].length; j++)
			{
				if(mapMatrix[i][j] != null)blockString[i][j] = mapMatrix[i][j].drawBlock();
				else blockString[i][j] = EMPTY_BLOCK;
			}


		for(int x = 0; x < 3; x++)
		{
			for(int i = 0; i < 8; i++)
			{
				for(int j = 0; j < 4 ; j++)
				{
					String line = blockString[x][j].split("\n")[i];
					stringBuilder.append(line);
				}
				stringBuilder.append("\n");
			}
		}

		return stringBuilder.toString();
	}

}
