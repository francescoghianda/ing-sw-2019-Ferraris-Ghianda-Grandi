package it.polimi.se2019.player;

import it.polimi.se2019.utils.logging.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ActionsGroup
{
	public static final ActionsGroup RUN = new ActionsGroup(Action.MOVE_ACTION, Action.MOVE_ACTION, Action.MOVE_ACTION);
	public static final ActionsGroup MOVE_AND_GRAB = new ActionsGroup(Action.MOVE_ACTION, Action.GRAB_ACTION);
	public static final ActionsGroup FIRE = new ActionsGroup(Action.FIRE_ACTION);

	private final ArrayList<Integer> actions;

	public ActionsGroup(Integer... actions)
	{
		this.actions = new ArrayList<>();
		this.actions.addAll(Arrays.asList(actions));
	}

	public List<Integer> getActions()
	{
		return this.actions;
	}

	public static ActionsGroup[] clonedValues()
	{
		Field[] allFields = ActionsGroup.class.getFields();
		ArrayList<ActionsGroup> actionsGroups = new ArrayList<>();

		for(Field field : allFields)
		{
			if(field.getType().equals(ActionsGroup.class))
			{
				try
				{
					actionsGroups.add(((ActionsGroup) field.get(null)).clone());
				}
				catch (IllegalAccessException e)
				{
					Logger.exception(e);
				}
			}
		}

		ActionsGroup[] retArray = new ActionsGroup[actionsGroups.size()];

		return actionsGroups.toArray(retArray);
	}

	public static ActionsGroup[] getPossibleActionGroups(Player player)
	{
		Integer[] alreadyExecutedActions = player.getExecutedAction();

		ArrayList<ActionsGroup> groups = new ArrayList<>(Arrays.asList(ActionsGroup.clonedValues()));

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


	@Override
	public ActionsGroup clone()
	{
		Integer[] clonedActions = new Integer[actions.size()];
		clonedActions = actions.toArray(clonedActions);
		return new ActionsGroup(clonedActions);
	}

	@Override
	public String toString()
	{
		return actions.toString();
	}
}
