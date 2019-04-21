package it.polimi.se2019.utils.logging;

import it.polimi.se2019.utils.constants.AnsiColor;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Logger
{
    private static Logger logger = new Logger();

    private SimpleDateFormat sdf;
    private boolean showTime = true;
    private ArrayList<LoggerOutputStream> outputs = new ArrayList<>();
    private LoggerFormatter formatter;
    private int minValue;
    private boolean enabled = true;

    private Logger()
    {
        try
        {
            sdf = new SimpleDateFormat("ddMMyy_HHmmss");
            addOutput(new LoggerOutputStream(System.out, true));
            File logFile = new File(getLogFileName());
            boolean addFileOutput = true;
            if(!logFile.getParentFile().exists())addFileOutput = logFile.getParentFile().mkdirs();
            if(addFileOutput)addOutput(new LoggerOutputStream(new FileOutputStream(logFile), false));
            setFormatter(getDefaultFormatter());
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

    public LoggerFormatter getDefaultFormatter()
    {
        return  message -> (message.isPrintColor() ? message.getLevel().getPrintColor() : "")+getTime()+" "+message+(message.isPrintColor() ? AnsiColor.RESET : "");
    }

    public static Logger getInstance()
    {
        return logger;
    }

    public void setFormatter(LoggerFormatter loggerFormatter)
    {
        formatter = loggerFormatter;
    }

    public void addOutput(LoggerOutputStream loggerOutputStream)
    {
        outputs.add(loggerOutputStream);
    }

    public void removeOutput(LoggerOutputStream loggerOutputStream)
    {
        outputs.remove(loggerOutputStream);
    }

    public void setMinLevel(Level level)
    {
        minValue = level.getValue();
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
        for(LoggerOutputStream out : outputs)if(message.getLevel().getValue() >= minValue)out.println(formatter.format(message.setPrintColor(out.supportColor())));
    }

    public static void log(Level level, String message)
    {
        logger.log(LogMessage.pack(level, message));
    }

    public static void info(String msg)
    {
        logger.log(LogMessage.pack(Level.INFO, msg));
    }

    public static void debug(String msg)
    {
        logger.log(LogMessage.pack(Level.DEBUG, msg));
    }

    public static void error(String msg)
    {
        logger.log(LogMessage.pack(Level.ERROR, msg));
    }

    public static void warning(String msg)
    {
        logger.log(LogMessage.pack(Level.WARNING, msg));
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
