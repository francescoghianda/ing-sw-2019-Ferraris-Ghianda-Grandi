package it.polimi.se2019.card.cardscript;

import it.polimi.se2019.card.cardscript.command.Command;

public interface OnCommandExecutedListener
{
    void onCommandExecuted(Command executed);
}
