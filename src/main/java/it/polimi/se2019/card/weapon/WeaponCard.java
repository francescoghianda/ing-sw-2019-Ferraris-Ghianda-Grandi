package it.polimi.se2019.card.weapon;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.cardscript.CardScriptExecutor;
import it.polimi.se2019.card.cost.Cost;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.player.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponCard extends Card
{
	private static final HashMap<String, WeaponCard> cards = new HashMap<>();

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

	public static WeaponCard findCardById(String id)
	{
		return cards.get(id);
	}

	public static void addCard(String id, WeaponCard card)
	{
		cards.putIfAbsent(id, card);
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
		optionalEffects.values().forEach(OptionalEffect::resetEnable);
	}

	public void reset()
	{
		scriptExecutor.reset();
	}

	public void fire(Player player)throws CanceledActionException
	{
		if(!isLoad)throw new CanceledActionException(CanceledActionException.Cause.IMPOSSIBLE_ACTION, "WEAPON NOT LOAD");
		if(scriptExecutor == null || !scriptExecutor.getContextPlayer().equals(player))createScriptExecutor(player);
		scriptExecutor.setScript(fireMode.equals(Mode.BASIC) ? basicModeScript : alternateModeScript);
		scriptExecutor.execute();
	}

	public boolean useOptionalEffect(Player player, OptionalEffect effect)throws CanceledActionException
	{
		if(!isLoad)throw new CanceledActionException(CanceledActionException.Cause.IMPOSSIBLE_ACTION);
		if(!effect.isEnabled())throw new CanceledActionException(CanceledActionException.Cause.IMPOSSIBLE_ACTION);
		if(scriptExecutor == null || !scriptExecutor.getContextPlayer().equals(player))createScriptExecutor(player);
		scriptExecutor.setScript(effect.getScript());
		scriptExecutor.execute();
		return true;
	}

	public void setFireMode(Mode fireMode)
	{
		if(fireMode == Mode.ALTERNATE_FIRE && !hasAlternateFireMode)return;
		this.fireMode = fireMode;
	}

	private void createScriptExecutor(Player player)
	{
		scriptExecutor = CardScriptExecutor.getWeaponScriptExecutor(player, this);
	}

	public void setLoad(boolean isLoad)
	{
		this.isLoad = isLoad;
	}

	public List<OptionalEffect> getEnabledOptionalEffects()
	{
		return this.optionalEffects.values().stream().filter(OptionalEffect::isEnabled).collect(Collectors.toList());
	}

	public Cost getBuyCost()
	{
		return buyCost;
	}

	public Cost getReloadCost()
	{
		return reloadCost;
	}

	public Cost getAlternateModeCost()
	{
		return alternateModeCost;
	}

	public void setBuyCost(int redCost, int blueCost, int yellowCost)
	{
		buyCost = new Cost(redCost, blueCost, yellowCost);
	}

	public boolean hasOptionalEffect()
	{
		return hasOptionalEffect;
	}

	public boolean hasAlternateFireMode()
	{
		return hasAlternateFireMode;
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

	public void setBasicModeScript(String basicModeScript)
	{
		this.basicModeScript = basicModeScript;
	}

	public void setAlternateModeScript(String alternateModeScript)
	{
		this.alternateModeScript = alternateModeScript;
		hasAlternateFireMode = true;
	}

	public void addOptionalEffect(OptionalEffect effect, String name)
	{
		optionalEffects.putIfAbsent(name, effect);
		hasOptionalEffect = true;
	}

	public OptionalEffect getOptionalEffect(String name)
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
