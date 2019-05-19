package it.polimi.se2019.map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.fail;

public class MapTest
{
    private Map map;

    @Before
    public void setUp()
    {
        map = Map.createMap();
    }

    @Test
    public void doorTest()
    {
        System.out.println(map.drawMap());

        List<Block> blocks = map.getAllBlocks();
        blocks.forEach(block ->
        {
            List<Block> doors = Arrays.asList(block.getDoors());
            doors.forEach(door->
            {
                if(door != null && !Arrays.asList(door.getDoors()).contains(block)) fail();
            });
        });
        assert true;
    }

    @Test
    public void spawnpointTest(){
        List<Block> blocks = map.getAllBlocks();
        int spawnpoint = 0;
        for(Block block : blocks)
        {
            if(block.isSpawnPoint()) spawnpoint++;

        }
        Assert.assertEquals(3, spawnpoint);

    }

    @Test
    public void mapNumberTest()
    {
        int mapNumber = map.getSelectedMapNumber();
        if(mapNumber < 1 || mapNumber > 4)fail();
        assert true;
    }





















}
