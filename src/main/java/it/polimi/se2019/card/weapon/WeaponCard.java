package it.polimi.se2019.card.weapon;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.CardScriptExecutor;
import it.polimi.se2019.card.Cost;
import it.polimi.se2019.card.Grabbable;
import it.polimi.se2019.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponCard extends Card implements Grabbable
{
	private Mode fireMode;

	private boolean hasAlternateFireMode;
	private boolean hasOptionalEffect;

	private Cost buyCost;
	private Cost reloadCost;
	private Cost alternateModeCost;

	private String basicModeScript;
	private String alternateModeScript;

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

	public void fire(Player player)throws WeaponNotLoadException
	{
		if(!isLoad)throw new WeaponNotLoadException(this);
		if(scriptExecutor == null || !scriptExecutor.getContextPlayer().equals(player))createScriptExecutor(player);
		scriptExecutor.setScript(fireMode.equals(Mode.BASIC) ? basicModeScript : alternateModeScript);
		scriptExecutor.execute();
	}

	public boolean useOptionalEffect(Player player, OptionalEffect effect)throws WeaponNotLoadException
	{
		if(!isLoad)throw new WeaponNotLoadException(this);
		if(!effect.isEnabled())return false;
		if(scriptExecutor == null || !scriptExecutor.getContextPlayer().equals(player))createScriptExecutor(player);
		scriptExecutor.setScript(effect.getScript());
		scriptExecutor.execute();
		return true;
	}

	private void createScriptExecutor(Player player)
	{
		scriptExecutor = new CardScriptExecutor(player);
		scriptExecutor.setWeapon(this);
	}

	public void setLoad(boolean isLoad)
	{
		this.isLoad = isLoad;
	}

	@Override
	public void grab(Player player)
	{

	}

	public List<OptionalEffect> getEnabledOptionalEffects()
	{
		return this.optionalEffects.values().stream().filter(OptionalEffect::isEnabled).collect(Collectors.toList());
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
		this.basicModeScript = basicModeScript;
	}

	void setAlternateModeScript(String alternateModeScript)
	{
		this.alternateModeScript = alternateModeScript;
	}

	void addOptionalEffect(OptionalEffect effect, String name)
	{
		optionalEffects.putIfAbsent(name, effect);
		hasOptionalEffect = true;
	}

	OptionalEffect getOptionaEffect(String name)
	{
		return optionalEffects.get(name);
	}

	public String toString()
	{
		return "WeaponCard "+getName();
	}


	public enum Mode
	{
		BASIC, ALTERNATE_FIRE
	}


}
