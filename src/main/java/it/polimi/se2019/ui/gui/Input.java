package it.polimi.se2019.ui.gui;

import it.polimi.se2019.utils.logging.Logger;

public class Input<R>
{
    private final InputHandler<R> source;
    private R input;

    public Input(InputHandler<R> source)
    {
        this.source = source;
    }

    R getInput(String inputName)
    {
        synchronized (SceneManager.getInstance())
        {
            try
            {
                while((input = source.getInput(inputName)) == null)SceneManager.getInstance().wait();
            }
            catch (InterruptedException e)
            {
                Logger.exception(e);
                Thread.currentThread().interrupt();
            }
        }
        return input;
    }

    public static void notifyAllInputs()
    {
        synchronized (SceneManager.getInstance())
        {
            SceneManager.getInstance().notifyAll();
        }
    }


}
