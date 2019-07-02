package it.polimi.se2019.utils.constants;

import java.io.Serializable;

public enum GameMode implements Serializable
{
    NORMAL(2), FINAL_FRENZY_BEFORE_FP(2), FINAL_FRENZY_AFTER_FP(1);

    private int playableAction;

    GameMode(int playableAction)
    {
        this.playableAction = playableAction;
    }

    public int getPlayableAction()
    {
        return playableAction;
    }

    public boolean isFinalFrenzy()
    {
        return this == FINAL_FRENZY_BEFORE_FP || this == FINAL_FRENZY_AFTER_FP;
    }

}
