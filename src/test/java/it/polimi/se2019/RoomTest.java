package it.polimi.se2019;

import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Map;
import it.polimi.se2019.map.Room;
import it.polimi.se2019.utils.constants.GameColor;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RoomTest
{
    private List<Room> rooms;

    @Before
    public void setUp()
    {
        Map map = Map.createMap();
        rooms = map.getMap();

    }

    @Test
    public void roomColorTest()
    {


    }


}

