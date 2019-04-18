package it.polimi.se2019.map;

import it.polimi.se2019.utils.constants.GameColor;

import java.util.ArrayList;

public class Room
{

	private final GameColor color;
	private ArrayList<Block> blocks;

	public Room(GameColor color)
	{
		this.blocks = new ArrayList<>();
		this.color = color;
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

}
