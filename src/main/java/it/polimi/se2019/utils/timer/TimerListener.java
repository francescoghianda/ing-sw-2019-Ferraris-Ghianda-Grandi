package it.polimi.se2019.utils.timer;

public interface TimerListener
{
    void onTimerStart(String timerName);
    void onTimerTick(String timerName, int remainingTime);
    void onTimerEnd(String timerName);

}
