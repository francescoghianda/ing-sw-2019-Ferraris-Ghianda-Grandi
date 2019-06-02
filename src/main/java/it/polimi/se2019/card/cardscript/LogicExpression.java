package it.polimi.se2019.card.cardscript;

import it.polimi.se2019.map.Block;

import java.util.HashMap;

public class LogicExpression
{
    private static final int MIN_DISTANCE = 0;
    private static final int MAX_DISTANCE = 1;

    private String expression;
    private HashMap<String, LogicExpression> subExpressions;

    private HashMap<String, Player> players;
    private HashMap<String, Block> blocks;

    public LogicExpression(String expression)
    {
        this.expression = expression;
        this.subExpressions = new HashMap<>();
        checkBrackets();
        removeExternalBrackets();
        if(containsSubExpression()) extractSubExpressions();
    }

    public boolean evaluate(HashMap<String, Player> players, HashMap<String, Block> blocks, Object object)
    {
        this.players = players;
        this.blocks = blocks;
        String[] orElements = expression.split("\\|");
        return evaluateOrElements(orElements, object);
    }

    private boolean evaluateOrElements(String[] orElements, Object object)
    {
        for(String orElement : orElements)
        {
            String[] andElements = orElement.split("&");
            if(evaluateAndElements(andElements, object))return true;
        }
        return false;
    }

    private boolean evaluateAndElements(String[] andElements, Object object)
    {
        for(String andElement : andElements)
        {
            if(!evaluateElement(andElement, object))return false;
        }
        return true;
    }

    private boolean evaluateElement(String element, Object obj)
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
            result = subExpressions.get(element).evaluate(players, blocks, obj);
            if(not)return !result;
            else return result;
        }

        Player contextPlayer = players.get("context_player");
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
        else throw new CardScriptErrorException();

        String[] split = expression.split(" ");
        if(expression.startsWith("!"))
        {
            invert = true;
            split[0] = split[0].substring(1);
        }

        switch (split[0])
        {
            case "visible":
                result = isPlayer ? players.get(split[1]).getVisiblePlayers().contains(player) :
                        players.get(split[1]).getVisibleBlocks().contains(block);
                break;
            case "equal":
                result = isPlayer ? player.equals(players.get(split[1])) :
                        block.equals(blocks.get(split[1]));
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
            default:
                throw new CardScriptErrorException("Invalid keyword \""+split[0]+"\"");
        }
        if(invert)return !result;
        return result;
    }

    private boolean isDigit(String str)
    {
        if(str == null || str.isEmpty())return false;
        for(char ch : str.toCharArray())if(!Character.isDigit(ch))return false;
        return true;
    }

    private boolean isAtDistance(Object obj, String param2, String distance, int maxOrMin)
    {
        if(!isDigit(distance))throw new CardScriptErrorException();
        int dist = Integer.parseInt(distance);
        Block block1;
        Block block2;

        if(obj instanceof Block)block1 = (Block)obj;
        else if(obj instanceof Player)block1 = ((Player)obj).getBlock();
        else throw new CardScriptErrorException();

        if(isPlayer(param2))block2 = players.get(param2).getBlock();
        else if(isBlock(param2))block2 = blocks.get(param2);
        else throw new CardScriptErrorException();

        return maxOrMin == MAX_DISTANCE ? block1.getManhattanDistanceFrom(block2) <= dist : block1.getManhattanDistanceFrom(block2) >= dist;
    }

    private boolean isPlayer(String str)
    {
        return players.containsKey(str);
    }

    private boolean isBlock(String str)
    {
        return blocks.containsKey(str);
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
