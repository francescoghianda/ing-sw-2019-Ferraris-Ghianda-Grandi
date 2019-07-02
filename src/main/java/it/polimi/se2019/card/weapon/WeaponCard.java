package it.polimi.se2019.card.weapon;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.Script;
import it.polimi.se2019.card.cost.Cost;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.player.ImpossibleActionException;
import it.polimi.se2019.player.NotEnoughAmmoException;
import it.polimi.se2019.player.Player;

import java.io.IOException;
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

	private volatile boolean load;
	private volatile boolean used;

	private transient Executor executor;

	public WeaponCard()
	{
		super();
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
		return this.load;
	}

	public void reload()
	{
		setLoad(true);
		used = false;
		optionalEffects.values().forEach(OptionalEffect::resetEnable);
	}

	public void reset()
	{
		executor.reset();
		used = false;
		optionalEffects.forEach((s, effect) -> effect.resetEnable());
	}

	public void fire(Player player, GameController gameController)throws ImpossibleActionException, CanceledActionException
	{
		if(!load)throw new ImpossibleActionException(ImpossibleActionException.Cause.WEAPON_NOT_LOADED);
		if(executor == null || !executor.getContextPlayer().equals(player))createScriptExecutor(player);

		try
		{
			executor.executeScript(new Script(fireMode.equals(Mode.BASIC) ? basicModeScript : alternateModeScript), gameController);
		}
		finally
		{
			used = executor.isCardUsed();
		}
	}

	public void useOptionalEffect(Player player, OptionalEffect effect, GameController gameController)throws ImpossibleActionException, CanceledActionException
	{
		if(!load)throw new ImpossibleActionException(ImpossibleActionException.Cause.WEAPON_NOT_LOADED);
		if(!effect.isEnabled())throw new ImpossibleActionException(ImpossibleActionException.Cause.EFFECT_NOT_ENABLED);
		if(!player.getGameBoard().canPay(effect.getCost()))throw new ImpossibleActionException(ImpossibleActionException.Cause.INSUFFICIENT_AMMO);
		if(executor == null || !executor.getContextPlayer().equals(player))createScriptExecutor(player);

		try
		{
			executor.executeScript(new Script(effect.getScript()), gameController);
		}
		finally
		{
			used = executor.isCardUsed();
		}

		try
		{
			player.getGameBoard().pay(effect.getCost());
		}
		catch (NotEnoughAmmoException e)
		{
			throw new ImpossibleActionException(ImpossibleActionException.Cause.INSUFFICIENT_AMMO);
		}
	}

	public void setFireMode(Mode fireMode)
	{
		if(fireMode == Mode.ALTERNATE_FIRE && !hasAlternateFireMode)return;
		this.fireMode = fireMode;
	}

	public boolean isUsed()
	{
		return used;
	}

	private void createScriptExecutor(Player player)
	{
		executor = new Executor(player, this);
	}

	public void setLoad(boolean load)
	{
		this.load = load;
		super.setEnabled(load);
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

	public boolean hasEnabledOptionalEffect()
	{
		return !getEnabledOptionalEffects().isEmpty();
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

	@Override
	public String toString()
	{
		return "WeaponCard #"+getId()+" - "+getName()+" - "+(isLoad() ? "load" : "unload");
	}


	public enum Mode
	{
		BASIC, ALTERNATE_FIRE
	}

}
