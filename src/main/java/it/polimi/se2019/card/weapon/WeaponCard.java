package it.polimi.se2019.card.weapon;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.cardscript.CardScriptExecutor;
import it.polimi.se2019.card.Grabbable;
import it.polimi.se2019.card.cost.Cost;
import it.polimi.se2019.player.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponCard extends Card implements Grabbable, Serializable
{
	private Mode fireMode;

	private boolean hasAlternateFireMode;
	private boolean hasOptionalEffect;

	private Cost buyCost;
	private Cost reloadCost;
	private Cost alternateModeCost;

	private transient String basicModeScript;
	private transient String alternateModeScript;

	private transient HashMap<String, OptionalEffect> optionalEffects;

	private boolean isLoad;

	private transient CardScriptExecutor scriptExecutor;

	public WeaponCard()
	{
		optionalEffects = new HashMap<>();
		fireMode = Mode.BASIC;
	}

	@Override
	public void setId(String id)
	{
		super.setId("WPC"+id);
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

	public void setBuyCost(int redCost, int blueCost, int yellowCost)
	{
		buyCost = new Cost(redCost, blueCost, yellowCost);
	}

	public void setBuyCost(Cost buyCost)
	{
		this.buyCost = buyCost;
	}

	public void setReloadCost(Cost reloadCost)
	{
		this.reloadCost = reloadCost;
	}

	public void setReloadCost(int redCost, int blueCost, int yellowCost)
	{
		reloadCost = new Cost(redCost, blueCost, yellowCost);
	}

	public void setAlternateModeCost(int redCost, int blueCost, int yellowCost)
	{
		alternateModeCost = new Cost(redCost, blueCost, yellowCost);
	}

	public void setAlternateModeCost(Cost alternateModeCost)
	{
		this.alternateModeCost = alternateModeCost;
	}

	void setHasAlternateFireMode(boolean hasAlternateFireMode)
	{
		this.hasAlternateFireMode = hasAlternateFireMode;
	}

	public void setBasicModeScript(String basicModeScript)
	{
		this.basicModeScript = basicModeScript;
	}

	public void setAlternateModeScript(String alternateModeScript)
	{
		this.alternateModeScript = alternateModeScript;
	}

	public void addOptionalEffect(OptionalEffect effect, String name)
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
		return "WeaponCard #"+getId()+" - "+getName();
	}


	public enum Mode
	{
		BASIC, ALTERNATE_FIRE
	}


}
