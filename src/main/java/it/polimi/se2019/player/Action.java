package it.polimi.se2019.player;

import java.io.Serializable;

/**
 * defines the possible actions that a player can do
 */
public enum Action implements Serializable
{

	MOVE(true), FIRE(true), GRAB(true), RELOAD(true), USE_POWER_UP(true), END_ACTION(false), END_ROUND(false);

	public static final long serialVersionUID = 11L;

	private boolean executable;

	/**
	 * method that checks if teh specific action is executable or not
	 * @param executable
	 */
	Action(boolean executable)
	{
		this.executable = executable;
	}

	/**
	 *
	 * @return a boolean value based on the check of the executable of an action
	 */
	public boolean isExecutable()
	{
		return executable;
	}
}
