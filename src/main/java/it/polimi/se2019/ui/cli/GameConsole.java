package it.polimi.se2019.ui.cli;

import it.polimi.se2019.utils.constants.Ansi;
import it.polimi.se2019.utils.logging.Logger;
import org.fusesource.jansi.AnsiConsole;

import java.io.PrintStream;
import java.util.Scanner;

class GameConsole
{
    static final PrintStream out = AnsiConsole.out();
    static final Scanner in = new Scanner(System.in);

    private GameConsole(){}

    static void startConsole()
    {
        Logger.getInstance().disableConsole();
        clear();
    }

    static void clear()
    {
        out.println(Ansi.CLEAR_SCREEN);
        out.flush();
    }

    static String nextLine(String question)
    {
        out.print(question);
        return in.nextLine();
    }

    static int nextInt(String question)
    {
        out.print(question);
        return in.nextInt();
    }

}
