package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.card.weapon.OptionalEffect;

public class EnableCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.GENERIC);

    private String effectName;

    public EnableCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        effectName = (String) getParam(0);
    }

    @Override
    public boolean exec()
    {
        if(executor.getWeaponCard().isPresent())
        {
            OptionalEffect effect = executor.getWeaponCard().get().getOptionalEffect(effectName);
            if(effect != null)effect.setEnabled(true);
            else return false;
        }
        return true;
    }

    @Override
    public Commands getType()
    {
        return Commands.ENABLE;
    }
}
