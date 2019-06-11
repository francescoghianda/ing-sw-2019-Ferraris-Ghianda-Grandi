package it.polimi.se2019.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapData implements Serializable
{
    public static final long serialVersionUID = 3L;

    private final int mapNumber;
    private final BlockData[][] blocks;
    private final String cliMap;

    public MapData(int mapNumber, BlockData[][] blocks, String cliMap)
    {
        this.mapNumber = mapNumber;
        this.blocks = blocks;
        this.cliMap = cliMap;
    }

    public String getCliMap()
    {
        return this.cliMap;
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

    public BlockData getBlock(int x, int y)
    {
        if(x < 0 || y < 0)return null;
        return blocks[y][x];
    }

    public int getDistance(BlockData block1, BlockData block2)
    {
        if(block1.getCoordinates().equals(block2.getCoordinates()))return 0;
        Integer dist = block1.getDistances().get(new Coordinates(block2.getX(), block2.getY()));
        return dist == null ? 0 : dist;
    }
}
