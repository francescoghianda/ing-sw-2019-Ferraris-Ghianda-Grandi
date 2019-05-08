package it.polimi.se2019;

import it.polimi.se2019.card.Cost;
import it.polimi.se2019.card.CostTooHighException;
import it.polimi.se2019.card.NegativeCostException;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class CostTest
{

    @Test
    public void correctInputTest()
    {
        try
        {
            Cost cost = new Cost(3, 2, 1);

            Assert.assertEquals(3, cost.getRedAmmo());
            Assert.assertEquals(2, cost.getBlueAmmo());
            Assert.assertEquals(1, cost.getYellowAmmo());
        }
        catch (Exception e)
        {
            fail();
        }
    }

    @Test
    public void tooHighCostExceptionTest()
    {
        Cost cost = null;
        try
        {
            cost = new Cost(4, 0, 0);
            fail();
        }
        catch (CostTooHighException e)
        {
            try
            {
                cost = new Cost(0, 4, 0);
                fail();
            }
            catch (CostTooHighException e1)
            {
                try
                {
                    cost = new Cost(0, 0, 4);
                    fail();
                }
                catch (CostTooHighException e2)
                {
                    assert true;
                }
            }
        }
    }

    @Test
    public void negativeCostExceptionTest()
    {
        Cost cost = null;
        try
        {
            cost = new Cost(-1, 0, 0);
            fail();
        }
        catch (NegativeCostException e)
        {
            try
            {
                cost = new Cost(0, -1, 0);
                fail();
            }
            catch (NegativeCostException e1)
            {
                try
                {
                    cost = new Cost(0, 0, -1);
                    fail();
                }
                catch (NegativeCostException e2)
                {
                    assert true;
                }
            }
        }
    }
}
