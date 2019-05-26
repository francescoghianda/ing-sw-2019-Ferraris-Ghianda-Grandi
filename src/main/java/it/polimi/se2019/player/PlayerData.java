package it.polimi.se2019.player;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.map.BlockData;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.utils.constants.GameColor;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerData implements Serializable
{
    private final GameColor color;
    private final ArrayList<Card> weapons;
    private final ArrayList<Card> powerUps;
    private final GameBoardData gameBoard;
    private final int x;
    private final int y;
    
    public PlayerData(GameColor color, ArrayList<Card> weapons, ArrayList<Card> powerUps, GameBoardData gameBoard, int x, int y)
    {
        this.color = color;
        this.weapons = weapons;
        this.powerUps = powerUps;
        this.gameBoard = gameBoard;
        this.x = x;
        this.y = y;
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

    @Override
    public String toString()
    {
        return color.toString();
    }
}
