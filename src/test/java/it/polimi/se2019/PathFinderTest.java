package it.polimi.se2019;

import it.polimi.se2019.map.Map;
import it.polimi.se2019.map.Path;
import it.polimi.se2019.map.PathFinder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.fail;

public class PathFinderTest
{
    private Map map;
    private List<Path> testPaths;

    @Before
    public void setUp()
    {
        map = Map.createMap();

        PathFinder pathFinder = new PathFinder(map.getMapMatrix()[0][0]);
        testPaths = pathFinder.getAllPathsTo(map.getMapMatrix()[2][3]);
    }

    @Test
    public void getAllPathsToTest()
    {
        int expected = -1;

        switch (map.getSelectedMapNumber())
        {
            case 1:
            case 3:
                expected = 4;
                break;
            case 2:
                expected = 3;
                break;
            case 4:
                expected = 5;
                break;
        }

        Assert.assertEquals(expected, testPaths.size());
        Assert.assertEquals(5, testPaths.get(0).getLength());
    }

    @Test
    public void pathsLengthTest()
    {
        for(int i = 0; i < testPaths.size()-1; i++)
        {
            if(testPaths.get(i).getLength() != testPaths.get(i+1).getLength())fail();
        }
        assert true;
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
