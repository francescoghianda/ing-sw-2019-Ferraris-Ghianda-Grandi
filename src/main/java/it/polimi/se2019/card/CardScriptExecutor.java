package it.polimi.se2019.card;

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
    private HashMap<String, Method> commands;

    private String[] currScript;

    private final Player contextPlayer;
    private HashMap<String, Player> players;
    private HashMap<String, Block> blocks;

    public CardScriptExecutor(Player contextPlayer)
    {
        this.players = new HashMap<>();
        this.blocks = new HashMap<>();
        this.commands = new HashMap<>();
        this.contextPlayer = contextPlayer;
        this.players.put("context_player", contextPlayer);

        try
        {
            commands.put("select_block", getClass().getMethod("selectBlock", String.class));
            commands.put("select_player", getClass().getMethod("selectPlayer", String.class));
            commands.put("hit", getClass().getMethod("hit", String.class));
            commands.put("mark", getClass().getMethod("mark", String.class));
            commands.put("move", getClass().getMethod("move", String.class));
        }
        catch (NoSuchMethodException e)
        {
            Logger.exception(e);
        }
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
        this.players.put("context_player", contextPlayer);
        this.blocks.clear();
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
        Predicate<Player> predicate = player -> evaluatePlayer(expressions, player);
        NetworkMessageServer<?> response = contextPlayer.getResponseTo(Messages.SELECT_PLAYER);
        while(!predicate.test((Player)response.getParam()))
        {
            response = contextPlayer.getResponseTo(Messages.STATE_MESSAGE.setParam(Messages.INVALID_PLAYER));
        }
        contextPlayer.sendMessageToClient(Messages.STATE_MESSAGE.setParam(Messages.OK));
        players.put(params[1].trim(), (Player)response.getParam());
    }

    private String[] getExpressions(String str)
    {
        if(str == null || str.isEmpty() || !(str.startsWith("(") && str.endsWith(")")))throw new CardScriptErrorException();
        return str.substring(1, str.length()-1).split(",");
    }

    private boolean evaluatePlayer(String[] expressions, Player player)
    {
        for(String expression : expressions)if(!evaluatePlayer(expression.trim(), player))return false;
        return true;
    }

    private boolean evaluatePlayer(String expression, Player player)
    {
        boolean invert = false;
        boolean result;
        String[] split = expression.split(" ");
        if(expression.startsWith("!"))
        {
            invert = true;
            split[0] = split[0].substring(1);
        }

        switch (split[0])
        {
            case "visible":
                result = players.get(split[1]).getVisiblePlayers().contains(player);
                break;
            case "equal":
                result = player.equals(players.get(split[1]));
                break;
            default:
                throw new CardScriptErrorException();
        }
        if(invert)return !result;
        return result;
    }

    private void selectBlock(String param)
    {

    }

    private void hit(String param)
    {
        String[] params = param.split(" ");
        if(!players.containsKey(params[0]) || !isDigit(params[1]))throw new CardScriptErrorException();
        players.get(params[0]).getGameBoard().addDamage(contextPlayer, Integer.parseInt(params[1]));
    }

    private void mark(String param)
    {
        String[] params = param.split(" ");
        if(!players.containsKey(params[0]) || !isDigit(params[1]))throw new CardScriptErrorException();
        players.get(params[0]).getGameBoard().addMarker(contextPlayer, Integer.parseInt(params[1]));
    }

    private void move(String param)
    {

    }


    private boolean isDigit(String str)
    {
        if(str == null || str.isEmpty())return false;
        for(char ch : str.toCharArray())if(!Character.isDigit(ch))return false;
        return true;
    }



}
