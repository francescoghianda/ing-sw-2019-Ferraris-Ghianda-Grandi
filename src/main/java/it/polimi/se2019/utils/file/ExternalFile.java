package it.polimi.se2019.utils.file;

import it.polimi.se2019.utils.logging.Logger;
import it.polimi.se2019.utils.network.NetworkUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ExternalFile
{
    private static File jarFile = new File(ExternalFile.class.getProtectionDomain().getCodeSource().getLocation().getFile());

    private ExternalFile(){}

    public static File getExternalFile(String name)
    {
        return new File(jarFile.getParent() + File.separator + name);
    }

    public static void readIpFile()
    {
        File ipFile = getExternalFile("ip.txt");

        if(ipFile.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(ipFile));
                String line = br.readLine();

                if(line != null && NetworkUtils.isIp(line.trim()))
                {
                    System.setProperty("java.rmi.server.hostname", line);
                }
                else
                {
                    Logger.warning("Ip file contains not valid ip!");
                }
                br.close();
            }
            catch (IOException e)
            {
                Logger.error("Error while reading ip file");
            }
        }
    }

}
