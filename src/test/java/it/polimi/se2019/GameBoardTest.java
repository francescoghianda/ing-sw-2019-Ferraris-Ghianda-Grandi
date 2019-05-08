package it.polimi.se2019;

import it.polimi.se2019.player.GameBoard;
import it.polimi.se2019.player.NegativeValueException;
import it.polimi.se2019.player.TooHighValueException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class GameBoardTest
{
    private GameBoard gameBoard;

    @Before
    public void setUp()
    {
        gameBoard = new GameBoard();
    }

    @Test
    public void negativeAmmoTest()
    {
        try
        {
            gameBoard.setRedAmmo(-1);
            fail();
        }
        catch (NegativeValueException e)
        {
            try
            {
                gameBoard.setBlueAmmo(-1);
                fail();
            }
            catch (NegativeValueException e1)
            {
                try
                {
                    gameBoard.setYellowAmmo(-1);
                    fail();
                }
                catch (NegativeValueException e2)
                {
                    assert true;
                }
            }
        }
    }

    @Test
    public void SetTooManyAmmoTest()
    {
        try
        {
            gameBoard.setRedAmmo(4);
            fail();
        }
        catch (TooHighValueException e)
        {
            try
            {
                gameBoard.setBlueAmmo(4);
                fail();
            }
            catch (TooHighValueException e1)
            {
                try
                {
                    gameBoard.setYellowAmmo(4);
                    fail();
                }
                catch (TooHighValueException e2)
                {
                    assert true;
                }
            }
        }
    }

    @Test
    public void negativeDamageTest()
    {
        try
        {
            gameBoard.addDamage(null, -1);
            fail();
        }
        catch (NegativeValueException e)
        {
            assert true;
        }
    }

    @Test
    public void negativeMarkerTest()
    {
        try
        {
            gameBoard.addMarker(null, -1);
            fail();
        }
        catch (NegativeValueException e)
        {
            assert true;
        }
    }


}
