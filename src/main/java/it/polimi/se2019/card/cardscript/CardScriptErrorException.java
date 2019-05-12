package it.polimi.se2019.card.cardscript;

public class CardScriptErrorException extends RuntimeException
{
    public CardScriptErrorException()
    {
        super();
    }

    public CardScriptErrorException(String msg)
    {
        super(msg);
    }
}
