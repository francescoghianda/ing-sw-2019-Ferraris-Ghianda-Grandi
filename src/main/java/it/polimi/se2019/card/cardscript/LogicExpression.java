package it.polimi.se2019.card.cardscript;

import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Direction;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Defines the logic expression to filter players/blocks
 */
public class LogicExpression
{
    private static final int MIN_DISTANCE = 0;
    private static final int MAX_DISTANCE = 1;

    private String expression;
    private HashMap<String, LogicExpression> subExpressions;

    private Executor executor;

    /**
     * Constructs a new LogicExpression
     * @param expression is the specific string that defines the logic expression
     * @throws CardScriptErrorException when the expression is not well formed
     */
    public LogicExpression(String expression)
    {
        this.expression = expression.trim();
        this.subExpressions = new HashMap<>();
        checkBrackets();
        removeExternalBrackets();
        if(containsSubExpression()) extractSubExpressions();
    }

    /**
     * Receives a player/a block
     * @param executor is the executor of the command
     * @param object refers to a player or a block
     * @return true if it is valid according to the expression
     * @throws LogicExpressionEvaluationException
     */


    public boolean evaluate(Executor executor, Object object) throws LogicExpressionEvaluationException
    {
        this.executor = executor;
        String[] orElements = expression.split("\\|");
        return evaluateOrElements(orElements, object);
    }

    /** Evaluetes the OR parts of the logic expression
     * @param orElements are the OR elements of the logic expression
     * @param object is the player/block to be evaluated
     * @return true if one OR element return true
     * @throws LogicExpressionEvaluationException
     */
    private boolean evaluateOrElements(String[] orElements, Object object) throws LogicExpressionEvaluationException
    {
        for(String orElement : orElements)
        {
            String[] andElements = orElement.trim().split("&");
            if(evaluateAndElements(andElements, object))return true;
        }
        return false;
    }

    /**
     * Evalutes the AND parts of the logic expression
     * @param andElements are the AND elements of the logic expression
     * @param object is the player/block to be evaluated
     * @@return true if all AND elements return true
     * @throws LogicExpressionEvaluationException
     */
    private boolean evaluateAndElements(String[] andElements, Object object) throws LogicExpressionEvaluationException
    {
        for(String andElement : andElements)
        {
            if(!evaluateElement(andElement.trim(), object))return false;
        }
        return true;
    }

    /**
     * For each element, it checks if it is a negative(not)expression, if it is a sub-expression and then it
     * executes all the possible command of the logic expression and evalutes the block or the player of that specific command
     * of the command expression
     * @param element is the element of the logic expression
     * @param obj is the player/block to be evaluated
     * @return
     * @throws LogicExpressionEvaluationException
     */

    private boolean evaluateElement(String element, Object obj) throws LogicExpressionEvaluationException
    {
        boolean invert = false;
        boolean result;

        if(element.startsWith("!"))
        {
            invert = true;
            element = element.substring(1);
        }

        if(element.startsWith("sub-"))
        {
            result = subExpressions.get(element).evaluate(executor, obj);
            if(invert)return !result;
            else return result;
        }

        Player contextPlayer = executor.getContextPlayer();
        Block block = null;
        Player player = null;
        boolean isPlayer = false;
        if(obj instanceof Block)block = (Block)obj;
        else if(obj instanceof Player)
        {
            player = (Player)obj;
            isPlayer = true;
        }
        else throw new LogicExpressionEvaluationException();

        String[] split = element.split(" ");

        for(int i = 0; i < split.length; i++)split[i] = split[i].trim();

        switch (split[0])
        {
            case "any":
                result = true;
                break;
            case "visible":
                result = isPlayer ? isVisibleBy(getPlayer(split[1]), player, null) : isVisibleBy(getPlayer(split[1]), null, block);
                break;
            case "equal":
                result = isPlayer ? player.equals(getPlayer(split[1])) :
                        block.equals(getBlock(split[1]));
                break;
            case "maxd":
                result = isAtDistance(isPlayer ? player.getBlock() : block, getBlockFromVariable(split[1]), split[2], MAX_DISTANCE);
                break;
            case "mind":
                result = isAtDistance(isPlayer ? player.getBlock() : block, getBlockFromVariable(split[1]), split[2], MIN_DISTANCE);
                break;
            case "damaged":
                result = contextPlayer.getDamagedPlayers().contains(player);
                break;
            case "straight-line":
                result = isPlayer ? isInStraightLine(player.getBlock()) : isInStraightLine(block);
                break;
            case "same-direction-of":
                result = isInSameDirectionOf(isPlayer ? player.getBlock() : block, getBlockFromVariable(split[1]));
                break;
            case "direction":
                result = isInDirection(isPlayer ? player.getBlock() : block, split[1]);
                break;
            case "same-room-of":
                result = isInSameRoomOf(isPlayer ? player.getBlock() : block, getBlockFromVariable(split[1]));
                break;
            default:
                throw new LogicExpressionEvaluationException("Invalid keyword \""+split[0]+"\"");
        }
        if(invert)return !result;

        return result;
    }

