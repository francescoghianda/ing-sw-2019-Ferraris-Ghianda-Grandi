package it.polimi.se2019.utils.constants;

import java.awt.*;

public enum GameColor
{

	RED(Color.RED), WHITE(Color.WHITE), YELLOW(Color.YELLOW), GREEN(Color.GREEN), BLUE(Color.BLUE), PURPLE(new Color(174, 0, 255));

	private final Color color;

	GameColor(Color color)
	{
		this.color = color;
	}

	public static GameColor parseColor(char c)
	{
		switch (c)
		{
			case 'B':
				return GameColor.BLUE;
			case 'G':
				return GameColor.GREEN;
			case 'W':
				return GameColor.WHITE;
			case 'R':
				return GameColor.RED;
			case 'Y':
				return GameColor.YELLOW;
			case 'P':
				return GameColor.PURPLE;
			default:
				return null;
		}
	}

	public Color getColor()
	{
		return this.color;
	}

}
