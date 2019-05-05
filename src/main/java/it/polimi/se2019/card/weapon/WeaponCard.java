package it.polimi.se2019.card.weapon;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.CardScriptExecutor;
import it.polimi.se2019.card.Cost;
import it.polimi.se2019.card.Grabbable;
import it.polimi.se2019.player.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class WeaponCard extends Card implements Grabbable
{
	private Mode fireMode;

	private boolean hasAlternateFireMode;
	private boolean hasOptionalEffect;

	private Cost buyCost;
	private Cost reloadCost;
	private Cost alternateModeCost;

	private HashMap<String, OptionalEffect> optionalEffects;

	private boolean isLoad;

	private CardScriptExecutor scriptExecutor;

	public WeaponCard()
	{
		optionalEffects = new HashMap<>();
		fireMode = Mode.BASIC;
	}

	public boolean isLoad()
	{
		return this.isLoad;
	}

	public void reload()
	{
		this.isLoad = true;
	}

	public void fire(Player player)
	{
		if(scriptExecutor == null || !scriptExecutor.getContextPlayer().equals(player))scriptExecutor = new CardScriptExecutor(player);
		scriptExecutor.setScript(fireMode.getScript());
		scriptExecutor.execute();
	}

	public boolean useOptionalEffect(Player player, OptionalEffect effect)
	{
		if(!effect.isEnabled())return false;
		if(scriptExecutor == null || !scriptExecutor.getContextPlayer().equals(player))scriptExecutor = new CardScriptExecutor(player);
		scriptExecutor.setScript(effect.getScript());
		scriptExecutor.execute();
		return true;
	}

	@Override
	public void grab(Player player)
	{

	}

	Cost getBuyCost()
	{
		return buyCost;
	}

	Cost getReloadCost()
	{
		return reloadCost;
	}

	Cost getAlternateModeCost()
	{
		return alternateModeCost;
	}

	void setBuyCost(int redCost, int blueCost, int yellowCost)
	{
		buyCost = new Cost(redCost, blueCost, yellowCost);
	}

	void setReloadCost(int redCost, int blueCost, int yellowCost)
	{
		reloadCost = new Cost(redCost, blueCost, yellowCost);
	}

	void setAlternateModeCost(int redCost, int blueCost, int yellowCost)
	{
		alternateModeCost = new Cost(redCost, blueCost, yellowCost);
	}

	void setHasAlternateFireMode(boolean hasAlternateFireMode)
	{
		this.hasAlternateFireMode = hasAlternateFireMode;
	}

	void setBasicModeScript(String basicModeScript)
	{
		Mode.BASIC.setScript(basicModeScript);
	}

	void setAlternateModeScript(String alternateModeScript)
	{
		Mode.ALTERNATE_FIRE.setScript(alternateModeScript);
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
		BASIC, ALTERNATE_FIRE;

		private String script;

		void setScript(String script)
		{
			this.script = script;
		}

		String getScript()
		{
			return this.script;
		}
	}


}
