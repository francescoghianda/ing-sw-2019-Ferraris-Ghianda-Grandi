package it.polimi.se2019.map;


import java.util.*;
import java.util.stream.Collectors;

/**
 * it searches for paths that connects a block to another
 */
public class PathFinder
{
    private final Block startBlock;
    private Block endBlock;

    public PathFinder(Block startBlock)
    {

        if (startBlock == null) throw new NullPointerException("The start block cannot be null!");
        this.startBlock = startBlock;
    }

    public Path getPathTo(Block endBlock)
    {
        this.endBlock = endBlock;
        List<Path> paths = findPaths();
        //Collections.shuffle(paths);
        List<Path> minPaths = getMinValidPath(paths);
        return minPaths.get(new Random().nextInt(minPaths.size()));
    }

    /**
     * searches for all the paths that lead to a specific block from another block
     * @param endBlock
     * @return
     */
    public List<Path> getAllPathsTo(Block endBlock)
    {
        this.endBlock = endBlock;
        List<Path> paths = findPaths();

        return getMinValidPath(paths);
    }

    private List<Path> findPaths()
    {
        Block leftBlock = startBlock.getLeftBlock();
        Block rightBlock = startBlock.getRightBlock();
        Block upperBlock = startBlock.getUpperBlock();
        Block bottomBlock = startBlock.getBottomBlock();

        ArrayList<Path> paths = new ArrayList<>();

        if(startBlock.isConnected(leftBlock))paths.addAll(findPaths(leftBlock, new Path()));
        if(startBlock.isConnected(rightBlock))paths.addAll(findPaths(rightBlock, new Path()));
        if(startBlock.isConnected(upperBlock))paths.addAll(findPaths(upperBlock, new Path()));
        if(startBlock.isConnected(bottomBlock))paths.addAll(findPaths(bottomBlock, new Path()));

        return paths;
    }

    /**
     * it filters the list of the paths keeping the valid paths with the minimum length
     * @param paths
     * @return the found paths
     */
    private List<Path> getMinValidPath(List<Path> paths)
    {
        paths = paths.stream().filter(Path::isValid).sorted(Comparator.comparingInt(Path::getLength)).collect(Collectors.toList());
        if(!paths.isEmpty())
        {
            int minLength = paths.get(0).getLength();
            paths = paths.stream().filter(path -> path.getLength() <= minLength).collect(Collectors.toList());
        }
        return paths;
    }



    private List<Path> findPaths(Block block, Path path)
    {
        if(block == null || block.equals(startBlock))return Collections.singletonList(path);
        if(block.equals(endBlock))
        {
            path.addBlock(block);
            path.setValid(true);
            return Collections.singletonList(path);
        }
        path.addBlock(block);

        ArrayList<Path> paths = new ArrayList<>();

        Block leftBlock = block.getLeftBlock();
        Block rightBlock = block.getRightBlock();
        Block upperBlock = block.getUpperBlock();
        Block bottomBlock = block.getBottomBlock();

        if(!path.contains(leftBlock) && block.isConnected(leftBlock)) paths.addAll(findPaths(leftBlock, path.clone()));
        if(!path.contains(rightBlock) && block.isConnected(rightBlock)) paths.addAll(findPaths(rightBlock, path.clone()));
        if(!path.contains(upperBlock) && block.isConnected(upperBlock)) paths.addAll(findPaths(upperBlock, path.clone()));
        if(!path.contains(bottomBlock) && block.isConnected(bottomBlock)) paths.addAll(findPaths(bottomBlock, path.clone()));

        return getMinValidPath(paths);
    }
}
