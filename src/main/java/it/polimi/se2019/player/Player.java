package it.polimi.se2019.player;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.utils.constants.GameColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * creates a player
 */
public class Player implements Serializable
{
	private final GameColor color;
	private boolean startingPlayer;
	private ArrayList<WeaponCard> weapons;
	private ArrayList<PowerUpCard> powerUps;
	private Block block;
	private GameBoard gameBoard;
	private transient ArrayList<Integer> executedAction;
	private transient GameController gameController;

	private transient ArrayList<Player> damagedPlayers;

	private final transient ClientConnection clientConnection;

	/**
	 * creates and initializes the features of the player
	 * @param color color of the player's pawn
	 * @param gameController current gamecontroller of the game
	 * @param clientConnection connection that the player has established to the client
	 */
	public Player(GameColor color, GameController gameController, ClientConnection clientConnection)
	{
		if(color == null || gameController == null || clientConnection == null)throw new NullPointerException();
		weapons = new ArrayList<>();
		powerUps = new ArrayList<>();
		executedAction = new ArrayList<>();
		damagedPlayers = new ArrayList<>();
		gameBoard = new GameBoard();
		this.color = color;
		this.clientConnection = clientConnection;
		this.gameController = gameController;
	}

	public void reset()
	{
		executedAction.clear();
		damagedPlayers.clear();
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

	public void addDamagedPlayer(Player player)
	{
		if(!damagedPlayers.contains(player))this.damagedPlayers.add(player);
	}

	public List<Player> getDamagedPlayers()
	{
		return this.damagedPlayers;
	}

	public List<Block> getVisibleBlocks()
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

		return visibleBlocks;
	}

	public List<Player> getVisiblePlayers()
	{
		List<Block> visibleBlocks = getVisibleBlocks();
		List<Player> visiblePlayers = new ArrayList<>();
		for(Block visibleBlock : visibleBlocks)visiblePlayers.addAll(visibleBlock.getPlayers());
		return visiblePlayers;
	}

	public Integer[] getExecutedActions()
	{
		Integer[] executed = new Integer[executedAction.size()];
		return executedAction.toArray(executed);
	}

	public ClientConnection getClientConnection()
	{
		return this.clientConnection;
	}


	/**
	 * adds a weapon card to the gameboard of the player
	 * @param weaponCard weaponcard added to this pleayer
	 */
	public void addWeaponCard (WeaponCard weaponCard)
	{
		if(weapons.size() < 3)
		{
			weapons.add(weaponCard);
		}
	}

	/**
	 *  adds a powerup card to the gameboard of the player
	 * @param powerUpCard powerup card added to this pleayer
	 */
	public void addPowerUpCard (PowerUpCard powerUpCard)
	{
		if(powerUps.size() < 3)
		{
			powerUps.add(powerUpCard);
		}
	}

	public List<WeaponCard> getWeapons()
	{
		return this.weapons;
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
		block.addPlayer(this);
	}

	public NetworkMessageServer getResponseTo(NetworkMessageClient<?> message)
	{
		return clientConnection.getResponseTo(message);
	}

	public void sendMessageToClient(NetworkMessageClient<?> message)
	{
		clientConnection.sendMessageToClient(message);
	}

	public void notifyOtherClients(NetworkMessageClient<?> message)
	{
		clientConnection.notifyOtherClients(message);
	}

	public PlayerData getData()
	{
		ArrayList<Card> powerUpsCards = new ArrayList<>(powerUps);
		ArrayList<Card> weaponsCards = new ArrayList<>(weapons);
		return new PlayerData(color, weaponsCards, powerUpsCards, gameBoard.getData());
	}

}
