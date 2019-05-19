package it.polimi.se2019.controller;

import org.junit.Test;

import static junit.framework.TestCase.fail;

public class GameControllerTest
{

    @Test
    public void startGameWithoutPlayerTest()
    {
        GameController controller = new GameController();

        try
        {
            controller.startGame();
            fail();
        }
        catch (StartGameWithoutPlayerException e)
        {
            assert true;
        }
    }

    @Test
    public void createTooManyPlayerTest()
    {
        /*GameController controller = new GameController();

        for(int i = 0; i < 5; i++)
        {
            controller.createPlayer(new RmiClientConnection(null, null, controller));
        }

        try
        {
            controller.createPlayer(new RmiClientConnection(null, null, controller));
            fail();
        }
        catch (TooManyPlayerException e)
        {
            assert true;
        }*/

    }
}
