package it.polimi.se2019.map;

import it.polimi.se2019.utils.constants.GameColor;

import java.util.ArrayList;

public class Room
{

	private final GameColor color;
	private ArrayList<Block> blocks;
	private final Map map;

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

	public void addBlock(Block block)
	{
		this.blocks.add(block);
	}


	public GameColor getColor()
	{
		return this.color;
	}

	public ArrayList<Block> getBlocks()
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
