package it.polimi.se2019.player;

import it.polimi.se2019.card.PowerUpCard;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.utils.constants.GameColor;

import java.util.ArrayList;
import java.util.List;


public class Player
{
	private final GameColor color;
	private boolean startingPlayer;
	private ArrayList<WeaponCard> weapons;
	private ArrayList<PowerUpCard> powerUps;
	private Block block;
	private GameBoard gameBoard;
	private ArrayList<Integer> executedAction;
	private GameController gameController;

	//private final CallbackInterface callbackInterface;
	private final ClientConnection server;

	public Player(GameColor color, GameController gameController, ClientConnection server)
	{
		weapons = new ArrayList<>();
		powerUps = new ArrayList<>();
		executedAction = new ArrayList<>();
		gameBoard = new GameBoard();
		this.color = color;
		//this.callbackInterface = callbackInterface;
		this.server = server;
		this.gameController = gameController;
	}

	public GameController getGameController()
	{
		return this.gameController;
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

	public Block[] getVisibleBlocks()
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

	public Player[] getVisiblePlayers()
	{
		Block[] visibleBlocks = getVisibleBlocks();
		List<Player> visiblePlayer = new ArrayList<>();
		for(Block visibleBlock : visibleBlocks)visiblePlayer.addAll(visibleBlock.getPlayers());
		Player[] players = new Player[visiblePlayer.size()];
		return visiblePlayer.toArray(players);
	}

	public Integer[] getExecutedActions()
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

	public void setAsStartingPlayer(boolean startingPlayer)
	{
		this.startingPlayer = startingPlayer;
	}

	public Block getBlock()
	{
		return this.block;
	}

	public void setBlock(Block block)
	{
		this.block = block;
	}

	public NetworkMessageServer getResponseTo(NetworkMessageClient<?> message)
	{
		return server.getResponseTo(message);
	}

	public void sendMessageToClient(NetworkMessageClient<?> message)
	{
		server.sendMessageToClient(message);
	}

	public void notifyOtherClients(NetworkMessageClient<?> message)
	{
		server.notifyOtherClients(message);
	}

}
