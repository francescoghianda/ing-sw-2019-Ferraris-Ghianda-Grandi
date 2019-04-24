package it.polimi.se2019;

import it.polimi.se2019.ui.UI;
import it.polimi.se2019.ui.cli.CLI;

public class App 
{
    public static void main(String[] args)
    {
        UI ui = new CLI();
        ui.init();

        //Map map = new Map();
        //map.initMap();

        //Logger.info(map.toString());

    }
}
