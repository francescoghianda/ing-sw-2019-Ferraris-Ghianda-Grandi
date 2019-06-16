package it.polimi.se2019.network.message;

public class TimeoutTime
{
    public static final TimeoutTime INDETERMINATE = new TimeoutTime(-1);

    private int seconds;

    private TimeoutTime(int seconds)
    {
        this.seconds = seconds;
    }

    public static TimeoutTime seconds(int seconds)
    {
        return new TimeoutTime(seconds);
    }

    public static TimeoutTime milliseconds(int milliseconds)
    {
        return new TimeoutTime(milliseconds/1000);
    }

    public boolean isIndeterminate()
    {
        return seconds < 0;
    }

    public int getSeconds()
    {
        return seconds;
    }
}
