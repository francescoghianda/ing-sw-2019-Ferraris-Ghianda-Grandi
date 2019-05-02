package it.polimi.se2019.map;

import it.polimi.se2019.card.Grabbable;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.constants.AnsiColor;

import java.util.ArrayList;
import java.util.List;

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

	private boolean drawBackground = false;

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

	public List<Player> getPlayers()
	{
		return this.players;
	}

	public boolean isSpawnPoint()
	{
		return this.spawnPoint;
	}

	/**
	 *
	 * @return return true if the block contains any door
	 */
	public boolean hasDoor()
	{
		for(Block door: doors)if(door != null)return true;
		return false;
	}

	/**
	 *
	 * @param side the side of the block
	 * @return true if there is a door in the side
	 */
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

	public Block getSideBlock(int side)
	{
		switch (side)
		{
			case UPPER_SIDE:
				return getUpperBlock();
			case LOWER_SIDE:
				return getBottomBlock();
			case RIGHT_SIDE:
				return getRightBlock();
			case LEFT_SIDE:
				return getLeftBlock();
			default:
				return null;
		}
	}

	/**
	 * return controls if the block is near to this block
	 * @param block the block near to this block
	 * @return return true if the block is near to thi block, false if not
	 */
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

	/**
	 * controls if the blocks are near and they are connected with a door
	 * @param block block connected with this block with a door
	 * @return return true if the block is near to this one and there is a door, return false if the block is near
	 * to ths block but there's not a door that connect them
	 */
	public boolean isConnected(Block block)
	{
		if(block == null)return false;
		if(!this.isNear(block))return false;
		if(room.equals(block.room))return true;
		for(Block door : doors)
		{
			if(block.equals(door))return true;
		}
		return false;
	}

	@Override
	public String toString()
	{
		return ""+room.getColor().name().charAt(0);
	}


	public boolean isInSameRoom(Block block)
	{
		return block != null && block.getRoom().equals(room);
	}

	public boolean isDoor(Block block)
	{
		if(block == null)return false;
		for(Block door : doors)if(door != null && door.equals(block))return true;
		return false;
	}

	/**
	 *
	 * @return
	 */
	String drawBlock()
	{
		StringBuilder stringBuilder = new StringBuilder();
		char[][] canvas = new char[8][12];

		for(int i = 0; i < canvas.length; i++)
			for(int j = 0; j < canvas[i].length; j++)canvas[i][j] = ' ';

		drawSide(canvas, UPPER_SIDE, 1, 0);
		drawSide(canvas, LOWER_SIDE, 1, 7);
		drawSide(canvas, RIGHT_SIDE, 11, 1);
		drawSide(canvas, LEFT_SIDE, 0, 1);

		drawCorners(canvas);

		for(int i = 0; i < canvas.length; i++)
		{
			for(int j = 0; j < canvas[i].length; j++)
			{
				if(drawBackground)
				{
					if(canvas[i][j] != ' ' && canvas[i][j] != '┆' && canvas[i][j] != '┄' && canvas[i][j] != '┼')stringBuilder.append(AnsiColor.convertColor(getRoom().getColor()));
					else if(canvas[i][j] == '┆' || canvas[i][j] == '┄' || canvas[i][j] == '┼')stringBuilder.append(AnsiColor.combineColor(AnsiColor.convertColorBackground(getRoom().getColor()), AnsiColor.BLACK));
					else stringBuilder.append(AnsiColor.convertColorBackground(getRoom().getColor()));
				}
				else
				{
					if(canvas[i][j] != ' ')stringBuilder.append(AnsiColor.convertColor(getRoom().getColor()));
				}

				stringBuilder.append(canvas[i][j]);
				stringBuilder.append(AnsiColor.RESET);
			}
			stringBuilder.append('\n');
		}


		return stringBuilder.toString();
	}

	private static final char ROOM_WALL_VERTICAL = '┃';
	private static final char ROOM_WALL_HORIZONTAL = '━';
	private static final char BLOCK_WALL_VERTICAL = '┆';
	private static final char BLOCK_WALL_HORIZONTAL = '┄';

	private void drawSide(char[][] canvas, int side, int x, int y)
	{
		Block sideBlock = getSideBlock(side);
		if(side == UPPER_SIDE || side == LOWER_SIDE)
		{
			char ch;
			if(this.isInSameRoom(sideBlock))ch = BLOCK_WALL_HORIZONTAL;
			else ch = ROOM_WALL_HORIZONTAL;
			for(int i = 0; i < 10; i++)
			{
				if(isDoor(sideBlock) && i >= 2 && i <= 7)
				{
					if(side == UPPER_SIDE && i == 2)canvas[y][x+i] = '┛';
					else if(side == UPPER_SIDE && i == 7)canvas[y][x+i] = '┗';
					else if(side == LOWER_SIDE && i == 2)canvas[y][x+i] = '┓';
					else if(side == LOWER_SIDE && i == 7)canvas[y][x+i] = '┏';
					else canvas[y][x+i] = ' ';
				}
				else canvas[y][x+i] = ch;
			}
		}
		else
		{
			char ch;
			if(this.isInSameRoom(sideBlock))ch = BLOCK_WALL_VERTICAL;
			else ch = ROOM_WALL_VERTICAL;
			for(int i = 0; i < 6; i++)
			{
				if(isDoor(sideBlock) && i >= 1 && i <= 4)
				{
					if(side == RIGHT_SIDE && i == 1)canvas[y+i][x] = '┗';
					else if(side == RIGHT_SIDE && i == 4)canvas[y+i][x] = '┏';
					else if(side == LEFT_SIDE && i == 1)canvas[y+i][x] = '┛';
					else if(side == LEFT_SIDE && i == 4)canvas[y+i][x] = '┓';
					else canvas[y+i][x] = ' ';
				}
				else canvas[y+i][x] = ch;
			}
		}
	}

	private void drawCorners(char[][] canvas)
	{
		Block upperBlock = getUpperBlock();
		Block bottomBlock = getBottomBlock();
		Block rightBlock = getRightBlock();
		Block leftBlock = getLeftBlock();

		//FIRST CORNER (UPPER-LEFT)
		if(!isInSameRoom(upperBlock) && !isInSameRoom(leftBlock)) canvas[0][0] = '┏';
		else if(!isInSameRoom(upperBlock) && isInSameRoom(leftBlock)) canvas[0][0] = '┯';
		else if(isInSameRoom(upperBlock) && isInSameRoom(leftBlock) && isInSameRoom(upperBlock.getLeftBlock())) canvas[0][0] = '┼';
		else if(isInSameRoom(upperBlock) && !isInSameRoom(leftBlock)) canvas[0][0] = '┠';

		//SECOND CORNER (UPPER-RIGHT)
		if(!isInSameRoom(upperBlock) && !isInSameRoom(rightBlock)) canvas[0][11] = '┓';
		else if(!isInSameRoom(upperBlock) && isInSameRoom(rightBlock)) canvas[0][11] = '┯';
		else if(isInSameRoom(upperBlock) && isInSameRoom(rightBlock) && isInSameRoom(upperBlock.getRightBlock())) canvas[0][11] = '┼';
		else if(isInSameRoom(upperBlock) && !isInSameRoom(rightBlock)) canvas[0][11] = '┨';

		//THIRD CORNER (BOTTOM-LEFT)
		if(!isInSameRoom(bottomBlock) && !isInSameRoom(leftBlock)) canvas[7][0] = '┗';
		else if(!isInSameRoom(bottomBlock) && isInSameRoom(leftBlock)) canvas[7][0] = '┷';
		else if(isInSameRoom(bottomBlock) && isInSameRoom(leftBlock) && isInSameRoom(bottomBlock.getLeftBlock())) canvas[7][0] = '┼';
		else if(isInSameRoom(bottomBlock) && !isInSameRoom(leftBlock)) canvas[7][0] = '┠';

		//FOURTH CORNER (BOTTOM-RIGHT)
		if(!isInSameRoom(bottomBlock) && !isInSameRoom(rightBlock)) canvas[7][11] = '┛';
		else if(!isInSameRoom(bottomBlock) && isInSameRoom(rightBlock)) canvas[7][11] = '┷';
		else if(isInSameRoom(bottomBlock) && isInSameRoom(rightBlock) && isInSameRoom(bottomBlock.getRightBlock())) canvas[7][11] = '┼';
		else if(isInSameRoom(bottomBlock) && !isInSameRoom(rightBlock)) canvas[7][11] = '┨';
	}

}
