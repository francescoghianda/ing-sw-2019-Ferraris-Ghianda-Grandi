package it.polimi.se2019;

import it.polimi.se2019.map.Map;
import it.polimi.se2019.map.Path;
import it.polimi.se2019.map.PathFinder;
import org.junit.Test;

import java.util.List;

public class PathFinderTest
{

    @Test
    public void getAllPathsToTest()
    {
        Map map = Map.createMap();
        PathFinder pathFinder = new PathFinder(map.getMapMatrix()[0][0]);

        List<Path> testPath = pathFinder.getAllPathsTo(map.getMapMatrix()[2][3]);

        System.out.print(testPath);
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
}
