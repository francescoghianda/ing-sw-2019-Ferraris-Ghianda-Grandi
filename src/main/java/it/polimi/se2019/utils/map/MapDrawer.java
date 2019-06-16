package it.polimi.se2019.utils.map;

import it.polimi.se2019.card.ammo.AmmoCard;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Map;
import it.polimi.se2019.utils.constants.Ansi;
import it.polimi.se2019.utils.constants.GameColor;

import static it.polimi.se2019.map.Block.*;

public class MapDrawer
{
    private int blockWidth;
    private int blockHeight;
    private String emptyBlock;

    private boolean drawBackground;

    public MapDrawer(int blockWidth, int blockHeight)
    {
        this.blockWidth = blockWidth;
        this.blockHeight = blockHeight;
        drawBackground = true;
        emptyBlock = getEmptyBlock(this.blockWidth, this.blockHeight);
    }

    private String getEmptyBlock(int blockWidth, int blockHeight)
    {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < blockHeight; i++)
        {
            for(int j = 0; j < blockWidth; j++)
            {
                builder.append(' ');
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    public String drawMap(Map map)
    {
        StringBuilder stringBuilder = new StringBuilder();
        Block[][] mapMatrix = map.getMapMatrix();
        String[][] blockString = new String[3][4];

        for(int i = 0; i < mapMatrix.length; i++)
            for(int j = 0; j < mapMatrix[i].length; j++)
            {
                if(mapMatrix[i][j] != null)blockString[i][j] = drawBlock(mapMatrix[i][j]);
                else blockString[i][j] = emptyBlock;
            }

        for(int x = 0; x < 3; x++)
        {
            for(int i = 0; i < blockHeight; i++)
            {
                for(int j = 0; j < 4 ; j++)
                {
                    String line = blockString[x][j].split("\n")[i];
                    stringBuilder.append(line);
                }
                stringBuilder.append("\n");
            }
        }

        return stringBuilder.toString();
    }

    private String drawBlock(Block block)
    {
        StringBuilder stringBuilder = new StringBuilder();
        String[][] blockCanvas = new String[blockHeight][blockWidth];

        GameColor color = block.getRoom().getColor();

        for(int i = 0; i < blockCanvas.length; i++)
            for(int j = 0; j < blockCanvas[i].length; j++)
            {
                setCharacter(blockCanvas, ' ', j, i, color);
            }

        drawSide(block, blockCanvas, UPPER_SIDE, 1, 0);
        drawSide(block, blockCanvas, LOWER_SIDE, 1, blockHeight-1);
        drawSide(block, blockCanvas, RIGHT_SIDE, blockWidth-1, 1);
        drawSide(block, blockCanvas, LEFT_SIDE, 0, 1);

        drawCorners(block, blockCanvas);

        drawAmmoCard(block, blockCanvas);

        for (String[] line : blockCanvas)
        {
            for (String str : line)
            {
                stringBuilder.append(str);
            }
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }

    private void setCharacter(String[][] canvas, char ch, int x, int y, GameColor color)
    {
        String colorStr = "";
        if(drawBackground)
        {
            if(ch != ' ' && ch != '│' && ch != '─' && ch != '┼')colorStr = Ansi.convertColor(color);
            else if(ch == '│' || ch == '─' || ch == '┼')colorStr = Ansi.combineColor(Ansi.convertColorBackground(color), Ansi.BLACK);
            else colorStr = Ansi.convertColorBackground(color);
        }
        else
        {
            if(ch != ' ')colorStr = Ansi.convertColor(color);
        }

        canvas[y][x] = colorStr;
        canvas[y][x] += ch;
        canvas[y][x] += Ansi.RESET;
    }

    private void setCharacter(String[][] canvas, char ch, int x, int y, String chColor, String bgColor)
    {
        String colorStr = "";
        if(drawBackground)
        {
            colorStr = Ansi.combineColor(bgColor, chColor);
        }
        else
        {
            if(ch != ' ')colorStr = chColor;
        }

        canvas[y][x] = colorStr;
        canvas[y][x] += ch;
        canvas[y][x] += Ansi.RESET;
    }

    private void drawAmmoCard(Block block, String[][] canvas)
    {
        //block.setAmmoCard(new AmmoCard(2, 2, 1, true, "4"));

        if(!block.isSpawnPoint() && block.getAmmoCard() != null)
        {
            AmmoCard ammoCard = block.getAmmoCard();
            GameColor blockColor = block.getRoom().getColor();
            int j = 1;
            for(int i = 0; i < ammoCard.getBlueAmmo(); i++)
            {
                setCharacter(canvas, '♦', i+1, j, Ansi.BLUE, blockColor == GameColor.BLUE ? Ansi.BLACK : Ansi.convertColorBackground(blockColor));
            }
            if(ammoCard.getBlueAmmo() > 0)j++;
            for(int i = 0; i < ammoCard.getRedAmmo(); i++)
            {
                setCharacter(canvas, '♦', i+1, j, Ansi.RED, blockColor == GameColor.RED ? Ansi.BLACK : Ansi.convertColorBackground(blockColor));
            }
            if(ammoCard.getRedAmmo() > 0)j++;
            for(int i = 0; i < ammoCard.getYellowAmmo(); i++)
            {
                setCharacter(canvas, '♦', i+1, j, Ansi.YELLOW, blockColor == GameColor.YELLOW ? Ansi.BLACK : Ansi.convertColorBackground(blockColor));
            }
            if(ammoCard.getYellowAmmo() > 0)j++;
            if(ammoCard.isPickPowerUp())
            {
                setCharacter(canvas, '★', 1, j, Ansi.BLACK, Ansi.convertColorBackground(blockColor));
            }
        }
    }

    private static final char ROOM_WALL_VERTICAL = '║';
    private static final char ROOM_WALL_HORIZONTAL = '═';
    private static final char BLOCK_WALL_VERTICAL = '│';
    private static final char BLOCK_WALL_HORIZONTAL = '─';

    private void drawSide(Block block, String[][] canvas, int side, int x, int y)
    {
        Block sideBlock = block.getSideBlock(side);
        GameColor color = block.getRoom().getColor();

        if(side == UPPER_SIDE || side == LOWER_SIDE)
        {
            char ch;
            if(block.isInSameRoom(sideBlock))ch = BLOCK_WALL_HORIZONTAL;
            else ch = ROOM_WALL_HORIZONTAL;
            for(int i = 0; i < blockWidth-2; i++)
            {
                int doorStart = blockWidth/2-blockWidth/4-1;
                int doorEnd = blockWidth/2+blockWidth/4-1;

                if(block.isDoor(sideBlock) && i >= doorStart && i <= doorEnd)
                {
                    if(side == UPPER_SIDE && i == doorStart)setCharacter(canvas, '╝', x+i, y, color);
                    else if(side == UPPER_SIDE && i == doorEnd)setCharacter(canvas, '╚', x+i, y, color);
                    else if(side == LOWER_SIDE && i == doorStart)setCharacter(canvas, '╗', x+i, y, color);
                    else if(side == LOWER_SIDE && i == doorEnd)setCharacter(canvas, '╔', x+i, y, color);
                    else setCharacter(canvas, ' ', x+i, y, color);
                }
                else setCharacter(canvas, ch, x+i, y, color);
            }
        }
        else
        {
            char ch;
            if(block.isInSameRoom(sideBlock))ch = BLOCK_WALL_VERTICAL;
            else ch = ROOM_WALL_VERTICAL;
            for(int i = 0; i < blockHeight-2; i++)
            {
                int doorStart = blockHeight/2-blockHeight/4-1;
                int doorEnd = blockHeight/2+blockHeight/4-1;

                if(block.isDoor(sideBlock) && i >= doorStart && i <= doorEnd)
                {
                    if(side == RIGHT_SIDE && i == doorStart)setCharacter(canvas, '╚', x, y+i, color);
                    else if(side == RIGHT_SIDE && i == doorEnd)setCharacter(canvas, '╔', x, y+i, color);
                    else if(side == LEFT_SIDE && i == doorStart)setCharacter(canvas, '╝', x, y+i, color);
                    else if(side == LEFT_SIDE && i == doorEnd)setCharacter(canvas, '╗', x, y+i, color);
                    else setCharacter(canvas, ' ', x, y+i, color);
                }
                else setCharacter(canvas, ch, x, y+i, color);
            }
        }
    }

    private void drawCorners(Block block, String[][] canvas)
    {
        Block upperBlock = block.getUpperBlock();
        Block bottomBlock = block.getBottomBlock();
        Block rightBlock = block.getRightBlock();
        Block leftBlock = block.getLeftBlock();

        GameColor color = block.getRoom().getColor();

        //FIRST CORNER (UPPER-LEFT)
        if(!block.isInSameRoom(upperBlock) && !block.isInSameRoom(leftBlock)) setCharacter(canvas, '╔', 0, 0, color);
        else if(!block.isInSameRoom(upperBlock) && block.isInSameRoom(leftBlock)) setCharacter(canvas, '╤', 0, 0, color);
        else if(block.isInSameRoom(upperBlock) && block.isInSameRoom(leftBlock) && block.isInSameRoom(upperBlock.getLeftBlock())) setCharacter(canvas, '┼', 0, 0, color);
        else if(block.isInSameRoom(upperBlock) && !block.isInSameRoom(leftBlock)) setCharacter(canvas, '╟', 0, 0, color);

        //SECOND CORNER (UPPER-RIGHT)
        if(!block.isInSameRoom(upperBlock) && !block.isInSameRoom(rightBlock)) setCharacter(canvas, '╗', blockWidth-1, 0, color);
        else if(!block.isInSameRoom(upperBlock) && block.isInSameRoom(rightBlock))setCharacter(canvas, '╤', blockWidth-1, 0, color);
        else if(block.isInSameRoom(upperBlock) && block.isInSameRoom(rightBlock) && block.isInSameRoom(upperBlock.getRightBlock()))setCharacter(canvas, '┼', blockWidth-1, 0, color);
        else if(block.isInSameRoom(upperBlock) && !block.isInSameRoom(rightBlock))setCharacter(canvas, '╢', blockWidth-1, 0, color);

        //THIRD CORNER (BOTTOM-LEFT)
        if(!block.isInSameRoom(bottomBlock) && !block.isInSameRoom(leftBlock)) setCharacter(canvas, '╚', 0, blockHeight-1, color);
        else if(!block.isInSameRoom(bottomBlock) && block.isInSameRoom(leftBlock)) setCharacter(canvas, '╧', 0, blockHeight-1, color);
        else if(block.isInSameRoom(bottomBlock) && block.isInSameRoom(leftBlock) && block.isInSameRoom(bottomBlock.getLeftBlock())) setCharacter(canvas, '┼', 0, blockHeight-1, color);
        else if(block.isInSameRoom(bottomBlock) && !block.isInSameRoom(leftBlock)) setCharacter(canvas, '╟', 0, blockHeight-1, color);

        //FOURTH CORNER (BOTTOM-RIGHT)
        if(!block.isInSameRoom(bottomBlock) && !block.isInSameRoom(rightBlock)) setCharacter(canvas, '╝', blockWidth-1, blockHeight-1, color);
        else if(!block.isInSameRoom(bottomBlock) && block.isInSameRoom(rightBlock)) setCharacter(canvas, '╧', blockWidth-1, blockHeight-1, color);
        else if(block.isInSameRoom(bottomBlock) && block.isInSameRoom(rightBlock) && block.isInSameRoom(bottomBlock.getRightBlock())) setCharacter(canvas, '┼', blockWidth-1, blockHeight-1, color);
        else if(block.isInSameRoom(bottomBlock) && !block.isInSameRoom(rightBlock)) setCharacter(canvas, '╢', blockWidth-1, blockHeight-1, color);
    }





}