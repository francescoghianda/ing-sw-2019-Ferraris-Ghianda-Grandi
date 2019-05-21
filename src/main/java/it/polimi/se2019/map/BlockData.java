package it.polimi.se2019.map;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.PlayerData;
import javafx.geometry.Point2D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class BlockData implements Serializable
{
    private final int x;
    private final int y;
    private final boolean spawnPoint;

    private final String ammoCardId;
    private final ArrayList<Card> weaponCards;
    private final HashMap<Bundle<Integer, Integer>, Integer> distances;
    private final ArrayList<PlayerData> players;

    public BlockData(int x, int y, boolean spawnPoint, String ammoCardId, ArrayList<Card> weaponCards, HashMap<Bundle<Integer, Integer>, Integer> distances, ArrayList<PlayerData> players)
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

    public ArrayList<Card> getWeaponCards()
    {
        return weaponCards;
    }

    public HashMap<Bundle<Integer, Integer>, Integer> getDistances()
    {
        return distances;
    }

    public ArrayList<PlayerData> getPlayers()
    {
        return players;
    }
}
