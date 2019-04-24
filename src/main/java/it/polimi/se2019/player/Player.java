package it.polimi.se2019.player;

import it.polimi.se2019.card.PowerUpCard;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.utils.constants.GameColor;

import java.util.ArrayList;


public class Player
{

	private final GameColor color;
	private boolean startingPlayer;
	private ArrayList<WeaponCard> weapons;
	private ArrayList<PowerUpCard> powerUps;
	private Block block;
	private GameBoard gameBoard;
	private ArrayList<Integer> executedAction;

	private final String nickname;
	private final CallbackInterface callbackInterface;
	private final NetworkServer server;

	public Player(GameColor color, String nickname, CallbackInterface callbackInterface, NetworkServer server)
	{
		weapons = new ArrayList<>();
		powerUps = new ArrayList<>();
		executedAction = new ArrayList<>();
		this.color = color;
		this.nickname = nickname;
		this.callbackInterface = callbackInterface;
		this.server = server;
	}

	public GameBoard getGameBoard()
	{
		return this.gameBoard;
	}

	public void addExecutedAction(Integer action)
	{
		this.executedAction.add(action);
	}

	public void resetExecutedAction()
	{
		this.executedAction.clear();
	}

	public Block[] getVisibleBlock()
	{
		ArrayList<Block> visibleBlocks = new ArrayList<>(block.getRoom().getBlocks());
		if(block.hasDoor())
		{
			Block[] doors = block.getDoors();
			for(Block door : doors)
			{
				if(door != null)visibleBlocks.addAll(door.getRoom().getBlocks());
			}
		}

		Block[] visible = new Block[visibleBlocks.size()];
		return visibleBlocks.toArray(visible);
	}

	public Integer[] getExecutedAction()
	{
		Integer[] executed = new Integer[executedAction.size()];
		return executedAction.toArray(executed);
	}

	public void addWeaponCard (WeaponCard weaponCard)
	{
		if(weapons.size() < 3)
		{
			weapons.add(weaponCard);
		}
	}

	public void addPowerUpCard (PowerUpCard powerUpCard)
	{
		if(powerUps.size() < 3)
		{
			powerUps.add(powerUpCard);
		}
	}

	public GameColor getColor()
	{
		return color;
	}

	public boolean isStartingPlayer()
	{
		return startingPlayer;
	}

	public Block getBlock()
	{
		return this.block;
	}


	public void setBlock(Block block)
	{
		this.block = block;
	}

	private boolean pathPossible(int currX, int currY, int destX, int destY)
	{
		return false;
	}

	private void execAction(ActionsGroup actionsGroup)
	{

	}

}
