package it.polimi.se2019.controller.settings;

import it.polimi.se2019.utils.logging.Logger;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class MatchSettings
{
    private static MatchSettings instance;

    private Properties properties;

    private Setting<Integer> skullNumber;
    private Setting<Integer> playersNumber;
    private Setting<Integer> startTimerSeconds;
    private Setting<Integer> responseTimerSeconds;
    private Setting<Integer> mapNumber;

    private MatchSettings()
    {
        loadSettings();
    }

    public static MatchSettings getInstance()
    {
        if(instance == null)instance = new MatchSettings();
        return instance;
    }

    private void loadSettings()
    {
        try
        {
            Logger.info("Loading settings from file...");
            properties = new Properties();
            properties.load(MatchSettings.class.getResourceAsStream("/properties/match_settings.properties"));

            skullNumber = new Setting<>("skull_number", 8);
            loadInteger(skullNumber);
            playersNumber = new Setting<>("players_number", 5);
            loadInteger(playersNumber);
            startTimerSeconds = new Setting<>("start_timer_seconds", 10);
            loadInteger(startTimerSeconds);
            responseTimerSeconds = new Setting<>("round_timer_seconds", 120);
            loadInteger(responseTimerSeconds);
            mapNumber = new Setting<>("map_number", -1);
            loadInteger(mapNumber);
        }
        catch (IOException e)
        {
            Logger.error("Error loading settings from file.");
            Logger.exception(e);
            Logger.info("Loading default settings...");
        }
        finally
        {
            Logger.info("Settings loaded!");
        }
    }

    private void loadInteger(Setting<Integer> setting)
    {
        String value = properties.getProperty(setting.getName());
        if(value == null)return;
        try
        {
            setting.set(Integer.parseInt(value));
        }
        catch (NumberFormatException e)
        {
            Logger.warning("Setting "+setting.getName()+" not loaded!");
            Logger.warning("Setting "+setting.getName()+" is set to default value ("+setting.get()+")");
        }
    }

    private void loadString(Setting<String> setting)
    {
        setting.set(properties.getProperty(setting.getName()));
    }

    public int getSkullNumber()
    {
        return skullNumber.get();
    }

    public int getPlayersNumber()
    {
        return playersNumber.get();
    }

    public int getStartTimerSeconds()
    {
        return startTimerSeconds.get();
    }

    public int getResponseTimerSeconds()
    {
        return responseTimerSeconds.get();
    }

    public int getMapNumber()
    {
        if(mapNumber.get() == -1)return new Random().nextInt(4)+1;
        return mapNumber.get();
    }

}
