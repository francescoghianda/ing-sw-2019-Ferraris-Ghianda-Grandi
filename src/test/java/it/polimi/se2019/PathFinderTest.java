package it.polimi.se2019;

import it.polimi.se2019.map.Map;
import it.polimi.se2019.map.Path;
import it.polimi.se2019.map.PathFinder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class PathFinderTest
{
    private Map map;

    @Before
    public void setUp()
    {
        map = Map.createMap();
    }

    @Test
    public void getAllPathsToTest()
    {
        /*PathFinder pathFinder = new PathFinder(map.getMapMatrix()[0][0]);
        List<Path> paths = pathFinder.getAllPathsTo(map.getMapMatrix()[2][3]);



        switch (map.getSelectedMapNumber())
        {
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
        }*/
    }

    @Test
    public void nullStartBlockTest()
    {
        try
        {
            PathFinder pathFinder = new PathFinder(null);
            assert false;
        }
        catch (NullPointerException e)
        {
            assert true;
        }
    }

    @Test
    public void equalStartEndDestinationBlockTest()
    {
        PathFinder pathFinder = new PathFinder(map.getAllBlocks().get(0));
        Assert.assertTrue(pathFinder.getAllPathsTo(map.getAllBlocks().get(0)).isEmpty());
    }
}
