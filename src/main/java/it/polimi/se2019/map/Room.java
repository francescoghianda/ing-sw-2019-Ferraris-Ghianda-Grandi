package it.polimi.se2019.map;

import it.polimi.se2019.utils.constants.GameColor;

import java.util.ArrayList;
import java.util.List;

public class Room
{

	private final GameColor color;
	private ArrayList<Block> blocks;
	private final Map map;

	/**
	 * creates a room of a spec color
	 * @param color color of the room that has to be created
	 * @param map ref to the map containing the room
	 */
	public Room(GameColor color, Map map)
	{
		this.blocks = new ArrayList<>();
		this.color = color;
		this.map = map;
	}

	public Map getMap()
	{
		return this.map;
	}

	/**
	 * adds a block to the room
	 * @param block block that has to be added
	 */
	public void addBlock(Block block)
	{
		this.blocks.add(block);
	}


	public GameColor getColor()
	{
		return this.color;
	}

	public List<Block> getBlocks()
	{
		return this.blocks;
	}

	public Block getBlockAt(int index)
	{
		return blocks.get(index);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Room)) return false;
		return this.color.equals(((Room)obj).getColor());
	}
}
