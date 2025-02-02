package it.polimi.se2019.card.cardscript.command;

import java.util.Arrays;

/**
 * It assigns a specific name to each command
 */
public enum Commands
{
    HIT, HIT_ROOM, MARK, PAY, MOVE, ENABLE, SELECT_PLAYER, SELECT_BLOCK, SELECT_ROOM, GET_BLOCK_OF, GET_BLOCK, GET_PLAYER, RESET_DAMAGED_PLAYERS, ASK_IF, IF, END_IF, ASK_AND_SELECT;

    public boolean equalsAny(Commands... commands)
    {
        return Arrays.stream(commands).anyMatch(command -> command == this);
    }
}
