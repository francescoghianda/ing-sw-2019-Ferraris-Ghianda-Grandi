package it.polimi.se2019.utils.timer;

public class AlreadyExistingTimerException extends RuntimeException
{
    public  AlreadyExistingTimerException(String timerName)
    {
        super("The timer "+timerName+" already exist!");
    }
}
