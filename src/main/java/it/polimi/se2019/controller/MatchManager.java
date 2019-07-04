package it.polimi.se2019.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * implements the manager of the match
 */
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

    /**
     * creates a new match adding a new one from a list
     * @return the match created
     */
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
