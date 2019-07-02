package it.polimi.se2019.player;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.CardData;
import it.polimi.se2019.map.BlockData;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.utils.constants.GameColor;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerData implements Serializable
{
    public static final long serialVersionUID = 9L;

    private final String username;
    private final GameColor color;
    private final ArrayList<CardData> weapons;
    private final ArrayList<CardData> powerUps;
    private final GameBoardData gameBoard;
    private final int x;
    private final int y;
    private final boolean finalFrenzyMode;
    
    public PlayerData(String username, GameColor color, ArrayList<CardData> weapons, ArrayList<CardData> powerUps, GameBoardData gameBoard, int x, int y, boolean finalFrenzyMode)
    {
        this.username = username;
        this.color = color;
        this.weapons = weapons;
        this.powerUps = powerUps;
        this.gameBoard = gameBoard;
        this.x = x;
        this.y = y;
        this.finalFrenzyMode = finalFrenzyMode;
    }

    public String getUsername()
    {
        return username;
    }

    public GameColor getColor()
    {
        return color;
    }

    public ArrayList<CardData> getWeapons()
    {
        return weapons;
    }

    public ArrayList<CardData> getPowerUps()
    {
        return powerUps;
    }

    public GameBoardData getGameBoard()
    {
        return gameBoard;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public Coordinates getCoordinates()
    {
        return new Coordinates(x, y);
    }

    public boolean isFinalFrenzyMode()
    {
        return finalFrenzyMode;
    }

    @Override
    public String toString()
    {
        return color.toString();
    }
}
