package it.polimi.se2019.card.cardscript;

import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.logging.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CardScriptExecutor
{

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

    public HashMap<String, Player> getPlayers()
    {
        return this.players;
    }

    public HashMap<String, Block> getBlocks()
    {
        return this.blocks;
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

    public void execute() throws CanceledActionException
    {
        for (String line : currScript)
        {
            if(!(line.isEmpty() || line.startsWith("#")))
            {
                try
                {
                    executeLine(line);
                }
                catch (InvocationTargetException e)
                {
                    if(e.getCause() instanceof CanceledActionException)
                    {
                        throw new CanceledActionException(((CanceledActionException)e.getCause()).getCanceledCause());
                    }
                    Logger.exception(e);
                }
                catch (IllegalAccessException e1)
                {
                    Logger.exception(e1);
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

    private void selectPlayer(String param) throws CanceledActionException
    {
        String[] params = param.trim().split("->");
        if(players.containsKey(params[1].trim()))throw new CardScriptErrorException();
        LogicExpression expression = new LogicExpression(params[0].trim());
        Predicate<Player> predicate = player -> expression.evaluate(players, blocks, player);

        List<Player> allPlayers = contextPlayer.getGameController().getPlayers();
        ArrayList<String> validUsername = allPlayers.stream().filter(predicate).map(Player::getUsername).collect(Collectors.toCollection(ArrayList::new));

        if(validUsername.isEmpty())throw new CanceledActionException(CanceledActionException.Cause.IMPOSSIBLE_ACTION);

        String chosen = contextPlayer.getView().choose(new Bundle<>("Scegli un giocatore tra", validUsername));
        Optional<Player> chosenPlayer = contextPlayer.getGameController().findPlayerByUsername(chosen);

        /*NetworkMessageServer<?> response = contextPlayer.getResponseTo(Messages.SELECT_PLAYER);
        while(!predicate.test((Player)response.getParam()))
        {
            response = contextPlayer.getResponseTo(Messages.STATE_MESSAGE_CLIENT.setParam(Messages.INVALID_PLAYER));
        }
        contextPlayer.sendMessageToClient(Messages.STATE_MESSAGE_CLIENT.setParam(Messages.OK));*/

        if(!chosenPlayer.isPresent())throw new CardScriptErrorException("Invalid player <"+chosen+">");

        players.put(params[1].trim(), chosenPlayer.get());
    }

    /*
    private void selectBlock(String param)
    {
        String[] params = param.trim().split("->");
        if(blocks.containsKey(params[1].trim()))throw new CardScriptErrorException();
        LogicExpression expression = new LogicExpression(params[0].trim());
        Predicate<Block> predicate = block -> expression.evaluate(players, blocks, block);
        NetworkMessageServer<?> response = contextPlayer.getResponseTo(Messages.SELECT_BLOCK);
        while(!predicate.test((Block)response.getParam()))
        {
            response = contextPlayer.getResponseTo(Messages.STATE_MESSAGE_CLIENT.setParam(Messages.INVALID_BLOCK));
        }
        contextPlayer.sendMessageToClient(Messages.STATE_MESSAGE_CLIENT.setParam(Messages.OK));
        blocks.put(params[1].trim(), (Block)response.getParam());
    }*/

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

    private boolean isDigit(String str)
    {
        if(str == null || str.isEmpty())return false;
        for(char ch : str.toCharArray())if(!Character.isDigit(ch))return false;
        return true;
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





}
