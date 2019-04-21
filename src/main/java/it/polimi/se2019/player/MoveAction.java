package it.polimi.se2019.player;

import it.polimi.se2019.map.Block;

public class MoveAction implements Action
{
	private final Block dest;

	public MoveAction(Block dest)
	{
		this.dest = dest;
	}

	public boolean execute(Player player)
	{
		if(player.getBlock().isConnected(dest))
		{
			player.setBlock(dest);
			return true;
		}
		return false;
	}

}
