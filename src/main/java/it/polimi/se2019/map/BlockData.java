package it.polimi.se2019.map;

import it.polimi.se2019.card.CardData;
import it.polimi.se2019.player.PlayerData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * It tranfers the data of a block from server to client
 */
public class BlockData implements Serializable
{
    public static final long serialVersionUID = 2L;

    private final int x;
    private final int y;
    private final boolean spawnPoint;

    private final String ammoCardId;
    private final ArrayList<CardData> weaponCards;
    private final HashMap<Coordinates, Integer> distances;
    private final ArrayList<PlayerData> players;

    /**
     *
     * @param x
     * @param y
     * @param spawnPoint
     * @param ammoCardId
     * @param weaponCards
     * @param distances
     * @param players
     */
    public BlockData(int x, int y, boolean spawnPoint, String ammoCardId, ArrayList<CardData> weaponCards, HashMap<Coordinates, Integer> distances, ArrayList<PlayerData> players)
    {
        this.x = x;
        this.y = y;
        this.spawnPoint = spawnPoint;
        this.ammoCardId = ammoCardId;
        this.weaponCards = weaponCards;
        this.distances = distances;
        this.players = players;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public boolean isSpawnPoint()
    {
        return this.spawnPoint;
    }

    public String getAmmoCardId()
    {
        return ammoCardId;
    }

    public ArrayList<CardData> getWeaponCards()
    {
        return weaponCards;
    }

    public HashMap<Coordinates, Integer> getDistances()
    {
        return distances;
    }

    public Coordinates getCoordinates()
    {
        return new Coordinates(x, y);
    }

    public ArrayList<PlayerData> getPlayers()
    {
        return players;
    }
}
