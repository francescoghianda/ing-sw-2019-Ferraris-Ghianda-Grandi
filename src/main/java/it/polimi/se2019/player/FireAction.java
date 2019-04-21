package it.polimi.se2019.player;

import it.polimi.se2019.card.WeaponCard;

public class FireAction implements Action
{
	private final Player playerToHit;
	private final WeaponCard weapon;

	public FireAction(Player playerToHit, WeaponCard weapon)
	{
		this.playerToHit = playerToHit;
		this.weapon = weapon;
	}

	@Override
	public boolean execute(Player player)
	{

		return false;
	}

}
