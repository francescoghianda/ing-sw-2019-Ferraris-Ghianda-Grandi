package it.polimi.se2019.player;

import it.polimi.se2019.utils.constants.GameMode;
import it.polimi.se2019.utils.logging.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * class which contains a group of actions that can be executed by players
 */
public class ActionsGroup
{
	public static final ActionsGroup RUN = new ActionsGroup(GameMode.NORMAL, 0, Action.MOVE_ACTION, Action.MOVE_ACTION, Action.MOVE_ACTION);
	public static final ActionsGroup MOVE_AND_GRAB = new ActionsGroup(GameMode.NORMAL, 0, Action.MOVE_ACTION, Action.GRAB_ACTION);
	public static final ActionsGroup FIRE = new ActionsGroup(GameMode.NORMAL, 0, Action.FIRE_ACTION);

	public static final ActionsGroup TWO_MOVES_AND_GRAB = new ActionsGroup(GameMode.NORMAL, 3, Action.MOVE_ACTION, Action.MOVE_ACTION, Action.GRAB_ACTION);
	public static final ActionsGroup MOVE_AND_SHOOT = new ActionsGroup(GameMode.NORMAL, 6, Action.MOVE_ACTION, Action.FIRE_ACTION);

	public static final ActionsGroup MOVE_RELOAD_SHOOT = new ActionsGroup(GameMode.FINAL_FRENZY_BEFORE_FP, 0, Action.MOVE_ACTION, Action.RELOAD_ACTION, Action.FIRE_ACTION);
	public static final ActionsGroup FOUR_MOVES = new ActionsGroup(GameMode.FINAL_FRENZY_BEFORE_FP, 0, Action.MOVE_ACTION, Action.MOVE_ACTION, Action.MOVE_ACTION, Action.MOVE_ACTION);
	public static final ActionsGroup TWO_MOVES_AND_GRAB_FINAL_FRENZY = new ActionsGroup(GameMode.FINAL_FRENZY_BEFORE_FP, 0, Action.MOVE_ACTION, Action.MOVE_ACTION, Action.GRAB_ACTION);

	public static final ActionsGroup TWO_MOVES_RELOAD_SHOOT = new ActionsGroup(GameMode.FINAL_FRENZY_AFTER_FP, 0, Action.MOVE_ACTION, Action.MOVE_ACTION, Action.RELOAD_ACTION, Action.FIRE_ACTION);
	public static final ActionsGroup THREE_MOVES_AND_GRAB = new ActionsGroup(GameMode.FINAL_FRENZY_AFTER_FP, 0, Action.MOVE_ACTION, Action.MOVE_ACTION, Action.MOVE_ACTION, Action.GRAB_ACTION);


	private final ArrayList<Integer> actions;
	private final GameMode gameMode;
	private final int damageToActivate;

	/**
	 * creates a group of actions
	 * @param gameMode it defines the mode of the game related to the group of actions
	 * @param damageToActivate damages that lead to a spec. group of actions
	 * @param actions actions that can be included in the group
	 */
	private ActionsGroup(GameMode gameMode, int damageToActivate, Integer... actions)
	{
		this.actions = new ArrayList<>();
		this.actions.addAll(Arrays.asList(actions));

		this.gameMode = gameMode;
		this.damageToActivate = damageToActivate;
	}

	public List<Integer> getActions() {
		return this.actions;
	}

		public static ActionsGroup[] clonedValues()
	{
		Field[] allFields = ActionsGroup.class.getFields();
		ArrayList<ActionsGroup> actionsGroups = new ArrayList<>();

		for (Field field : allFields) {
			if (field.getType().equals(ActionsGroup.class)) {
				try {
					actionsGroups.add(((ActionsGroup) field.get(null)).clone());
				} catch (IllegalAccessException e) {
					Logger.exception(e);
				}
			}
		}

		ActionsGroup[] retArray = new ActionsGroup[actionsGroups.size()];

		return actionsGroups.toArray(retArray);
	}

	/**
	 *
	 * @param player The player for whom you want to know possible actions
	 * @return The possible ActionGroups for the player
	 */

	public static ActionsGroup[] getPossibleActionGroups(Player player)
	{
		GameMode gameMode = player.getGameController().getGameMode();
		int totalDamage = player.getGameBoard().getTotalReceivedDamage();

		Integer[] alreadyExecutedActions = player.getExecutedActions();

		ArrayList<ActionsGroup> groups = new ArrayList<>(Arrays.asList(ActionsGroup.clonedValues()));

		groups.removeIf(group -> !group.gameMode.equals(gameMode) || group.damageToActivate > totalDamage);
		/*groups.forEach(group ->
		{
			if(!group.gameMode.equals(gameMode) || group.damageToActivate > totalDamage)groups.remove(group);
		});*/

		for(int j = 0; j < alreadyExecutedActions.length; j++)
		{
			Iterator<ActionsGroup> iterator = groups.iterator();
			while (iterator.hasNext())
			{
				ActionsGroup group = iterator.next();
				if(group.getActions().size() <= alreadyExecutedActions.length-j)iterator.remove();
				else
				{
					boolean found = false;
					for(int i = 0; i < group.getActions().size();)
					{
						if(group.getActions().get(i) == alreadyExecutedActions[j].intValue())
						{
							group.getActions().remove(i);
							found = true;
							break;
						}
						group.getActions().remove(i);
					}
					if(!found)iterator.remove();
				}
			}
		}

		ActionsGroup[] possibleActionGroups = new ActionsGroup[groups.size()];

		return groups.toArray(possibleActionGroups);

	}

	/**
	 *
	 * @param player The player for whom you want to know possible actions
	 * @return The distinct possible actions for the player
	 */

	public static Integer[] getPossibleActions(Player player)
	{
		ArrayList<Integer> possibleAction = new ArrayList<>();
		ActionsGroup[] groups = getPossibleActionGroups(player);

		for(ActionsGroup group : groups)
		{
			possibleAction.addAll(group.getActions());
		}

		possibleAction = possibleAction.stream().distinct().collect(Collectors.toCollection(ArrayList::new));

		Integer[] intArray = new Integer[possibleAction.size()];

		return possibleAction.toArray(intArray);
	}

	public boolean deepEquals(ActionsGroup group)
	{
		return super.equals(group);
	}

	@Override
	public boolean equals(Object group)
	{
		if(!(group instanceof ActionsGroup) || ((ActionsGroup) group).getActions().size() != getActions().size() ||
		!((ActionsGroup) group).gameMode.equals(gameMode) || ((ActionsGroup) group).damageToActivate != damageToActivate)return false;

		for(int i = 0; i < actions.size(); i++)
		{
			if(!actions.get(i).equals(((ActionsGroup) group).actions.get(i)))return false;
		}
		return true;
	}

	@Override
	public ActionsGroup clone()
	{
		Integer[] clonedActions = new Integer[actions.size()];
		clonedActions = actions.toArray(clonedActions);
		return new ActionsGroup(gameMode, damageToActivate, clonedActions);
	}

	@Override
	public String toString()
	{
		return actions.toString();
	}
}
