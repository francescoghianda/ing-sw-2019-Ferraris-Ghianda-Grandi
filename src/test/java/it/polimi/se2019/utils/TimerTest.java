package it.polimi.se2019.utils;

import it.polimi.se2019.utils.timer.AlreadyExistingTimerException;
import it.polimi.se2019.utils.timer.NegativeTimeException;
import it.polimi.se2019.utils.timer.Timer;

import org.junit.Test;
import static junit.framework.TestCase.fail;

public class TimerTest
{

    @Test
    public void timerCreation()
    {
        Timer t = null;
        try
        {
            t = Timer.createTimer("timer1", 10);
        }
        catch (AlreadyExistingTimerException | NegativeTimeException e)
        {
            fail();
        }

        Timer.destroyTimer("timer1");
        assert true;
    }

    @Test
    public void negativeTimeTest()
    {
        Timer timer = null;
        try
        {
            timer = Timer.createTimer("timer1", -1);
            fail();
        }
        catch (NegativeTimeException e)
        {
            assert true;
        }
    }

    @Test
    public void alreadyExistingTimerExceptionTest()
    {
        Timer t1 = null;
        Timer t2 = null;
        try
        {
            t1 = Timer.createTimer("timer1", 20);
            t2 = Timer.createTimer("timer1", 10);
            fail();
        }
        catch (AlreadyExistingTimerException e)
        {
            Timer.destroyTimer("timer1");
            assert true;
        }
    }
}
