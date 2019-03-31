package it.polimi.se2019.player;

public abstract class Action
{

	public Action()
	{

	}

	public abstract void exec(Player player);
	
	public abstract Action getInstance();

}
