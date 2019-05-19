package it.polimi.se2019.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapData implements Serializable
{
    private final int mapNumber;
    private final BlockData[][] blocks;

    public MapData(int mapNumber, BlockData[][] blocks)
    {
        this.mapNumber = mapNumber;
        this.blocks = blocks;
    }

    public int getMapNumber()
    {
        return mapNumber;
    }

    public BlockData[][] getBlocks()
    {
        return blocks;
    }

    public List<BlockData> getBlocksAsList()
    {
        List<BlockData> blocksList = new ArrayList<>();
        for (BlockData[] block : blocks)
        {
            for (BlockData blockData : block)
            {
                if (blockData != null) blocksList.add(blockData);
            }
        }
        return blocksList;
    }
}
