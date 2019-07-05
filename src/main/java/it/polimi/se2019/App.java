package it.polimi.se2019;

import it.polimi.se2019.ui.cli.CLI;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.ui.gui.GUI;
import it.polimi.se2019.utils.file.ExternalFile;

public class App
{

    private boolean startInGUI;

    private App(String[] args)
    {
        parseArgs(args);
    }

    private void start()
    {
        UI ui;

        if(System.console() == null || startInGUI)
        {
           ui = new GUI();
        }
        else
        {
            ui = new CLI();
        }



        ui.startUI();
    }

    private void parseArgs(String[] args)
    {
        for(String arg : args)
        {
            arg = arg.toLowerCase();

            switch (arg)
            {
                case "start-gui":
                    startInGUI = true;
                    break;
            }
        }

    }

    public static void main(String[] args)
    {
        ExternalFile.readIpFile();
        new App(args).start();
    }

}
