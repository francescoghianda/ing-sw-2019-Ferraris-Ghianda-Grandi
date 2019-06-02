package it.polimi.se2019.card.powerup;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.constants.GameColor;

import java.io.Serializable;
import java.util.HashMap;

public class PowerUpCard extends Card implements Serializable
{
	private static final HashMap<String, PowerUpCard> cards = new HashMap<>();

	private transient String originalId;
	private transient String script;
	private GameColor color;
	private Use use;

	public PowerUpCard()
	{
		super();
	}

	public static void addCard(String id, PowerUpCard card)
	{
		cards.putIfAbsent(id, card);
	}

	public static PowerUpCard findById(String id)
	{
		return cards.get(id);
	}

	public void execute(Player player)
	{

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
