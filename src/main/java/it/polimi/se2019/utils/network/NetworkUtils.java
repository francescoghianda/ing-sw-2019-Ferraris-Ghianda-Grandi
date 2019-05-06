package it.polimi.se2019.utils.network;

import java.util.regex.Pattern;

public class NetworkUtils
{
    private static final String IP_REGEX = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
    private static final Pattern ipPattern = Pattern.compile(IP_REGEX);

    private static final int minPort = 1024;
    private static final int maxPort = 65535;

    public static boolean isValidPort(int port)
    {
        return port >= minPort && port <= maxPort;
    }

    public static boolean isIp(String ip)
    {
        return ipPattern.matcher(ip).matches();
    }
}
