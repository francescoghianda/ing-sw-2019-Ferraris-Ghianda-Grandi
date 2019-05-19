package it.polimi.se2019.utils.constants;

public enum GameColor
{

	RED("#FF0000"), WHITE("#666666"), YELLOW("#ffd700"), GREEN("#38471f"), BLUE("#006466"), PURPLE("#800080");

	private final String color;

	GameColor(String color)
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

	public String getColor()
	{
		return this.color;
	}

}
