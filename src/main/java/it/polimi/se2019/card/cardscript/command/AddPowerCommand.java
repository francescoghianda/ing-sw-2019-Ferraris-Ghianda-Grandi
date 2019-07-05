package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.player.Player;

public class AddPowerCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.PLAYER, ParameterTypes.DIGIT);

    private Player player;
    private int value;

    /**
     * Constructs a new command
     *
     * @param executor   indicates the executor of the command
     * @param parameters refers to the parameters of the command
     */
    public AddPowerCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        player = (Player) getParam(0);
        value = (int) getParam(1);
    }

    @Override
    protected boolean exec()
    {
        if(player == null)return false;

        player.setPower(player.getPower()+value);

        return true;
    }

    @Override
    public Commands getType()
    {
        return null;
    }
}
