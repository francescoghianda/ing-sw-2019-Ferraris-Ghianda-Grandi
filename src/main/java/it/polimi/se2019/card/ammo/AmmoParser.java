package it.polimi.se2019.card.ammo;

import it.polimi.se2019.card.AmmoCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AmmoParser
{
    private AmmoParser(){}

    public static List<AmmoCard> parseCards()
    {
        Scanner scanner = new Scanner(AmmoParser.class.getResourceAsStream("/ammo_cards"));
        List<AmmoCard> cards = new ArrayList<>();

        String line;
        int i = 0;

        while (scanner.hasNextLine() && !(line = scanner.nextLine()).equals("END"))
        {
            if(line.isEmpty() || line.charAt(0) == '#')continue;
            String[] splitLine = line.trim().split(",");
            if(splitLine.length != 5)throw new AmmoSyntaxErrorException("Error at line "+i);
            cards.add(new AmmoCard(Integer.parseInt(splitLine[0].trim()), Integer.parseInt(splitLine[1].trim()), Integer.parseInt(splitLine[2].trim()), Boolean.parseBoolean(splitLine[3].trim()), splitLine[4].trim()));
            i++;
        }

        return cards;
    }

}
