package it.polimi.se2019.card.cardscript;

import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.network.message.Messages;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.logging.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Predicate;

public class CardScriptExecutor
{
    private static final int MIN_DISTANCE = 0;
    private static final int MAX_DISTANCE = 1;

    private HashMap<String, Method> commands;

    private String[] currScript;

    private final Player contextPlayer;
    private Block contextBlock;
    private HashMap<String, Player> players;
    private HashMap<String, Block> blocks;

    private WeaponCard weapon;

    public CardScriptExecutor(Player contextPlayer)
    {
        this.players = new HashMap<>();
        this.blocks = new HashMap<>();
        this.commands = new HashMap<>();
        this.contextPlayer = contextPlayer;
        this.players.put("context_player", contextPlayer);
        updateContextBlock();

        try
        {
            commands.put("select_block", getClass().getMethod("selectBlock", String.class));
            commands.put("select_player", getClass().getMethod("selectPlayer", String.class));
            commands.put("hit", getClass().getMethod("hit", String.class));
            commands.put("mark", getClass().getMethod("mark", String.class));
            commands.put("move", getClass().getMethod("move", String.class));
            commands.put("pay", getClass().getMethod("pay", String.class));
        }
        catch (NoSuchMethodException e)
        {
            Logger.exception(e);
        }
    }

    public void setWeapon(WeaponCard weapon)
    {
        this.weapon = weapon;
    }

    public Player getContextPlayer()
    {
        return this.contextPlayer;
    }

    public CardScriptExecutor setScript(String script)
    {
        currScript = script.split("\\r?\\n");
        return this;
    }

    public void reset()
    {
        this.players.clear();
        this.blocks.clear();
        this.players.put("context_player", contextPlayer);
        updateContextBlock();
    }

    public void execute()
    {
        for (String line : currScript)
        {
            if(!(line.isEmpty() || line.startsWith("#")))
            {
                try
                {
                    executeLine(line);
                }
                catch (InvocationTargetException | IllegalAccessException e)
                {
                    Logger.exception(e);
                }
            }
        }
    }

    private void executeLine(String line) throws InvocationTargetException, IllegalAccessException
    {
        StringBuilder commandBuilder = new StringBuilder();
        boolean executed = false;
        char[] chars = line.toLowerCase().toCharArray();
        for(int i = 0; i < chars.length; i++)
        {
            commandBuilder.append(chars[i]);
            if(commands.containsKey(commandBuilder.toString()))
            {
                commands.get(commandBuilder.toString()).invoke(this, line.substring(i).trim());
                executed = true;
                break;
            }
        }
        if(!executed)throw new CardScriptErrorException();
    }

    private void selectPlayer(String param)
    {
        String[] params = param.trim().split("->");
        if(players.containsKey(params[1].trim()))throw new CardScriptErrorException();
        String[] expressions = getExpressions(params[0].trim());
        Predicate<Player> predicate = player -> evaluateExpression(expressions, player);
        NetworkMessageServer<?> response = contextPlayer.getResponseTo(Messages.SELECT_PLAYER);
        while(!predicate.test((Player)response.getParam()))
        {
            response = contextPlayer.getResponseTo(Messages.STATE_MESSAGE_CLIENT.setParam(Messages.INVALID_PLAYER));
        }
        contextPlayer.sendMessageToClient(Messages.STATE_MESSAGE_CLIENT.setParam(Messages.OK));
        players.put(params[1].trim(), (Player)response.getParam());
    }

    private void selectBlock(String param)
    {
        String[] params = param.trim().split("->");
        if(blocks.containsKey(params[1].trim()))throw new CardScriptErrorException();
        String[] expressions = getExpressions(params[0].trim());
        Predicate<Block> predicate = block -> evaluateExpression(expressions, block);
        NetworkMessageServer<?> response = contextPlayer.getResponseTo(Messages.SELECT_BLOCK);
        while(!predicate.test((Block)response.getParam()))
        {
            response = contextPlayer.getResponseTo(Messages.STATE_MESSAGE_CLIENT.setParam(Messages.INVALID_BLOCK));
        }
        contextPlayer.sendMessageToClient(Messages.STATE_MESSAGE_CLIENT.setParam(Messages.OK));
        blocks.put(params[1].trim(), (Block)response.getParam());
    }

    private String[] getExpressions(String str)
    {
        if(str == null || str.isEmpty() || !(str.startsWith("(") && str.endsWith(")")))throw new CardScriptErrorException();
        return str.substring(1, str.length()-1).split(",");
    }

    private boolean evaluateExpression(String[] expressions, Object obj)
    {
        for(String expression : expressions)
        {
            boolean result = false;
            String[] subExpressions = expression.split("or");
            for(String subExpr : subExpressions)
            {
                if(evaluateExpression(subExpr.trim(), obj))result = true;
            }
            if(!result)return false;
        }
        return true;
    }

    private boolean evaluateExpression(String expression, Object obj)
    {
        Block block = null;
        Player player = null;
        boolean result;
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
                throw new CardScriptErrorException();
        }
        if(invert)return !result;
        return result;

    }

    private void hit(String param)
    {
        String[] params = param.split(" ");
        if(!isDigit(params[1]))throw new CardScriptErrorException();
        if(isPlayer(params[0]))
        {
            players.get(params[0]).getGameBoard().addDamage(contextPlayer, Integer.parseInt(params[1]));
            contextPlayer.addDamagedPlayer(players.get(params[0]));
        }
        else if(isBlock(params[0]))
            blocks.get(params[0]).getPlayers().forEach(player ->
            {
                player.getGameBoard().addDamage(contextPlayer, Integer.parseInt(params[1]));
                contextPlayer.addDamagedPlayer(player);
            });
        else throw new CardScriptErrorException();
    }

    private void pay(String param)
    {
        //TODO
    }

    private void mark(String param)
    {
        String[] params = param.split(" ");
        if(!isDigit(params[1]))throw new CardScriptErrorException();
        if(isPlayer(params[0]))
            players.get(params[0]).getGameBoard().addMarker(contextPlayer, Integer.parseInt(params[1]));
        else if(isBlock(params[0]))
            blocks.get(params[0]).getPlayers().forEach(player -> player.getGameBoard().addMarker(contextPlayer, Integer.parseInt(params[1])));
        else throw new CardScriptErrorException();
    }

    private void move(String param)
    {
        String[] params = param.split(" ");
        if(!isBlock(params[1]))throw new CardScriptErrorException();
        if(isPlayer(params[0]))
            players.get(params[0]).setBlock(blocks.get(params[1]));
        else if(isBlock(params[0]))
            blocks.get(params[0]).getPlayers().forEach(player -> player.setBlock(blocks.get(params[1])));
        else throw new CardScriptErrorException();

        updateContextBlock();
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

       return maxOrMin == MAX_DISTANCE ? block1.getDistanceFrom(block2) <= dist : block1.getDistanceFrom(block2) >= dist;
    }


    private void updateContextBlock()
    {
        contextBlock = contextPlayer.getBlock();
        blocks.put("context_block", contextPlayer.getBlock());
    }

    private boolean isPlayer(String str)
    {
        return players.containsKey(str);
    }

    private boolean isBlock(String str)
    {
        return blocks.containsKey(str);
    }

    private boolean isDigit(String str)
    {
        if(str == null || str.isEmpty())return false;
        for(char ch : str.toCharArray())if(!Character.isDigit(ch))return false;
        return true;
    }



}
