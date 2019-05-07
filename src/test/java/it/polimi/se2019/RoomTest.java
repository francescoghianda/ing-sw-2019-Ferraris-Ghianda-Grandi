package it.polimi.se2019;

import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Map;
import it.polimi.se2019.map.Room;
import it.polimi.se2019.utils.constants.GameColor;

import java.util.List;

public class RoomTest
{
    private List<Room> rooms;
    public void setUp()
    {
        Map map = Map.createMap();
        rooms = map.getMap();

    }

    public void roomColorTest()
    {
        rooms.forEach(room->
        {
            List<Block> blocks = room.getBlocks();
            GameColor color = blocks.get(0).get;

        });

    }


}

