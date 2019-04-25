package it.polimi.se2019.map;

import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Map
{
	private ArrayList<Room> map;
	private Scanner scanner;

	private Map()
	{
		this.map = new ArrayList<>();
	}

	public List<Room> getMap()
	{
		return map;
	}

	public static Map createMap()
	{
		return new Map().initMap();
	}

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
		Logger.debug("Map ready!");
		return this;
	}

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

	private void readDoors()
	{
		String[] doors = scanner.nextLine().split(";");
		for(String door : doors)
		{
			String[] blocks = door.split("-");
			Block block1 = findByColor(GameColor.parseColor(blocks[0].charAt(0))).getBlockAt(Integer.parseInt(""+blocks[0].charAt(1)));
			Block block2 = findByColor(GameColor.parseColor(blocks[1].charAt(0))).getBlockAt(Integer.parseInt(""+blocks[1].charAt(1)));
			block1.setDoor(block2);
			block2.setDoor(block1);
		}
	}

	private void createBlock(GameColor color, boolean spawnPoint, int x, int y)
	{
		Room room = findByColor(color);
		if(room == null)room = createRoom(color);
		room.addBlock(new Block(spawnPoint, x, y, room));
	}

	private Room createRoom(GameColor color)
	{
		Room room = new Room(color, this);
		map.add(room);
		return room;
	}

	private Room findByColor(GameColor color)
	{
		for(Room room : map)if(room.getColor().equals(color))return room;
		return null;
	}

	public Block[][] getMapMatrix()
	{
		Block[][] mapMatrix = new Block[3][4];
		for(Room room : map)
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
		Block[][] mapMatrix = getMapMatrix();
		for(int i = 0; i < mapMatrix.length; i++)
		{
			stringBuilder.append('\n');
			for(int j = 0; j < mapMatrix[i].length; j++)
			{
				if(mapMatrix[i][j] != null)stringBuilder.append(mapMatrix[i][j].toString()).append(" ");
				else stringBuilder.append("  ");
			}
		}
		return stringBuilder.toString();
	}

}
