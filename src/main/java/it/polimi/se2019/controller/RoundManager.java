package it.polimi.se2019.controller;

import it.polimi.se2019.player.Player;

import java.util.List;

public class RoundManager
{
    private List<Player> players;
    private int playerIndex;
    private int roundIndex;
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
        while (!nextPlayer.getClientConnection().isConnected() || !nextPlayer.getClientConnection().isLogged())nextPlayer = getNextPlayer();
        roundIndex++;
        return nextPlayer;
    }

    private Player getNextPlayer()
    {
        if(playerIndex >= players.size())
        {
            playerIndex = 0;
            if(roundIndex >= players.size())roundNumber++;
            roundIndex = 0;
        }

        Player currentPlayer = players.get(playerIndex);
        playerIndex++;
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
