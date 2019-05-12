package it.polimi.se2019.utils.xml;

public class NotValidXMLException extends Exception
{
    public NotValidXMLException(String filePath)
    {
        super(filePath);
    }
}
