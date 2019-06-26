package it.polimi.se2019.card.cardscript;

import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Direction;
import it.polimi.se2019.player.Player;

import java.util.HashMap;

public class LogicExpression
{
    private static final int MIN_DISTANCE = 0;
    private static final int MAX_DISTANCE = 1;

    private String expression;
    private HashMap<String, LogicExpression> subExpressions;

    private Executor executor;

    public LogicExpression(String expression)
    {
        this.expression = expression;
        this.subExpressions = new HashMap<>();
        checkBrackets();
        removeExternalBrackets();
        if(containsSubExpression()) extractSubExpressions();
    }

    public boolean evaluate(Executor executor, Object object) throws LogicExpressionEvaluationException
    {
        this.executor = executor;
        String[] orElements = expression.split("\\|");
        return evaluateOrElements(orElements, object);
    }

    private boolean evaluateOrElements(String[] orElements, Object object) throws LogicExpressionEvaluationException
    {
        for(String orElement : orElements)
        {
            String[] andElements = orElement.split("&");
            if(evaluateAndElements(andElements, object))return true;
        }
        return false;
    }

    private boolean evaluateAndElements(String[] andElements, Object object) throws LogicExpressionEvaluationException
    {
        for(String andElement : andElements)
        {
            if(!evaluateElement(andElement, object))return false;
        }
        return true;
    }

    private boolean evaluateElement(String element, Object obj) throws LogicExpressionEvaluationException
    {
        boolean not = false;
        boolean result;
        if(element.startsWith("!"))
        {
            not = true;
            element = element.substring(1);
        }

        if(element.startsWith("sub-"))
        {
            result = subExpressions.get(element).evaluate(executor, obj);
            if(not)return !result;
            else return result;
        }

        Player contextPlayer = executor.getContextPlayer();
        Block block = null;
        Player player = null;
        boolean invert = false;
        boolean isPlayer = false;
        if(obj instanceof Block)block = (Block)obj;
        else if(obj instanceof Player)
        {
            player = (Player)obj;
            isPlayer = true;
        }
        else throw new LogicExpressionEvaluationException();

        String[] split = expression.split(" ");
        if(expression.startsWith("!"))
        {
            invert = true;
            split[0] = split[0].substring(1);
        }

        switch (split[0])
        {
            case "visible":
                result = isPlayer ? getPlayer(split[1]).getVisiblePlayers().contains(player) : getPlayer(split[1]).getVisibleBlocks().contains(block);
                break;
            case "equal":
                result = isPlayer ? player.equals(getPlayer(split[1])) :
                        block.equals(getBlock(split[1]));
                break;
            case "maxd":
                result = isAtDistance(obj, split[1], split[2], MAX_DISTANCE);
                break;
            case "mind":
                result = isAtDistance(obj, split[1], split[2], MIN_DISTANCE);
                break;
            case "damaged":
                result = contextPlayer.getDamagedPlayers().contains(player);
                break;
            case "straight-line":
                result = isPlayer ? isInStraightLine(player.getBlock()) : isInStraightLine(block);
                break;
            case "same-direction-of":
                result = isInSameDirectionOf(split[1], isPlayer ? player.getBlock() : block);
                break;
            case "direction":
                result = isInDirection(isPlayer ? player.getBlock() : block, split[1]);
                break;
            default:
                throw new LogicExpressionEvaluationException("Invalid keyword \""+split[0]+"\"");
        }
        if(invert)return !result;
        return result;
    }

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

    private boolean isInSameDirectionOf(String varName, Block block1) throws LogicExpressionEvaluationException
    {
        Block block2;
        if(isPlayer(varName))block2 = getPlayer(varName).getBlock();
        else if(isBlock(varName))block2 = getBlock(varName);
        else throw new LogicExpressionEvaluationException();

        Direction direction1 = block1.getDirectionFrom(executor.getContextPlayerBlock());
        Direction direction2 = block2.getDirectionFrom(executor.getContextPlayerBlock());

        return direction1 != Direction.NaD && direction1 == direction2;
    }

    private boolean isInStraightLine(Block block)
    {
        Player contextPlayer = executor.getContextPlayer();
        return block.getX() == contextPlayer.getBlock().getX() || block.getY() == contextPlayer.getBlock().getY();
    }

    private Player getPlayer(String varName) throws LogicExpressionEvaluationException
    {
        return executor.getPlayer(varName).orElseThrow(this::newException);
    }

    private Block getBlock(String varName) throws LogicExpressionEvaluationException
    {
        return executor.getBlock(varName).orElseThrow(this::newException);
    }

    private LogicExpressionEvaluationException newException()
    {
        return new LogicExpressionEvaluationException();
    }

    private boolean isDigit(String str)
    {
        if(str == null || str.isEmpty())return false;
        for(char ch : str.toCharArray())if(!Character.isDigit(ch))return false;
        return true;
    }

    private boolean isAtDistance(Object obj, String param2, String distance, int maxOrMin) throws LogicExpressionEvaluationException
    {
        if(!isDigit(distance))throw new LogicExpressionEvaluationException();
        int dist = Integer.parseInt(distance);
        Block block1;
        Block block2;

        if(obj instanceof Block)block1 = (Block)obj;
        else if(obj instanceof Player)block1 = ((Player)obj).getBlock();
        else throw new LogicExpressionEvaluationException();

        if(isPlayer(param2))block2 = getPlayer(param2).getBlock();
        else if(isBlock(param2))block2 = getBlock(param2);
        else throw new LogicExpressionEvaluationException();

        return maxOrMin == MAX_DISTANCE ? block1.getManhattanDistanceFrom(block2) <= dist : block1.getManhattanDistanceFrom(block2) >= dist;
    }

    private boolean isPlayer(String varName)
    {
        return executor.containsPlayer(varName);
    }

    private boolean isBlock(String varName)
    {
        return executor.containsBlock(varName);
    }

    private void removeExternalBrackets()
    {
        if(expression.startsWith("(") && expression.endsWith(")"))
        {
            expression = expression.substring(1, expression.length()-1);
        }
    }

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
}
