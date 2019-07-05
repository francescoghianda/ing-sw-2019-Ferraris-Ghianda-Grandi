package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.card.cost.Cost;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.NotEnoughAmmoException;
import it.polimi.se2019.player.Player;

import java.util.ArrayList;

public class PayCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.ENUM("any", "red", "blue", "yellow"), ParameterTypes.DIGIT);

    private String type;
    private int value;

    /**
     * Constructs a new command
     *
     * @param executor   indicates the executor of the command
     * @param parameters refers to the parameters of the command
     */
    public PayCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        this.type = (String) getParam(0);
        this.value = (int) getParam(1);
    }

    @Override
    protected boolean exec() throws CommandExecutionException
    {
        Player player = executor.getContextPlayer();

        if(type.equals("any"))
        {
            ArrayList<String> options = new ArrayList<>();

            if(player.getGameBoard().getRedAmmo() >= value)options.add("Rosso");
            if(player.getGameBoard().getBlueAmmo() >= value)options.add("Blu");
            if(player.getGameBoard().getYellowAmmo() >= value)options.add("Yellow");

            if(options.isEmpty())throw new CommandExecutionException(new CommandError(this, CommandErrorHandler.STOP_IMPOSSIBLE_ACTION));

            String chosen = player.getView().choose(Bundle.of("Con che colore vuoi pagare?", options));
            if(chosen.equals("Rosso"))type = "red";
            else if(chosen.equals("Giallo"))type = "yellow";
            else type = "blue";
        }

        Cost cost;
        if(type.equals("red")) cost = new Cost(value, 0, 0);
        else if(type.equals("blue")) cost = new Cost(0, value, 0);
        else cost = new Cost(0, 0, value);

        try
        {
            player.getGameBoard().pay(cost);
        }
        catch (NotEnoughAmmoException e)
        {
            throw new CommandExecutionException(new CommandError(this, CommandErrorHandler.STOP_IMPOSSIBLE_ACTION));
        }

        return true;
    }

    @Override
    public Commands getType()
    {
        return Commands.PAY;
    }
}
