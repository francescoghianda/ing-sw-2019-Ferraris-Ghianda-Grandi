package it.polimi.se2019.player;

import java.util.ArrayList;
import java.util.Arrays;

public class ActionsGroup
{
	public static final ActionsGroup RUN = new ActionsGroup();
	public static final ActionsGroup MOVE_AND_GRAB = new ActionsGroup();
	public static final ActionsGroup FIRE = new ActionsGroup();
	public static final ActionsGroup RELOAD = new ActionsGroup();

	private ArrayList<Action> actions;

	private ActionsGroup(Action... actions)
	{
		this.actions = new ArrayList<>();
		this.actions.addAll(Arrays.asList(actions));
	}

	public ArrayList<Action> getActions()
	{
		return this.actions;
	}

}
