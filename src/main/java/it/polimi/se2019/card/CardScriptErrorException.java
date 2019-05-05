package it.polimi.se2019.card;

class CardScriptErrorException extends RuntimeException
{
    CardScriptErrorException()
    {
        super();
    }

    CardScriptErrorException(String msg)
    {
        super(msg);
    }
}
