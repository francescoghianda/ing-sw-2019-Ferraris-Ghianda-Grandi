package it.polimi.se2019.card.powerup;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.Script;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.player.ImpossibleActionException;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.constants.GameColor;

import java.io.IOException;
import java.util.HashMap;

/**
 * Defines a powerupcard. It is a sublcass of card, it needs the specification of parameters like ID, script, color,
 * use, scriptExectuor, and a boolean value that indicates if it is used or not.
 */
public class PowerUpCard extends Card
{
	private static final HashMap<String, PowerUpCard> cards = new HashMap<>();

	private transient String originalId;
	private transient String script;
	private GameColor color;
	private Use use;
	private transient Executor scriptExecutor;
	private boolean used;

	public PowerUpCard()
	{
		super();
	}

	/**
	 * adds a specific powerup card
	 * @param id is the id of the powerup card that has to be added
	 * @param card
	 */
	public static void addCard(String id, PowerUpCard card)
	{
		cards.putIfAbsent(id, card);
	}

	/**
	 * search a powerupcard by its ID
	 * @param id
	 * @return the id of the card
	 */
	public static PowerUpCard findById(String id)
	{
		return cards.get(id);
	}

	public Use getUse()
	{
		return use;
	}

	/**
	 * applies the effect of the powerup card
	 * @param player
	 * @param gameController
	 * @throws CanceledActionException
	 * @throws ImpossibleActionException
	 */
	public void apply(Player player, GameController gameController) throws CanceledActionException, ImpossibleActionException
	{
		if(scriptExecutor == null || !scriptExecutor.getContextPlayer().equals(player))createScriptExecutor(player);
		try
		{
			scriptExecutor.executeScript(new Script(script), gameController);
		}
		finally
		{
			used = scriptExecutor.isCardUsed();
		}
	}

	/**
	 * the used powerupcard is not available now
	 */
	public void reset()
	{
		used = false;
	}

	public boolean isUsed()
	{
		return used;
	}

	public boolean isViewFinder()
	{
		return getName().equalsIgnoreCase("mirino");
	}

	public boolean isTagBackGrenade()
	{
		return getName().equalsIgnoreCase("granata venom");
	}

	private void createScriptExecutor(Player player)
	{
		//scriptExecutor = CardScriptExecutor.getPowerUpScriptExecutor(player, this);
		scriptExecutor = new Executor(player);
	}

	@Override
	public void setId(String id)
	{
		this.originalId = id;
		super.setId("PUC"+id);
	}

	public void setScript(String script)
	{
		this.script = script;
	}

	public void setColor(GameColor color)
	{
		this.color = color;
	}

	public GameColor getColor()
	{
		return this.color;
	}

	public void setUse(Use use)
	{
		this.use = use;
	}


	@Override
	public String toString()
	{
		return "PowerUpCard #"+getId()+" - "+getName()+" ("+getColor()+")";
	}

	@Override
	public PowerUpCard clone()
	{
		int cloneNumber = Integer.parseInt(originalId.split("_")[1]) + 1;

		PowerUpCard cloned = new PowerUpCard();
		cloned.script = script;
		cloned.use = use;
		cloned.color = color;
		cloned.setId(originalId.split("_")[0]+"_"+cloneNumber);
		cloned.setName(getName());
		cloned.setDescription(getDescription());
		return cloned;
	}


	public enum Use
	{
		ON_DAMAGE, ON_FIRE, ALWAYS
	}
}
