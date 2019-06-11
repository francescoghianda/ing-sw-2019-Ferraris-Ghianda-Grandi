package it.polimi.se2019.player;

import java.io.Serializable;

public enum Action implements Serializable
{

	MOVE(true), FIRE(true), GRAB(true), RELOAD(true), USE_POWER_UP(true), END_ROUND(false);

	public static final long serialVersionUID = 11L;

	private boolean executable;

	Action(boolean executable)
	{
		this.executable = executable;
	}

	public boolean isExecutable()
	{
		return executable;
	}
}
