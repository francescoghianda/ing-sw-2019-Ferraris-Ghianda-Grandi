package it.polimi.se2019.utils;

import it.polimi.se2019.map.Map;
import it.polimi.se2019.utils.map.MapDrawer;
import org.junit.Test;

public class MapDrawerTest
{

    @Test
    public void test()
    {
        Map map = Map.createMap(4);

        MapDrawer generator = new MapDrawer(15, 11);

        System.out.println(generator.drawMap(map));
    }
}
