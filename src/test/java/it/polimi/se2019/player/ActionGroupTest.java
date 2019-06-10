package it.polimi.se2019.player;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.controller.Match;
import it.polimi.se2019.controller.MatchManager;
import it.polimi.se2019.network.rmi.server.RmiClientConnection;
import it.polimi.se2019.utils.constants.GameColor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ActionGroupTest
{

    private Player player;
    private Player player2;

    @Before
    public void setUp()
    {
        Match match = MatchManager.getInstance().getMatch();

        player = new Player(GameColor.BLUE, match.getGameController(), new RmiClientConnection(null, null));
        player2 = new Player(GameColor.PURPLE, match.getGameController(), new RmiClientConnection(null, null));
    }

    @Test
    public void getPossibleActionNormalModeTest()
    {


        List<ActionsGroup> actionsGroups = Arrays.asList(ActionsGroup.getPossibleActionGroups(player));

        Assert.assertTrue(actionsGroups.contains(ActionsGroup.FIRE));
        Assert.assertTrue(actionsGroups.contains(ActionsGroup.RUN));
        Assert.assertTrue(actionsGroups.contains(ActionsGroup.MOVE_AND_GRAB));
        Assert.assertFalse(actionsGroups.contains(ActionsGroup.FOUR_MOVES));
        Assert.assertFalse(actionsGroups.contains(ActionsGroup.TWO_MOVES_AND_GRAB));
        Assert.assertFalse(actionsGroups.contains(ActionsGroup.MOVE_AND_SHOOT));
        Assert.assertFalse(actionsGroups.contains(ActionsGroup.MOVE_RELOAD_SHOOT));
        Assert.assertFalse(actionsGroups.contains(ActionsGroup.THREE_MOVES_AND_GRAB));
        Assert.assertFalse(actionsGroups.contains(ActionsGroup.TWO_MOVES_AND_GRAB_FINAL_FRENZY));
        Assert.assertFalse(actionsGroups.contains(ActionsGroup.TWO_MOVES_RELOAD_SHOOT));
    }

    @Test
    public void getPossibleActionNormalModePlayerDamagedTest()
    {
        player.getGameBoard().addDamage(player2, 6);

        List<ActionsGroup> actionsGroups = Arrays.asList(ActionsGroup.getPossibleActionGroups(player));

        Assert.assertTrue(actionsGroups.contains(ActionsGroup.FIRE));
        Assert.assertTrue(actionsGroups.contains(ActionsGroup.RUN));
        Assert.assertTrue(actionsGroups.contains(ActionsGroup.MOVE_AND_GRAB));
        Assert.assertFalse(actionsGroups.contains(ActionsGroup.FOUR_MOVES));
        Assert.assertTrue(actionsGroups.contains(ActionsGroup.TWO_MOVES_AND_GRAB));
        Assert.assertTrue(actionsGroups.contains(ActionsGroup.MOVE_AND_SHOOT));
        Assert.assertFalse(actionsGroups.contains(ActionsGroup.MOVE_RELOAD_SHOOT));
        Assert.assertFalse(actionsGroups.contains(ActionsGroup.THREE_MOVES_AND_GRAB));
        Assert.assertFalse(actionsGroups.contains(ActionsGroup.TWO_MOVES_AND_GRAB_FINAL_FRENZY));
        Assert.assertFalse(actionsGroups.contains(ActionsGroup.TWO_MOVES_RELOAD_SHOOT));
    }
}
