package it.polimi.se2019;

import it.polimi.se2019.player.Player;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class PlayerTest
{

    @Test
    public void nullParamaterExceptionTest()
    {
        Player player = null;
        try
        {
            player = new Player(null, null, null);
            fail();
        }
        catch (NullPointerException e)
        {
            assert true;
        }
    }
}
