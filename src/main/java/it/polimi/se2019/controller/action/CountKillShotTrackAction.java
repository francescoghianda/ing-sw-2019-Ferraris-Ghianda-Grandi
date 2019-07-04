package it.polimi.se2019.controller.action;

import it.polimi.se2019.controller.Death;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.constants.GameColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * It containes a list of deaths and a max point and it counts the number of killshot
 */
public class CountKillShotTrackAction extends ControllerAction
{
    private static final int MAX_POINTS = 8;
    private List<Death> deaths;


    public CountKillShotTrackAction(GameController controller, List<Death> deaths)
    {
        super(controller);
        this.deaths = new ArrayList<>(deaths);
    }

    @Override
    public void execute()
    {
        int processed = 0;
        while (!deaths.isEmpty())
        {
            Player player = getMax();
            if(player == null)continue;

            int points = MAX_POINTS-(processed*2);
            points = points <= 0 ? 1 : points;

            player.getGameBoard().addPoints(points);

            remove(player);
            processed++;
        }

    }

    private Player getMax()
    {
        Map<GameColor, Long> map = deaths.stream().collect(Collectors.groupingBy(Death::getKillerColor, Collectors.counting()));
        Map.Entry<GameColor, Long> max = getMax(map.entrySet());
        return max == null ? null : gameController.findPlayerByColor(max.getKey()).orElse(null);
    }

    private Map.Entry<GameColor, Long> getMax(Set<Map.Entry<GameColor, Long>> entries)
    {
        Map.Entry<GameColor, Long> max =  null;

        for(Map.Entry<GameColor, Long> entry : entries)
        {
            if(max == null || entry.getValue() > max.getValue())max = entry;
        }

        return max;
    }

    private void remove(Player player)
    {
        deaths = deaths.stream().filter(death -> death.getKillerColor() != player.getColor()).collect(Collectors.toList());
    }

}
