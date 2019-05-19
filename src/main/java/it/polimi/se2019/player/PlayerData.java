package it.polimi.se2019.player;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.utils.constants.GameColor;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerData implements Serializable
{
    private final GameColor color;
    private final ArrayList<Card> weapons;
    private final ArrayList<Card> powerUps;
    private final GameBoardData gameBoard;
    
    public PlayerData(GameColor color, ArrayList<Card> weapons, ArrayList<Card> powerUps, GameBoardData gameBoard)
    {
        this.color = color;
        this.weapons = weapons;
        this.powerUps = powerUps;
        this.gameBoard = gameBoard;
    }

    public GameColor getColor()
    {
        return color;
    }

    public ArrayList<Card> getWeapons()
    {
        return weapons;
    }

    public ArrayList<Card> getPowerUps()
    {
        return powerUps;
    }

    public GameBoardData getGameBoard()
    {
        return gameBoard;
    }

    @Override
    public String toString()
    {
        return color.toString();
    }
}