    /**
     * return a boolean value based on the visibility of a player (p1) or a block (b1) from a (context)player
     * @param player is the player on which to check visibility. If null, return false
     * @param p1 is the player on which to check if it visible by the Player (context), if null, get the block.
     * @param b1 is the block on which to check if it visible by the specific player
     * @return a boolean value based on the result of the check( true if player/block is visible by the specific player)
     */
    private boolean isVisibleBy(Player player, Player p1, Block b1)
    {
        if(player == null)return false;
        if(p1 == null) return player.getVisibleBlocks().contains(b1);
        return player.getVisiblePlayers().contains(p1);
    }

    /**
     * checks if two specific blocks are in the same room
      * @param block1 is the first block to evaluate. If null, return false
     * @param block2 is the second block to evaluate.
     * @return true if the block1's room is the same of block2's room
     */
    private boolean isInSameRoomOf(Block block1, Block block2)
    {
        if(block2 == null)return false;
        return block1.getRoom().equals(block2.getRoom());
    }

    /**

     * receives the name of a variable already defined and returns
     * the block that is associated to that variable(contained in the executor)
     * @param varName is the name of the variable
     * @return the block associated to that variable
     * @throws LogicExpressionEvaluationException
     */
    private Block getBlockFromVariable(String varName) throws LogicExpressionEvaluationException
    {
        Block block;
        if(isPlayer(varName))
        {
            Player player = getPlayer(varName);
            block = player == null ? null : player.getBlock();
        }
        else if(isBlock(varName))block = getBlock(varName);
        else throw new LogicExpressionEvaluationException();
        return block;
    }

