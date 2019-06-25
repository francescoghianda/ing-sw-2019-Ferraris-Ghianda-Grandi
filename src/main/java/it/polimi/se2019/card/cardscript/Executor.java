package it.polimi.se2019.card.cardscript;

import it.polimi.se2019.card.cardscript.command.*;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.player.Player;

import java.util.*;

public class Executor implements OnCommandExecutedListener
{
    private final Player contextPlayer;

    private HashMap<String, Player> varPlayers;
    private HashMap<String, Block> varBlocks;
    private HashMap<String, Boolean> varBoolean;

    private WeaponCard weaponCard;

    private boolean jumpToEndIf;

    public Executor(Player contextPlayer)
    {
        this.contextPlayer = contextPlayer;
        varPlayers = new HashMap<>();
        varBlocks = new HashMap<>();
        varBoolean = new HashMap<>();

        reset();
    }

    public Executor(Player contextPlayer, WeaponCard weaponCard)
    {
        this(contextPlayer);
        this.weaponCard = weaponCard;
    }

    public void reset()
    {
        varPlayers.clear();
        varBlocks.clear();
        varBoolean.clear();
        varPlayers.put("context_player", contextPlayer);
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
        if(varPlayers.containsKey(varName))return Optional.of(varPlayers.get(varName));
        return Optional.empty();
    }

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
        if(varBlocks.containsKey(varName))return Optional.of(varBlocks.get(varName));
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
        return varBlocks.containsKey(varName);
    }

    public boolean varExist(String varName)
    {
        return varPlayers.containsKey(varName) || varBlocks.containsKey(varName);
    }

    public void executeScript(Script script) throws CanceledActionException
    {
        Iterator<ScriptCommand> commands = script.iterator();

        while (commands.hasNext())
        {
            ScriptCommand scriptCommand = commands.next();
            try
            {
                if(!jumpToEndIf && !scriptCommand.getCommand().equals("endif"))getCommand(scriptCommand.getCommand(), scriptCommand.getParameters()).setLineNumber(scriptCommand.getScriptLineNumber()).execute();
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

        if(type.equalsAny(Commands.HIT, Commands.MARK, Commands.MOVE) && result)
        {
            //TODO settare l'arma/potenziamento come usato per la futura disattivazione/rimozione
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
