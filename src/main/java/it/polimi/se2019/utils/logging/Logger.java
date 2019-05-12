package it.polimi.se2019.utils.logging;

import it.polimi.se2019.utils.constants.Ansi;
import org.fusesource.jansi.AnsiConsole;

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

    private boolean consoleOutput;

    private Logger()
    {
        try
        {
            AnsiConsole.systemInstall();
            sdf = new SimpleDateFormat("ddMMyy_HHmmss");
            addOutput(new LoggerOutputStream(AnsiConsole.out, getDefaultFormatter(),true));
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
        return "log/log" + sdf.format(Calendar.getInstance().getTime()) + ".txt";
    }

    public LoggerFormatter getDefaultFormatter()
    {
        return  message -> (message.isPrintColor() ? message.getLevel().getPrintColor() : "")+getTime()+" "+message+(message.isPrintColor() ? Ansi.RESET : "");
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
        for(LoggerOutputStream out : outputs)
        {
            if(!consoleOutput && outputs.indexOf(out) == 0)continue;
            if(message.getLevel().getValue() >= out.getMinValue())out.println(out.getFormatter().format(message.setPrintColor(out.supportColor())));
        }
    }

    private void inputLog(LogMessage message)
    {
        if(!enabled)return;
        for(LoggerOutputStream out : outputs)
        {
            if(!consoleOutput && outputs.indexOf(out) == 0)continue;
            if(message.getLevel().getValue() >= out.getMinValue())out.print(out.getFormatter().format(message.setPrintColor(out.supportColor())));
        }
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

    public static void exception(Exception e)
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        logger.log(LogMessage.pack(Level.ERROR, sw.toString()));
    }

    public void disableConsole()
    {
        consoleOutput = false;
    }

    public void enableConsole()
    {
        consoleOutput = true;
    }

    public interface LoggerFormatter
    {
        String format(LogMessage message);
    }

}
