package it.polimi.se2019;

import it.polimi.se2019.ui.cli.CLI;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.ui.gui.GUI;

import java.util.UUID;

public class App 
{
    public static void main(String[] args)
    {

        UI ui = new CLI();
        ui.startUI();
    }

}
