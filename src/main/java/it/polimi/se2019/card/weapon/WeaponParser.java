package it.polimi.se2019.card.weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WeaponParser
{
    private Scanner scanner;

    private ArrayList<WeaponCard> weaponCards;

    public WeaponParser()
    {
        this.scanner = new Scanner(getClass().getResourceAsStream("/weapon_cards"));
        this.weaponCards = new ArrayList<>();
    }

    public List<WeaponCard> parseCards()
    {
        weaponCards.clear();
        while(scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if(line.equals("card"))parseCard();
        }

        return weaponCards;
    }

    private void parseCard()
    {
        WeaponCard card = new WeaponCard();

        String line;

        while(!(line = removeSpaces(scanner.nextLine())).equalsIgnoreCase("end_card"))
        {
            if(line.startsWith("#") || line.isEmpty())continue;
            String[] token = line.split("=");

            switch (token[0].toLowerCase())
            {
                case "id":
                    card.setId(token[1]);
                    break;
                case "name":
                    card.setName(token[1]);
                    break;
                case "description":
                    card.setDescription(readDescription());
                    break;
                case "buy_cost":
                    setBuyCost(card, token[1]);
                    break;
                case "reload_cost":
                    setReloadCost(card, token[1]);
                    break;
                case "basic_mode":
                    card.setBasicModeScript(readScript("end_basic_mode"));
                    break;
                case "alternate_mode":
                    card.setHasAlternateFireMode(true);
                    card.setAlternateModeScript(readScript("end_alternate_mode"));
                    setAlternateModeCost(card, token[1]);
                    break;
                case "optional_effect":
                    addOptionalEffect(card, token[1]);
                    break;
            }
        }

        weaponCards.add(card);
    }

    private void addOptionalEffect(WeaponCard card, String name)
    {
        OptionalEffect effect = new OptionalEffect(name);
        card.addOptionalEffect(effect, name);

        String line;

        while(!(line = removeSpaces(scanner.nextLine())).equalsIgnoreCase("end_optional_effect"))
        {
            if(line.startsWith("#") || line.isEmpty())continue;
            String[] token = line.split("=");

            switch (token[0].toLowerCase())
            {
                case "cost":
                    setOptionalEffectCost(effect, token[1]);
                    break;
                case "enabled":
                    effect.setEnabled(Boolean.parseBoolean(token[1]));
                    break;
                case "script":
                    effect.setScript(readScript("end_script"));
                    break;
            }
        }
    }

    private void setOptionalEffectCost(OptionalEffect effect, String allCost)
    {
        int[] cost = parseCost(allCost);
        effect.setCost(cost[0], cost[1], cost[2]);
    }

    private String readScript(String endBlockMarker)
    {
        return readUntil(endBlockMarker).toLowerCase();
    }

    private String readDescription()
    {
        return readUntil("end_description");
    }

    private String readUntil(String endBlockMarker)
    {
        StringBuilder str = new StringBuilder();
        String line;
        while(!(line = scanner.nextLine()).trim().equalsIgnoreCase(endBlockMarker))
        {
            str.append(line).append('\n');
        }
        return str.toString();
    }

    private int[] parseCost(String allCost) // r b y
    {
        String[] costToken = allCost.split(",");
        int[] cost = new int[3];
        for(String costStr : costToken)
        {
            int c = 0;
            switch (costStr.charAt(1))
            {
                case 'r':
                    c = 0;
                    break;
                case 'b':
                    c = 1;
                    break;
                case 'y':
                    c = 2;
                    break;
            }
            cost[c] = Integer.parseInt(costStr.substring(0, 1));
        }
        return cost;
    }

    private void setBuyCost(WeaponCard card, String allCost)
    {
        int[] cost = parseCost(allCost);
        card.setBuyCost(cost[0], cost[1], cost[2]);
    }

    private void setReloadCost(WeaponCard card, String allCost)
    {
        int[] cost = parseCost(allCost);
        card.setReloadCost(cost[0], cost[1], cost[2]);
    }

    private void setAlternateModeCost(WeaponCard card, String allCost)
    {
        int[] cost = parseCost(allCost);
        card.setAlternateModeCost(cost[0], cost[1], cost[2]);
    }

    private String removeSpaces(String str)
    {
        StringBuilder newStr = new StringBuilder();
        for(char c : str.toCharArray())if(c != ' ')newStr.append(c);
        return newStr.toString();
    }

}
