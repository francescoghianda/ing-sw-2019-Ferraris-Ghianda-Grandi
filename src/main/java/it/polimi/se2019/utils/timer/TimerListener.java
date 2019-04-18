package it.polimi.se2019.utils.timer;

public interface TimerListener
{
    void onTimerStart();
    void onTimerTick(int remainingTime);
    void onTimerEnd();

}
