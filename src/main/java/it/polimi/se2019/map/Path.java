package it.polimi.se2019.map;

import java.util.ArrayList;

public class Path
{
    private ArrayList<Block> blocks;
    private boolean valid;

    public Path()
    {
        blocks = new ArrayList<>();
    }

    public void addBlock(Block block)
    {
        blocks.add(block);
    }

    public int getLength()
    {
       return blocks.size();
    }

    public  boolean contains(Block block)
    {
        return blocks.contains(block);
    }

    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    public boolean isValid()
    {
        return this.valid;
    }

    @Override
    public String toString()
    {
        return blocks.toString()+" - Length: "+ blocks.size() + (valid ? " (valid)" : "");
    }

    @Override
    public Path clone()
    {
        Path clonedPath = new Path();
        clonedPath.valid = valid;
        clonedPath.blocks.addAll(blocks);
        return clonedPath;
    }


}
