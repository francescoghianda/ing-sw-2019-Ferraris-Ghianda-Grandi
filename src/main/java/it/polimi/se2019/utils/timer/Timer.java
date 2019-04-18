package it.polimi.se2019.utils.timer;

import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class Timer implements Runnable
{
    private static HashMap<String, Timer> timers = new HashMap<>();

    private ArrayList<TimerListener> listeners;
    private String name;
    private volatile int seconds;
    private volatile boolean running;

    private Thread thread;

    private Timer(int seconds, String name)
    {
        this.seconds = seconds;
        this.name = name;
        this.listeners = new ArrayList<>();
    }

    public static Timer createTimer(String name, int seconds) throws AlreadyExistingTimerException
    {
        if(timers.containsKey(name))throw new AlreadyExistingTimerException(name);
        timers.put(name, new Timer(seconds, name));
        return timers.get(name);
    }

    public void addTimerListener(TimerListener listener)
    {
        if(!listeners.contains(listener))listeners.add(listener);
    }

    public static Timer getTimer(String name)
    {
        return timers.get(name);
    }

    public void start()
    {
        if(thread == null || !thread.isAlive())
        {
            thread = new Thread(this);
            running = true;
            thread.start();
        }
    }

    @Override
    public void run()
    {
        long delay = 0;
        int elapsedSeconds = 0;
        for(TimerListener listener : listeners)listener.onTimerStart();
        while(running)
        {
            long startTime = System.currentTimeMillis();
            for(TimerListener listener : listeners)listener.onTimerTick(seconds-elapsedSeconds);
            long delta = System.currentTimeMillis()-startTime;
            if(delta < 1000)
            {
                try
                {
                    Thread.sleep(1000-delta);
                }
                catch (InterruptedException e)
                {
                    Logger.error(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
            else
            {
                delay += delta-1000;
                Logger.warning("The timer <"+name+"> is doing to much work on a single tick! (delay: "+delay+" ms)");
            }
            elapsedSeconds++;
            if(seconds-elapsedSeconds <= 0)running = false;
        }
        for(TimerListener listener: listeners)listener.onTimerEnd();
    }
}
