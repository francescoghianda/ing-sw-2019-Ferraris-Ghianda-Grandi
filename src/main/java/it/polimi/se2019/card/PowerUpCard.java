package it.polimi.se2019.card;

import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.constants.GameColor;

public class PowerUpCard extends Card implements Grabbable
{
	private String name;
	private String description;
	private String id;

	private String script;
	private GameColor color;

	public PowerUpCard()
	{
		super();
	}

	public void execute(Player player)
	{

	}

	public void setScript(String script)
	{
		this.script = script;
	}

	public void setColor(GameColor color)
	{
		this.color = color;
	}

	@Override
	public void grab(Player player)
	{

	}

	public String toString()
	{
		return "";
	}

}
