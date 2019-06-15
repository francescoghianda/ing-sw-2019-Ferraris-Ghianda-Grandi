package it.polimi.se2019.map;

import it.polimi.se2019.card.ammo.AmmoCard;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.player.PlayerData;
import it.polimi.se2019.utils.constants.Ansi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Block implements Serializable
{
	public static final int UPPER_SIDE = 0;
	public static final int LOWER_SIDE = 1;
	public static final int RIGHT_SIDE = 2;
	public static final int LEFT_SIDE = 3;

	private Block[] doors;
	private final Room room;

	private AmmoCard ammoCard;
	private ArrayList<WeaponCard> weaponCards;

	private final boolean spawnPoint;

	private transient HashMap<Block, List<Path>> paths;

	private ArrayList<Player> players;

	private final Coordinates coordinates;

	private boolean drawBackground = false;

	public Block(boolean spawnPoint, int x, int y, Room room)
	{
		doors = new Block[4];
		paths = new HashMap<>();
		this.weaponCards = new ArrayList<>();
		this.players = new ArrayList<>();
		this.spawnPoint = spawnPoint;
		coordinates = new Coordinates(x, y);
		this.room = room;

	}

	public void addPathsTo(Block block, List<Path> paths)
	{
		this.paths.put(block, paths);
	}

	public List<Path> getAllPathsTo(Block block)
	{
		return this.paths.getOrDefault(block, new ArrayList<>());
	}

	public Path getRandomPathTo(Block block)
	{
		List<Path> paths = getAllPathsTo(block);
		return paths.isEmpty() ? null : paths.get(new Random().nextInt(paths.size()));
	}

	public boolean containsPlayer()
	{
		return !players.isEmpty();
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

	public boolean isAmmoCardPresent()
	{
		return ammoCard != null;
	}

	public boolean isAllWeaponsPresent()
	{
		return weaponCards.size() == 3;
	}

	public Coordinates getCoordinates()
	{
		return coordinates;
	}

	public boolean addWeaponCard(WeaponCard weaponCard)
	{
		if(weaponCards.size() >= 3)return false;

		weaponCards.add(weaponCard);
		return true;
	}

	public boolean isWeaponCardPresent()
	{
		return !weaponCards.isEmpty();
	}

	public void replaceWeaponCard(WeaponCard weaponCard1, WeaponCard weaponCard2)
	{
		if(!weaponCards.contains(weaponCard1))return;
		int index = weaponCards.indexOf(weaponCard1);
		weaponCards.remove(weaponCard1);
		weaponCards.add(index, weaponCard2);
	}

	public void removeWeaponCard(WeaponCard weaponCard)
	{
		weaponCards.remove(weaponCard);
	}

	public void removeAmmoCard()
	{
		ammoCard = null;
	}

	public void setAmmoCard(AmmoCard ammoCard)
	{
		this.ammoCard = ammoCard;
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

	public List<Block> getDoorsAsList()
	{
		List<Block> doorsList = new ArrayList<>();
		for(Block door : this.doors)
		{
			if(door != null)doorsList.add(door);
		}
		return doorsList;
	}

	public Block[] getDoors()
	{
		return this.doors;
	}

	public void setDoor(Block block)
	{
		int x = coordinates.getX();
		int y = coordinates.getY();

		if(!block.isNear(this))throw new NotNearBlockException();
		int side;
		if(x == block.getX() && y > block.getY())side = UPPER_SIDE;
		else if(x == block.getX() && y < block.getY())side = LOWER_SIDE;
		else if(y == block.getY() && x > block.getX())side = LEFT_SIDE;
		else side = RIGHT_SIDE;
		doors[side] = block;
	}

	public int getManhattanDistanceFrom(Block block)
	{
		return coordinates.getManhattanDistanceFrom(block.getCoordinates());
	}

	public int getDistanceFrom(Block block)
	{
		return paths.get(block).get(0).getLength();
	}

	public int getX()
	{
		return coordinates.getX();
	}

	public int getY()
	{
		return coordinates.getY();
	}

	public Room getRoom()
	{
		return this.room;
	}

	public AmmoCard getAmmoCard()
	{
		return this.ammoCard;
	}

	public boolean contaisGrabbable()
	{
		return ammoCard != null || !weaponCards.isEmpty();
	}


	public Block getBottomBlock()
	{
		if(getY() >= 2)return null;
		return room.getMap().getMapMatrix()[getY()+1][getX()];
	}

	public Block getUpperBlock()
	{
		if(getY() <= 0)return null;
		return room.getMap().getMapMatrix()[getY()-1][getX()];
	}

	public Block getRightBlock()
	{
		if(getX() >= 3)return null;
		return room.getMap().getMapMatrix()[getY()][getX()+1];
	}

	public Block getLeftBlock()
	{
		if(getX() <= 0)return null;
		return room.getMap().getMapMatrix()[getY()][getX()-1];
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
		if(block == null)return false;
		if(block.getX() == getX())
		{
			return block.getY() == getY() + 1 || block.getY() == getY() - 1;
		}
		else if(block.getY() == getY())
		{
			return block.getX() == getX() + 1 || block.getX() == getX() - 1;
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
		return "("+getX()+", "+getY()+") - "+room.getColor().name().charAt(0);
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


	public BlockData getData()
	{
		HashMap<Coordinates, Integer> distances = new HashMap<>();
		ArrayList<PlayerData> playersData = new ArrayList<>();

		paths.forEach((block, path) -> distances.put(new Coordinates(block.getX(), block.getY()), path.get(0).getLength()));
		players.forEach(player -> playersData.add(player.getData()));

		String ammoCardId = ammoCard == null ? null : ammoCard.getId();

		return new BlockData(getX(), getY(), spawnPoint, ammoCardId, new ArrayList<>(weaponCards), distances, playersData);
	}

}
