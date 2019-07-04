package it.polimi.se2019.card.cardscript;

import it.polimi.se2019.card.cardscript.command.*;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.player.ImpossibleActionException;
import it.polimi.se2019.player.Player;

import java.util.*;

/**
 * The executor that executes all the script of the command called by the context player
 */
public class Executor implements OnCommandExecutedListener
{
    private final Player contextPlayer;

    private HashMap<String, Player> varPlayers;
    private HashMap<String, Block> varBlocks;
    private HashMap<String, Boolean> varBoolean;

    private WeaponCard weaponCard;

    private boolean jumpToEndIf;

    private boolean cardUsed;

    /**
     * Constructs a new executor for the context player
     * @param contextPlayer calls the command that has to be executed by the executor
     */

    public Executor(Player contextPlayer)
    {
        this.contextPlayer = contextPlayer;
        varPlayers = new HashMap<>();
        varBlocks = new HashMap<>();
        varBoolean = new HashMap<>();

        reset();
    }

    /**
     * Constructs the new Executor for the player who wants to execute a command for a weaponcard
     * @param contextPlayer is the player that calls the command
     * @param weaponCard is the weaponcard that runs the associated command
     */

    public Executor(Player contextPlayer, WeaponCard weaponCard)
    {
        this(contextPlayer);
        this.weaponCard = weaponCard;
    }

    /**
     * all the variables of the executor are deleted. (end of the round)
     */
    public void reset()
    {
        varPlayers.clear();
        varBlocks.clear();
        varBoolean.clear();
        varPlayers.put("context_player", contextPlayer);
        cardUsed = false;
    }

    public boolean isCardUsed()
    {
        return cardUsed;
    }

    public Optional<WeaponCard> getWeaponCard()
    {
        return weaponCard == null ? Optional.empty() : Optional.of(weaponCard);
    }

    public Player getContextPlayer()
    {
        return contextPlayer;
    }

    public Block getContextPlayerBlock()
    {
        return contextPlayer.getBlock();
    }

    public Optional<Player> getPlayer(String varName)
    {
        if(varName.equals("context_player"))return Optional.of(contextPlayer);
        if(varPlayers.get(varName) != null)return Optional.ofNullable(varPlayers.get(varName));
        return Optional.empty();
    }

    /**
     * It adds a boolean variable to the executor's varibles
     * @param varName in the name of the new boolean variable
     * @param bool is the value of the new boolean value
     * @return
     */
    public boolean addBoolean(String varName, boolean bool)
    {
        if(varBoolean.containsKey(varName))return false;
        varBoolean.put(varName, bool);
        return true;
    }

    public boolean getBoolean(String varName)
    {
        return varBoolean.getOrDefault(varName, false);
    }

    public boolean addPlayer(String varName, Player player)
    {
        if(varPlayers.containsKey(varName))return false;
        varPlayers.put(varName, player);
        return true;
    }

    public Optional<Block> getBlock(String varName)
    {
        if(varName.equals("context_block"))return Optional.of(contextPlayer.getBlock());
        if(varBlocks.get(varName) != null)return Optional.ofNullable(varBlocks.get(varName));
        return Optional.empty();
    }

    public boolean addBlock(String varName, Block block)
    {
        if(varBlocks.containsKey(varName))return false;
        varBlocks.put(varName, block);
        return true;
    }

    public List<Player> getPlayers()
    {
        return new ArrayList<>(varPlayers.values());
    }

    public List<Block> getBlocks()
    {
        return new ArrayList<>(varBlocks.values());
    }

    public boolean containsPlayer(String varName)
    {
        return varPlayers.containsKey(varName);
    }

    public boolean containsBlock(String varName)
    {
        if(varName.equalsIgnoreCase("context_block"))return true;
        return varBlocks.containsKey(varName);
    }

    public boolean varExist(String varName)
    {
        return varPlayers.containsKey(varName) || varBlocks.containsKey(varName);
    }

    public void executeScript(Script script, GameController gameController) throws ImpossibleActionException, CanceledActionException
    {
        Iterator<ScriptCommand> commands = script.iterator();

        while (commands.hasNext())
        {
            ScriptCommand scriptCommand = commands.next();
            try
            {
                if(!jumpToEndIf && !scriptCommand.getCommand().equals("endif"))
                {
                    getCommand(scriptCommand.getCommand(), scriptCommand.getParameters()).setLineNumber(scriptCommand.getScriptLineNumber()).execute();
                    gameController.sendBroadcastUpdate();
                }
            }
            catch (CommandExecutionException e)
            {
                e.getError().handle();
            }

        }
    }

    private Command getCommand(String command, String[] parameters)
    {
        switch (command)
        {
            case "move":
                return new MoveCommand(this, parameters);
            case "hit":
                return new HitCommand(this, parameters);
            case "hit_room":
                return new HitRoomCommand(this, parameters);
            case "mark":
                return new MarkCommand(this, parameters);
            case "select_player":
                return new SelectPlayerCommand(this, parameters);
            case "select_block":
                return new SelectBlockCommand(this, parameters);
            case "ask_and_select":
                return new AskAndSelectCommand(this, parameters);
            case "enable":
                return new EnableCommand(this, parameters);
            case "askif":
                return new AskIfCommand(this, parameters);
            case "get_block":
                return new GetBlockCommand(this, parameters);
            case "get_player":
                return new GetPlayerCommand(this, parameters);
            case "get_block_of":
                return new GetBlockOfCommand(this, parameters);
            case "reset_damaged_players":
                return new ResetDamagedCommand(this, parameters);
            case "if":
                return new IfCommand(this, parameters);
            case "endif":
                return new EndIfCommand(this, parameters);
            default:
                throw new CardScriptErrorException("Command "+command+" not recognized!");
        }
    }

    @Override
    public void onCommandExecuted(Command executed)
    {
        Commands type = executed.getType();
        boolean result = executed.getResult();

        if(type.equalsAny(Commands.HIT, Commands.MARK, Commands.MOVE, Commands.HIT_ROOM) && result)
        {
            cardUsed = true;
        }
        else if(type == Commands.IF && !result)
        {
            jumpToEndIf = true;
        }
        else if(type == Commands.END_IF)
        {
            jumpToEndIf = false;
        }
    }
}
