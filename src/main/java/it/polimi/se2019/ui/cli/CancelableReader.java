package it.polimi.se2019.ui.cli;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public final class CancelableReader
{
    private static List<CancelableReader> readers = new ArrayList<>();

    private BufferedReader br;
    private FutureTask<String> inputTask;

    private CancelableReader(InputStream in)
    {
        this.br = new BufferedReader(new InputStreamReader(in));
    }

    public static CancelableReader createNew(InputStream in)
    {
        CancelableReader cancelableReader = new CancelableReader(in);
        readers.add(cancelableReader);
        return cancelableReader;
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
        finally
        {
            inputTask = null;
        }

        return input;
    }

    public void cancel()
    {
        if(inputTask != null)inputTask.cancel(true);
    }

    public static void cancelAll()
    {
        readers.forEach(CancelableReader::cancel);
    }
}