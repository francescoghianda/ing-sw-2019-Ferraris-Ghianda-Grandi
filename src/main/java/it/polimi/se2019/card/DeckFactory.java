package it.polimi.se2019.card;

import it.polimi.se2019.utils.logging.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class DeckFactory<T extends Card>
{
	private Scanner scanner;

	public DeckFactory(File file)
	{
		try
		{
			scanner = new Scanner(new FileReader(file));
		}
		catch (FileNotFoundException e)
		{
			Logger.exception(e);
		}
	}

	public Deck<T> createDeck()
	{


		return null;
	}

}
