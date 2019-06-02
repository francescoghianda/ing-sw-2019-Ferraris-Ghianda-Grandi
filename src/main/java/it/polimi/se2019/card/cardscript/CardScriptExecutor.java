package it.polimi.se2019.card.cardscript;

import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.card.weapon.OptionalEffect;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Coordinates;
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
    private PowerUpCard powerUp;

    private boolean firstPlayerOfScript;

    private CardScriptExecutor(Player contextPlayer)
    {
        this.players = new HashMap<>();
        this.blocks = new HashMap<>();
        this.commands = new HashMap<>();
        this.contextPlayer = contextPlayer;
        this.players.put("context_player", contextPlayer);
        firstPlayerOfScript = true;
        updateContextBlock();

        try
        {
            commands.put("select_block", getClass().getDeclaredMethod("selectBlock", String.class));
            commands.put("select_player", getClass().getDeclaredMethod("selectPlayer", String.class));
            commands.put("hit", getClass().getDeclaredMethod("hit", String.class));
            commands.put("mark", getClass().getDeclaredMethod("mark", String.class));
            commands.put("move", getClass().getDeclaredMethod("move", String.class));
            commands.put("enable", getClass().getDeclaredMethod("enable", String.class));
            commands.put("pay", getClass().getDeclaredMethod("pay", String.class));
        }
        catch (NoSuchMethodException e)
        {
            Logger.exception(e);
        }
    }

    private CardScriptExecutor(Player contextPlayer, WeaponCard weaponCard)
    {
        this(contextPlayer);
        this.weapon = weaponCard;
    }

    private CardScriptExecutor(Player contextPlayer, PowerUpCard powerUpCard)
    {
        this(contextPlayer);
        this.powerUp = powerUpCard;
    }

    public static CardScriptExecutor getWeaponScriptExecutor(Player contextPlayer, WeaponCard weaponCard)
    {
        return new CardScriptExecutor(contextPlayer, weaponCard);
    }

    public static CardScriptExecutor getPowerUpScriptExecutor(Player contextPlayer, PowerUpCard powerUpCard)
    {
        return new CardScriptExecutor(contextPlayer, powerUpCard);
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
        firstPlayerOfScript = true;
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
                commands.get(commandBuilder.toString()).invoke(this, line.substring(i+1).trim());
                executed = true;
                break;
            }
        }
        if(!executed)throw new CardScriptErrorException();
    }

    private void enable(String param)
    {
        if(weapon != null)
        {
            OptionalEffect effect = weapon.getOptionalEffect(param);
            if(effect != null)effect.setEnabled(true);
        }
    }

    private void selectPlayer(String param) throws CanceledActionException
    {
        String[] params = param.trim().split("->");
        if(players.containsKey(params[1].trim()))throw new CardScriptErrorException(params[1]+" already exist!");
        LogicExpression expression = new LogicExpression(params[0].trim());
        Predicate<Player> predicate = player -> expression.evaluate(players, blocks, player);

        List<Player> allPlayers = contextPlayer.getGameController().getPlayers();
        allPlayers.remove(contextPlayer);
        ArrayList<String> validUsername = allPlayers.stream().filter(predicate).map(Player::getUsername).collect(Collectors.toCollection(ArrayList::new));

        if(validUsername.isEmpty())
        {
            if(firstPlayerOfScript)throw new CanceledActionException(CanceledActionException.Cause.IMPOSSIBLE_ACTION, "NOT VALID PLAYER");
            else return;
        }

        String chosen = contextPlayer.getView().chooseOrCancel(new Bundle<>("Scegli un giocatore tra", validUsername));
        Optional<Player> chosenPlayer = contextPlayer.getGameController().findPlayerByUsername(chosen);

        if(!chosenPlayer.isPresent())throw new CardScriptErrorException("Invalid player <"+chosen+">");

        players.put(params[1].trim(), chosenPlayer.get());
        firstPlayerOfScript = false;
    }

    private void selectBlock(String param) throws CanceledActionException
    {
        String[] params = param.trim().split("->");
        if(blocks.containsKey(params[1].trim()))throw new CardScriptErrorException(params[1]+" already exist!");
        LogicExpression expression = new LogicExpression(params[0].trim());
        Predicate<Block> predicate = block -> expression.evaluate(players, blocks, block);

        List<Block> allBlocks = contextBlock.getRoom().getMap().getAllBlocks();
        ArrayList<Coordinates> validBlocks = allBlocks.stream().filter(predicate).map(Block::getCoordinates).collect(Collectors.toCollection(ArrayList::new));

        if(validBlocks.isEmpty()) throw new CanceledActionException(CanceledActionException.Cause.IMPOSSIBLE_ACTION, "NOT VALID BLOCK");

        Coordinates chosen = contextPlayer.getView().chooseBlockFrom(validBlocks);
        Block chosenBlock = contextBlock.getRoom().getMap().getBlock(chosen.getX(), chosen.getY());

        if(chosenBlock == null)throw new CardScriptErrorException("Invalid block <"+chosen+">");

        blocks.put(params[1].trim(), chosenBlock);
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
