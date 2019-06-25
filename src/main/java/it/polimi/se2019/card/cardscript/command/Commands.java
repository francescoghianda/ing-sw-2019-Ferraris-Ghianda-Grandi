package it.polimi.se2019.card.cardscript.command;

import java.util.Arrays;

public enum Commands
{
    HIT, MARK, MOVE, ENABLE, SELECT_PLAYER, SELECT_BLOCK, ASK_IF, IF, END_IF, ASK_AND_SELECT;

    public boolean equalsAny(Commands... commands)
    {
        return Arrays.stream(commands).anyMatch(command -> command == this);
    }
}
