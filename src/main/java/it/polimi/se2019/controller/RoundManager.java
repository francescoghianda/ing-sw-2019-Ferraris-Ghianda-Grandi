package it.polimi.se2019.controller;

import it.polimi.se2019.player.Player;

import java.util.List;

public class RoundManager
{
    private List<Player> players;
    private int index;
    private int roundNumber;

    public RoundManager(List<Player> players)
    {
        this.players = players;
        roundNumber = 1;
    }

    public Player next()
    {
        Player nextPlayer = getNextPlayer();
        System.out.println(getRoundNumber());
        while (!nextPlayer.getClientConnection().isConnected())nextPlayer = getNextPlayer();
        return nextPlayer;
    }

    private Player getNextPlayer()
    {
        if(index >= players.size())
        {
            index = 0;
            roundNumber++;
        }
        Player currentPlayer = players.get(index);
        index++;

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
