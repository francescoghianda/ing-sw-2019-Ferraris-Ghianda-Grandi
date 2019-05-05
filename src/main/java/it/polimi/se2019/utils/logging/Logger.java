package it.polimi.se2019.utils.logging;

import it.polimi.se2019.utils.constants.AnsiColor;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A class that provide to log the output on a console and/or a file
 */
public class Logger
{
    private static Logger logger = new Logger();

    private SimpleDateFormat sdf;
    private boolean showTime = true;
    private ArrayList<LoggerOutputStream> outputs = new ArrayList<>();
    private boolean enabled = true;

    private boolean gameMode;

    private Logger()
    {
        try
        {
            sdf = new SimpleDateFormat("ddMMyy_HHmmss");
            addOutput(new LoggerOutputStream(System.out, getDefaultFormatter(),true));
            File logFile = new File(getLogFileName());
            boolean addFileOutput = true;
            if(!logFile.getParentFile().exists())addFileOutput = logFile.getParentFile().mkdirs();
            if(addFileOutput)addOutput(new LoggerOutputStream(new FileOutputStream(logFile), getDefaultFormatter(), false));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private String getLogFileName()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("log/log");
        builder.append(sdf.format(Calendar.getInstance().getTime()));
        builder.append(".txt");
        return builder.toString();
    }

    /**
     * Enable the game mode
     * With the game mode enabled the console not print the timestamp and the level of the message
     * @param gameMode
     */
    public void enableGameMode(boolean gameMode)
    {
        this.gameMode = gameMode;
        if(gameMode)
        {
            outputs.get(0).setMinLevel(Level.CLI);
            outputs.get(0).setFormatter(LogMessage::getMessage);
        }
        else
        {
            outputs.get(0).setMinLevel(Level.INFO);
            outputs.get(0).setFormatter(getDefaultFormatter());
        }
    }

    public LoggerFormatter getDefaultFormatter()
    {
        return  message -> (message.isPrintColor() ? message.getLevel().getPrintColor() : "")+getTime()+" "+message+(message.isPrintColor() ? AnsiColor.RESET : "");
    }

    public static Logger getInstance()
    {
        return logger;
    }

    /**
     * Add an output for the logger
     * @param loggerOutputStream The LoggerOutputStream to add
     */
    public void addOutput(LoggerOutputStream loggerOutputStream)
    {
        outputs.add(loggerOutputStream);
    }

    public void removeOutput(LoggerOutputStream loggerOutputStream)
    {
        if(!loggerOutputStream.equals(outputs.get(0)))outputs.remove(loggerOutputStream);
    }

    public void setMinLevel(Level level)
    {
        outputs.forEach(output -> output.setMinLevel(level));
    }

    public void showTime()
    {
        showTime = true;
    }

    public void hideTime()
    {
        showTime = false;
    }

    public void enableLogger()
    {
        enabled = true;
    }

    public void disableLogger()
    {
        enabled = false;
    }

    private String getTime()
    {
        return showTime ? Calendar.getInstance().getTime().toString().split(" ")[3] : "";
    }

    private void log(LogMessage message)
    {
        if(!enabled)return;
        for(LoggerOutputStream out : outputs)if(message.getLevel().getValue() >= out.getMinValue())out.println(out.getFormatter().format(message.setPrintColor(out.supportColor())));
    }

    private void inputLog(LogMessage message)
    {
        if(!enabled)return;
        for(LoggerOutputStream out : outputs)if(message.getLevel().getValue() >= out.getMinValue())out.print(out.getFormatter().format(message.setPrintColor(out.supportColor())));
    }

    public static void log(Level level, Object obj)
    {
        logger.log(LogMessage.pack(level, obj.toString()));
    }

    public static void info(Object obj)
    {
        logger.log(LogMessage.pack(Level.INFO, obj.toString()));
    }

    public static void debug(Object obj)
    {
        logger.log(LogMessage.pack(Level.DEBUG, obj.toString()));
    }

    public static void error(Object obj)
    {
        logger.log(LogMessage.pack(Level.ERROR, obj.toString()));
    }

    public static void warning(Object obj)
    {
        logger.log(LogMessage.pack(Level.WARNING, obj.toString()));
    }

    public static void cli(Object obj)
    {
        logger.log(LogMessage.pack(Level.CLI, obj.toString()));
    }

    public static void inputCli(Object obj)
    {
        logger.inputLog(LogMessage.pack(Level.CLI, obj.toString()));
    }


    public static void exception(Exception e)
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        logger.log(LogMessage.pack(Level.ERROR, sw.toString()));
    }

    public interface LoggerFormatter
    {
        String format(LogMessage message);
    }

}