    /**
     * checks if the the direction of the block is in the same direction compared to the context player's block
     * @param block the block on which to evaluate the direction
     * @param direction direction to be evaluated
     * @return true if the block is in the same direction compared to the context player's block
     * @throws LogicExpressionEvaluationException
     */
    private boolean isInDirection(Block block, String direction) throws LogicExpressionEvaluationException
    {
        try
        {
            if(direction.equalsIgnoreCase("cardinal")) return block.getDirectionFrom(executor.getContextPlayerBlock()).isCardinalDirection();

            return block.getDirectionFrom(executor.getContextPlayerBlock()) == Direction.valueOf(direction.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            throw new LogicExpressionEvaluationException("Invalid direction \""+direction+"\"");
        }
    }

    /**
     * checks if the direction from a block is the same direction of another block.
     * @param block1 is the first block to evaluate
     * @param block2 is the second block to evaluate
     * @return
     */

    private boolean isInSameDirectionOf(Block block1, Block block2)
    {
        if(block2 == null)return false;

        Direction direction1 = block1.getDirectionFrom(executor.getContextPlayerBlock());
        Direction direction2 = block2.getDirectionFrom(executor.getContextPlayerBlock());

        return direction1 != Direction.NaD && direction1 == direction2;
    }

    /**
     * checks if the block is on a straight line from the context player's block
     * @param block is the block to evaluate
     * @return true if the block is on a straight line from the context player's block
     */
    private boolean isInStraightLine(Block block)
    {
        Player contextPlayer = executor.getContextPlayer();
        return block.getX() == contextPlayer.getBlock().getX() || block.getY() == contextPlayer.getBlock().getY();
    }

    private Player getPlayer(String varName)
    {
        return executor.getPlayer(varName).orElse(null);
    }

    private Block getBlock(String varName)
    {
        return executor.getBlock(varName).orElse(null);
    }

    private LogicExpressionEvaluationException newException()
    {
        return new LogicExpressionEvaluationException();
    }

    /**
     * check the composition of the string
     * @param str is the string to evaluate
     * @return true if the string consists of characters between 0 and 9
     */

    private boolean isDigit(String str)
    {
        if(str == null || str.isEmpty())return false;
        for(char ch : str.toCharArray())if(!Character.isDigit(ch))return false;
        return true;
    }

    /**
     * Check the distance between two blocks.
     * @param block1 is the first block to evaluate
     * @param block2 is the second block for calculate the distance
     * @param distance is the value of the calculated distance between the blocks
     * @param maxOrMin is a boolean value which indicates if the distance between the two block is the maximum (or minimum) distance
     * @return true if the distance is <= max.distance or >= min.distance
     * @throws LogicExpressionEvaluationException
     */
    private boolean isAtDistance(Block block1, Block block2, String distance, int maxOrMin) throws LogicExpressionEvaluationException
    {
        if(block2 == null)return false;
        if(!isDigit(distance))throw new LogicExpressionEvaluationException();
        int dist = Integer.parseInt(distance);

        return maxOrMin == MAX_DISTANCE ? block1.getManhattanDistanceFrom(block2) <= dist : block1.getManhattanDistanceFrom(block2) >= dist;
    }

    /**
     *
     * @param varName
     * @return true if the executor contains the player associated to that varname
     */
    private boolean isPlayer(String varName)
    {
        return executor.containsPlayer(varName);
    }

    /**
     *
     * @param varName
     * @return true if the executor contains the block associated to that varname
     */
    private boolean isBlock(String varName)
    {
        return executor.containsBlock(varName);
    }

    /**
     * Deletes external bracket from string of the logic expression
     */
    private void removeExternalBrackets()
    {
        if(expression.startsWith("(") && expression.endsWith(")"))
        {
            expression = expression.substring(1, expression.length()-1);
        }
    }

    /**
     * deletes the subexpression from the string of the logic expression
     */
    private void extractSubExpressions()
    {
        char[] chars = expression.toCharArray();

        for(int i = 0; i < chars.length; i++)
        {
            if(chars[i] == '(')
            {
                int endIndex = getEndIndex(i);
                String subExpression = expression.substring(i, endIndex);
                String name = "sub-"+subExpressions.size();
                subExpressions.put(name, new LogicExpression(subExpression));
                expression = expression.replace(subExpression, name);
                i += endIndex;
            }
        }
    }

    private int getEndIndex(int index)
    {
        char[] chars = expression.toCharArray();
        int count = 0;
        for(int i = index+1; i < chars.length; i++)
        {
            if(chars[i] == ')' && count == 0)return i+1;
            if(chars[i] == '(')count++;
            if(chars[i] == ')' && count > 0)count--;
        }
        return index+1;
    }

    private boolean containsSubExpression()
    {
        return expression.contains("(");
    }

    /**
     * It checks if the logic expression is well-parenthesized. Return a boolean value based on the result of the check
     */
    private void checkBrackets()
    {
        String error = "The expression is bad parenthesized";
        boolean firstBracket = true;
        int count = 0;
        char[] chars = expression.toCharArray();
        for(char ch : chars)
        {
            if(ch == '(')
            {
                if(firstBracket)firstBracket = false;
                count++;
            }
            if(ch == ')')
            {
                if(firstBracket)throw new CardScriptErrorException(error);
                count--;
            }
            if(count < 0)throw new CardScriptErrorException(error);
        }
        if(count != 0)throw new CardScriptErrorException(error);
    }

    /**
     * Filters the list using the logic expression passed as the parameter
     * @param list is the list that has to be filtered
     * @param logicExpression is the logic expression used to filter the list
     * @param executor executes the command
     * @param <T> the generic type of the list
     * @return the filtered list
     */
    public static <T> List<T> filter(List<T> list, LogicExpression logicExpression, Executor executor)
    {
        return list.stream().filter(player -> {
            try
            {
                return logicExpression.evaluate(executor, player);
            }
            catch (LogicExpressionEvaluationException e)
            {
                Logger.exception(e);
                return false;
            }
        }).collect(Collectors.toList());
    }
}
