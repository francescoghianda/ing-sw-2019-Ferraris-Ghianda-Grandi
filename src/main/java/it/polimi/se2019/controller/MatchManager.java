package it.polimi.se2019.controller;

import it.polimi.se2019.network.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MatchManager
{
    private static MatchManager instance;

    private List<Match> matchList;

    private MatchManager()
    {
        matchList = new ArrayList<>();
    }

    public static MatchManager getInstance()
    {
        if(instance == null)instance = new MatchManager();
        return instance;
    }

    private Match createNewMatch()
    {
        Match match = new Match();
        matchList.add(match);
        return match;
    }

    public Match getMatch()
    {
        List<Match> waitingPlayersMatchList = matchList.stream().filter(match -> match.getState() == Match.State.WAITING_FOR_PLAYERS).collect(Collectors.toList());
        if(waitingPlayersMatchList.isEmpty())return createNewMatch();
        return waitingPlayersMatchList.get(0);
    }

}
