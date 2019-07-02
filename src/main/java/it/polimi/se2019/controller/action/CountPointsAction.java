package it.polimi.se2019.controller.action;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.constants.GameMode;

import java.util.Map;
import java.util.Set;

public class CountPointsAction extends ControllerAction
{

    private Player deadPlayer;

    public CountPointsAction(GameController gameController, Player deadPlayer)
    {
        super(gameController);
        this.deadPlayer = deadPlayer;
    }

    @Override
    public void execute()
    {
        Set<Map.Entry<Player, Integer>> entries = deadPlayer.getGameBoard().getReceivedDamage().entrySet();

        if(!deadPlayer.isFinalFrenzyMode())deadPlayer.getFirstBloodPlayer().getGameBoard().addPoints(1);
        int maxPoints = deadPlayer.getGameBoard().getMaxPointsValue(deadPlayer.isFinalFrenzyMode());
        int processed = 0;

        while (!entries.isEmpty())
        {
            Map.Entry<Player, Integer> entry = getMax(entries);
            if(entry != null)
            {
                int points = maxPoints - (processed*2);
                entry.getKey().getGameBoard().addPoints(points > 1 ? points : 1);
                entries.remove(entry);
                processed++;
            }
        }

    }

    private Map.Entry<Player, Integer> getMax(Set<Map.Entry<Player, Integer>> entries)
    {
        Map.Entry<Player, Integer> max =  null;

        for(Map.Entry<Player, Integer> entry : entries)
        {
            if(max == null || entry.getValue() > max.getValue())max = entry;
        }

        return max;
    }
}
