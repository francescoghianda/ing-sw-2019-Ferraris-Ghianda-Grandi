package it.polimi.se2019.card.weapon;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.Grabbable;
import it.polimi.se2019.player.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class WeaponCard extends Card implements Grabbable
{
	private Mode fireMode;

	private boolean hasAlternateFireMode;
	private boolean hasOptionalEffect;

	private int buyCostRed;
	private int buyCostYellow;
	private int buyCostBlue;

	private int reloadCostRed;
	private int reloadCostYellow;
	private int reloadCostBlue;

	private int alternateModeCostRed;
	private int alternateModeCostBlue;
	private int alternateModeCostYellow;

	private String basicModeScript;
	private String alternateModeScript;

	private HashMap<String, OptionalEffect> optionalEffects;

	public WeaponCard()
	{
		optionalEffects = new HashMap<>();
		fireMode = Mode.BASIC;
	}

	public void exec(Player player, int effect)
	{

	}

	@Override
	public void grab(Player player)
	{

	}

	void setBuyCost(int redCost, int blueCost, int yellowCost)
	{
		this.buyCostRed = redCost;
		this.buyCostBlue = blueCost;
		this.buyCostYellow = yellowCost;
	}

	void setReloadCost(int redCost, int blueCost, int yellowCost)
	{
		this.reloadCostRed = redCost;
		this.reloadCostBlue = blueCost;
		this.reloadCostYellow = yellowCost;
	}

	void setAlternateModeCost(int redCost, int blueCost, int yellowCost)
	{
		this.alternateModeCostRed = redCost;
		this.alternateModeCostBlue = blueCost;
		this.alternateModeCostYellow = yellowCost;
	}

	void setHasAlternateFireMode(boolean hasAlternateFireMode)
	{
		this.hasAlternateFireMode = hasAlternateFireMode;
	}

	void setBasicModeScript(String basicModeScript)
	{
		this.basicModeScript = basicModeScript;
	}

	void setAlternateModeScript(String alternateModeScript)
	{
		this.alternateModeScript = alternateModeScript;
	}

	void addOptionalEffect(OptionalEffect effect, String name)
	{
		optionalEffects.putIfAbsent(name, effect);
	}

	OptionalEffect getOptionaEffect(String name)
	{
		return optionalEffects.get(name);
	}

	public String toString()
	{
		return "";
	}


	public enum Mode
	{
		BASIC, ALTERNATE_FIRE
	}


}
