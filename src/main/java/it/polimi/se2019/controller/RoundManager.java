package it.polimi.se2019.controller;

import it.polimi.se2019.player.Player;

import java.util.List;

public class RoundManager
{
    private List<Player> players;
    private int index;
    private int roundNumber;

    public RoundManager(List<Player> players, Player firstPlayer)
    {
        this.players = players;
        roundNumber = 1;
        index = players.indexOf(firstPlayer);
    }

    public Player next()
    {
        Player currentPlayer = players.get(index);
        index++;
        if(index > players.size())
        {
            index = 0;
            roundNumber++;
        }
        return currentPlayer;
    }

    public int getRoundNumber()
    {
        return this.roundNumber;
    }

    public boolean isFirstRound()
    {
        return roundNumber == 1;
    }
}
