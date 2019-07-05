package it.polimi.se2019.ui.cli;

import it.polimi.se2019.controller.TimeOutException;
import it.polimi.se2019.utils.constants.Ansi;
import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.logging.Logger;
import org.fusesource.jansi.AnsiConsole;


import java.io.IOException;
import java.io.PrintStream;


class GameConsole
{
    private static boolean isWindows = isWindows();
    private static final PrintStream out = getOut();
    private static final CancelableReader in = CancelableReader.createNew(System.in);

    private static int caretX;
    private static int caretY;

    private static int savedX;
    private static int savedY;

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
            Logger.info("Executing chcp command...");
            try
            {
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "chcp", "437").inheritIO();
                pb.start().waitFor();
                out = new PrintStream(System.out);
                out = AnsiConsole.wrapPrintStream(out, -1);
                Logger.info("chcp command executed correctly");
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
        //Logger.info("OS: "+System.getProperty("os.name"));
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    static int getCaretX()
    {
        return caretX;
    }

    static int getCaretY()
    {
        return caretY;
    }

    static synchronized void setCaretPosition(int x, int y)
    {
        if(CancelableReader.getActiveReader() > 0)return;
        out.printf("%c[%d;%df", Ansi.ESC, y, x);
        caretX = x;
        caretY = y;
    }

    static void saveCaretPosition()
    {
        //GameConsole.out.printf("%c[s", Ansi.ESC);
        savedX = caretX;
        savedY = caretY;
    }

    static void restoreCaretPosition()
    {
        //GameConsole.out.printf("%c[u", Ansi.ESC);
        setCaretPosition(savedX, savedY);
    }

    static synchronized void eraseLine()
    {
        if(CancelableReader.getActiveReader() > 0)return;
        out.printf("%c[K", Ansi.ESC);
    }

    static synchronized void printf(String format, Object... objects)
    {
        String s = String.format(format, objects);
        print(s);
    }

    static synchronized void print(Object o)
    {
        if(CancelableReader.getActiveReader() > 0)return;
        String s = o.toString();
        String[] lines = s.split("\n");
        caretY += lines.length;
        caretX += lines[lines.length-1].length();
        out.print(s);
    }

    static synchronized void printColored(GameColor color, Object o)
    {
        if(CancelableReader.getActiveReader() > 0)return;
        out.print(Ansi.convertColor(color));
        print(o);
        out.print(Ansi.RESET);
    }

    static synchronized void printlnColored(GameColor color, Object o)
    {
        if(CancelableReader.getActiveReader() > 0)return;
        out.print(Ansi.convertColor(color));
        println(o);
        out.print(Ansi.RESET);
    }

    static synchronized void println(Object o)
    {
        if(CancelableReader.getActiveReader() > 0)return;
        print(o);
        out.print('\n');
        caretX = 0;
        caretY++;
    }

    static synchronized void clear()
    {
        if(CancelableReader.getActiveReader() > 0)return;
        out.print(Ansi.CLEAR_SCREEN);
        out.flush();

        caretX = 0;
        caretY = 0;

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

    static String nextLine() throws TimeOutException
    {
        try
        {
            return in.nextLine();
        }
        catch (CanceledInputException e)
        {
            throw new TimeOutException();
        }
    }

}
