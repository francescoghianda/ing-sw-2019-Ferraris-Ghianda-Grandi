package it.polimi.se2019.ui.cli;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CancelableReader
{
    private BufferedReader br;
    private FutureTask<String> inputTask;

    public CancelableReader(InputStream in)
    {
        this.br = new BufferedReader(new InputStreamReader(in));
    }

    public String nextLine() throws CanceledInputException
    {
        inputTask = new FutureTask<>(() -> br.readLine());

        Thread inputThread = new Thread(inputTask);
        inputThread.setDaemon(true);
        inputThread.setName("Input thread");
        inputThread.start();

        String input;

        try
        {
            input = inputTask.get();
        }
        catch (InterruptedException | CancellationException | ExecutionException e)
        {
            throw new CanceledInputException();
        }

        return input;
    }

    public void cancel()
    {
        if(inputTask != null)inputTask.cancel(true);
    }
}