package it.polimi.se2019.controller;

import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Map;
import it.polimi.se2019.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GameController
{
    private List<Player> players;
    private Map map;

    public GameController()
    {
        map = Map.createMap();
        players = new ArrayList<>();
    }

    public Player createPlayer()
    {
        return null;
    }

    public void movePlayer(Player player, Block block)
    {

    }
}
