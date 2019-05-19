package it.polimi.se2019.ui.gui.components;

public class InvalidCardIdException extends RuntimeException
{

    public InvalidCardIdException(String id)
    {
        super(id);
    }
}
