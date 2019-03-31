package it.polimi.se2019.map;

public class Block
{

	public static final int UPPER_SIDE = 0;
	public static final int LOWER_SIDE = 1;
	public static final int RIGHT_SIDE = 2;
	public static final int LEFT_SIDE = 3;

	private Door[] doors;
	private Room room;
	private Grabbable card;

	public Block()
	{
	}


	public boolean hasDoor()
	{
		return false;
	}


	public boolean hasDoor(int side)
	{
		return false;
	}

	public void getDoor(int side)
	{

	}

	public Room getRoom()
	{
		return null;
	}

	public Grabbable getCard()
	{
		return null;
	}

}
