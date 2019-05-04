package it.polimi.se2019.map;

import java.util.*;
import java.util.stream.Collectors;

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
        Collections.shuffle(paths);
        return getMinValidPath(paths);
    }

    public List<Path> getAllPathsTo(Block endBlock)
    {
        this.endBlock = endBlock;
        List<Path> paths = findPaths();

        paths = paths.stream().filter(Path::isValid).sorted(Comparator.comparingInt(Path::getLength)).collect(Collectors.toList());
        int minLength = paths.get(0).getLength();
        paths = paths.stream().filter(path -> path.getLength() <= minLength).collect(Collectors.toList());

        return paths;
    }

    private List<Path> findPaths()
    {
        Block leftBlock = startBlock.getLeftBlock();
        Block rightBlock = startBlock.getRightBlock();
        Block upperBlock = startBlock.getUpperBlock();
        Block bottomBlock = startBlock.getBottomBlock();

        ArrayList<Path> paths = new ArrayList<>();

        paths.add(findPath(leftBlock, new Path()));
        paths.add(findPath(rightBlock, new Path()));
        paths.add(findPath(upperBlock, new Path()));
        paths.add(findPath(bottomBlock, new Path()));

        return paths;
    }

    private Path getMinValidPath(List<Path> paths)
    {
        Optional<Path> minPath = paths.stream().filter(Path::isValid).min(Comparator.comparingInt(Path::getLength));
        return minPath.orElse(new Path());
    }

    private Path findPath(Block block, Path path)
    {
        if(block == null || block.equals(startBlock))return path;
        if(block.equals(endBlock))
        {
            path.addBlock(block);
            path.setValid(true);
            return path;
        }
        path.addBlock(block);

        ArrayList<Path> paths = new ArrayList<>();

        Block leftBlock = block.getLeftBlock();
        Block rightBlock = block.getRightBlock();
        Block upperBlock = block.getUpperBlock();
        Block bottomBlock = block.getBottomBlock();

        if(!path.contains(leftBlock) && block.isConnected(leftBlock)) paths.add(findPath(leftBlock, path.clone()));
        if(!path.contains(rightBlock) && block.isConnected(rightBlock)) paths.add(findPath(rightBlock, path.clone()));
        if(!path.contains(upperBlock) && block.isConnected(upperBlock)) paths.add(findPath(upperBlock, path.clone()));
        if(!path.contains(bottomBlock) && block.isConnected(bottomBlock)) paths.add(findPath(bottomBlock, path.clone()));

        return getMinValidPath(paths);
    }
}
