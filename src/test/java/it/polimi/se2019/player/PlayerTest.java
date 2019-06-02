package it.polimi.se2019.player;

import org.junit.Test;

import static junit.framework.TestCase.fail;

public class PlayerTest
{

    @Test
    public void nullParameterExceptionTest()
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
