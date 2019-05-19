package it.polimi.se2019.map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.fail;

public class BlockTest
{

    private Map map;
    private List<Block> blocks;

    @Before
    public void setUp()
    {
        map = Map.createMap();
        blocks = map.getAllBlocks();
    }

    @Test
    public void isConnectedNullParamTest()
    {
        Assert.assertFalse(blocks.get(0).isConnected(null));
    }

    @Test
    public void isNearNullParamTest()
    {
        Assert.assertFalse(blocks.get(0).isNear(null));
    }

    @Test
    public void isConnectedTest()
    {
        blocks.forEach(block1 -> blocks.forEach(block2 ->
        {
            if(block1.isConnected(block2))
            {
                Assert.assertTrue(block2.isConnected(block1));
            }
        }));
    }

    @Test
    public void isNearTest()
    {
        blocks.forEach(block1 -> blocks.forEach(block2 ->
        {
            if(block1.isNear(block2))
            {
                Assert.assertTrue(block2.isNear(block1));
            }
        }));
    }

    @Test
    public void isConnectedThenIsNearTest()
    {
        blocks.forEach(block1 -> blocks.forEach(block2 ->
        {
            if(block1.isConnected(block2))
            {
                Assert.assertTrue(block1.isNear(block2));
            }
        }));
    }

    @Test
    public void blockIsNotConnectedWithItselfTest()
    {
        blocks.forEach(block -> Assert.assertFalse(block.isConnected(block)));
    }

    @Test
    public void setDoorToBlockNotNearTest()
    {
        blocks.forEach(block1 -> blocks.forEach(block2 ->
        {
            if(!block1.isNear(block2))
            {
                try
                {
                    block1.setDoor(block2);
                    fail();
                }
                catch (NotNearBlockException e)
                {
                    assert true;
                }
            }
        }));
    }

    @Test
    public void getDoorsAsListNotContainsNullBlock()
    {
        blocks.forEach(block ->
        {
            List<Block> doors = block.getDoorsAsList();
            doors.forEach(Assert::assertNotNull);
        });

    }


}
