package it.polimi.se2019;

import it.polimi.se2019.map.Map;
import it.polimi.se2019.utils.logging.Logger;
import it.polimi.se2019.utils.logging.LoggerOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class App 
{
    public static void main( String[] args )
    {
        try
        {
            Logger.getInstance().addOutput(new LoggerOutputStream(new FileOutputStream(new File("logging.txt")), false));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        Logger.info( "Hello Word!");

        Map map = new Map();
        map.initMap();

        Logger.info(map.toString());
    }
}
