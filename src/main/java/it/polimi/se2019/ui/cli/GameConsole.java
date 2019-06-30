package it.polimi.se2019.ui.cli;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.TimeOutException;
import it.polimi.se2019.utils.constants.Ansi;
import it.polimi.se2019.utils.logging.Logger;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

class GameConsole
{
    static final PrintStream out = getOut();
    static final CancelableReader in = CancelableReader.createNew(System.in);

    private static boolean isWindows = isWindows();

    private GameConsole(){}

    static void startConsole()
    {
        Logger.getInstance().disableConsole();
        clear();
    }

    private static PrintStream getOut()
    {
        PrintStream out;
        if(isWindows)
        {
            try
            {
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "chcp", "65001").inheritIO();
                pb.start().waitFor();
                out = new PrintStream(System.out);
                out = AnsiConsole.wrapPrintStream(out, -1);
            }
            catch (InterruptedException | IOException e)
            {
                Logger.exception(e);
                Thread.currentThread().interrupt();
                return AnsiConsole.out();
            }
        }
        else
        {
            out = AnsiConsole.out();
        }
        return out;
    }

    private static boolean isWindows()
    {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    static void clear()
    {
        out.println(Ansi.CLEAR_SCREEN);
        out.flush();

        /*if(isWindows)
        {
            try
            {
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "cls").inheritIO();
                pb.start().waitFor();
            }
            catch (IOException | InterruptedException e)
            {
                Logger.exception(e);
            }
        }*/
    }

    static String nextLine(String question) throws TimeOutException
    {
        try
        {
            out.print(question);
            return in.nextLine();
        }
        catch (CanceledInputException e)
        {
            throw new TimeOutException();
        }
    }

}
