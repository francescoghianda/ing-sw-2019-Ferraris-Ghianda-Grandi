package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.utils.logging.Logger;

public interface CommandErrorHandler
{
    void handle(CommandError error) throws CanceledActionException;

    CommandErrorHandler CONTINUE = error ->
    {
        Logger.warning(error.getCommand()+" has thrown an error.");
        Logger.warning("The execution was not interrupted.");
    };

    CommandErrorHandler STOP_CANCELED_BY_USER = error ->
    {
        Logger.error(error.getCommand()+" has thrown an error.");
        Logger.error("The execution was interrupted! ");
        throw new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER);
    };

    CommandErrorHandler STOP_IMPOSSIBLE_ACTION = error ->
    {
        Logger.error(error.getCommand()+" has thrown an error.");
        Logger.error("The execution was interrupted! ");
        throw new CanceledActionException(CanceledActionException.Cause.IMPOSSIBLE_ACTION);
    };

    CommandErrorHandler STOP_ERROR = error ->
    {
        Logger.error(error.getCommand()+" has thrown an error.");
        Logger.error("The execution was interrupted! ");
        throw new CanceledActionException(CanceledActionException.Cause.ERROR);
    };
}
