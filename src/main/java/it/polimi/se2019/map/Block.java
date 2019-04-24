package it.polimi.se2019.map;

import it.polimi.se2019.card.Grabbable;
import it.polimi.se2019.player.Player;

import java.util.ArrayList;

public class Block
{
	public static final int UPPER_SIDE = 0;
	public static final int LOWER_SIDE = 1;
	public static final int RIGHT_SIDE = 2;
	public static final int LEFT_SIDE = 3;

	private Block[] doors;
	private final Room room;
	private Grabbable[] cards;
	private final boolean spawnPoint;

	private ArrayList<Player> players;

	private final int x;
	private final int y;

	public Block(boolean spawnPoint, int x, int y, Room room)
	{
		cards = new Grabbable[3];
		doors = new Block[4];
		this.spawnPoint = spawnPoint;
		this.x = x;
		this.y = y;
		this.room = room;
	}

	public void addPlayer(Player player)
	{
		this.players.add(player);
	}

	public void removePlayer(Player player)
	{
		this.players.remove(player);
	}

	public ArrayList<Player> getPlayers()
	{
		return this.players;
	}

	public boolean isSpawnPoint()
	{
		return this.spawnPoint;
	}

	public boolean hasDoor()
	{
		for(Block door: doors)if(door != null)return true;
		return false;
	}

	public boolean hasDoor(int side)
	{
		return doors[side] != null;
	}

	public Block getDoor(int side)
	{
		return doors[side];
	}

	public Block[] getDoors()
	{
		return this.doors;
	}

	public void setDoor(Block block)
	{
		int side;
		if(this.x == block.x && this.y > block.y)side = UPPER_SIDE;
		else if(this.x == block.x && this.y < block.y)side = LOWER_SIDE;
		else if(this.y == block.y && this.x > block.x)side = LEFT_SIDE;
		else side = RIGHT_SIDE;
		doors[side] = block;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public Room getRoom()
	{
		return this.room;
	}

	public Grabbable getCard(int index)
	{
		return cards[index];
	}

	public void setCard(Grabbable card, int index)
	{
		cards[index] = card;
	}

	public Block getBottomBlock()
	{
		if(y >= 2)return null;
		return room.getMap().getMapMatrix()[y+1][x];
	}

	public Block getUpperBlock()
	{
		if(y <= 0)return null;
		return room.getMap().getMapMatrix()[y-1][x];
	}

	public Block getRightBlock()
	{
		if(x >= 3)return null;
		return room.getMap().getMapMatrix()[y][x+1];
	}

	public Block getLeftBlock()
	{
		if(x <= 0)return null;
		return room.getMap().getMapMatrix()[y][x-1];
	}

	public boolean isNear(Block block)
	{
		if(block.getX() == x)
		{
			return block.getY() == y + 1 || block.getY() == y - 1;
		}
		else if(block.getY() == y)
		{
			return block.getX() == x + 1 || block.getX() == x - 1;
		}

		return false;
	}

	public boolean isConnected(Block block)
	{
		if(!this.isNear(block))return false;
		if(room.equals(block.room))return true;
		for(Block door : doors)
		{
			if(door.equals(block))return true;
		}
		return false;
	}

	@Override
	public String toString()
	{
		return ""+room.getColor().name().charAt(0);
	}

}
