package it.polimi.se2019.utils.constants;

public class Ansi
{
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String WHITE = "\u001B[37m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GREEN = "\u001B[32m";
    public static final String PURPLE = "\u001B[35m";
    public static final String BLUE = "\u001B[34m";

    public static final String BLUE_BACKGROUND = "\u001B[44m";
    public static final String RED_BACKGROUND = "\u001B[41m";
    public static final String WHITE_BACKGROUND = "\u001B[47m";
    public static final String YELLOW_BACKGROUND = "\u001B[43m";
    public static final String PURPLE_BACKGROUND = "\u001B[45m";
    public static final String GREEN_BACKGROUND = "\u001B[42m";

    public static final String CLEAR_SCREEN = "\u001b[2J\u001b[0;0H";


    public static String combineColor(String backgroundColor, String foregroundColor)
    {
        return "\u001B["+backgroundColor.substring(2, 4)+';'+foregroundColor.substring(2, 4)+"m";
    }

    public static String convertColor(GameColor color)
    {
        switch (color)
        {
            case RED:
                return Ansi.RED;
            case BLUE:
                return Ansi.BLUE;
            case GREEN:
                return Ansi.GREEN;
            case WHITE:
                return Ansi.WHITE;
            case PURPLE:
                return Ansi.PURPLE;
            case YELLOW:
                return Ansi.YELLOW;
            default:
                return "";
        }
    }

    public static String convertColorBackground(GameColor color)
    {
        switch (color)
        {
            case RED:
                return Ansi.RED_BACKGROUND;
            case BLUE:
                return Ansi.BLUE_BACKGROUND;
            case GREEN:
                return Ansi.GREEN_BACKGROUND;
            case WHITE:
                return Ansi.WHITE_BACKGROUND;
            case PURPLE:
                return Ansi.PURPLE_BACKGROUND;
            case YELLOW:
                return Ansi.YELLOW_BACKGROUND;
            default:
                return "";
        }
    }
}
