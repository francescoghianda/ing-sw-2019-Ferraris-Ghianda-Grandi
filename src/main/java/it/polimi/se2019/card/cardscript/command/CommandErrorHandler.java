package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.player.ImpossibleActionException;
import it.polimi.se2019.utils.logging.Logger;

public interface CommandErrorHandler
{
    void handle(CommandError error) throws CanceledActionException, ImpossibleActionException;

    CommandErrorHandler CONTINUE = CommandErrorHandler::printContinue;

    CommandErrorHandler STOP_CANCELED_BY_USER = error ->
    {
        printStop(error);
        throw new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER);
    };

    CommandErrorHandler STOP_IMPOSSIBLE_ACTION = error ->
    {
        printStop(error);
        throw new ImpossibleActionException(ImpossibleActionException.Cause.OTHER);
    };

    CommandErrorHandler STOP_ERROR = error ->
    {
        printStop(error);
        throw new CanceledActionException(CanceledActionException.Cause.ERROR);
    };

    static void printStop(CommandError error)
    {
        Logger.error(error.getCommand()+" has thrown an error.");
        Logger.error("The execution was interrupted! ");
    }

    static void printContinue(CommandError error)
    {
        Logger.warning(error.getCommand()+" has thrown an error.");
        Logger.warning("The execution was not interrupted.");
    }
}
