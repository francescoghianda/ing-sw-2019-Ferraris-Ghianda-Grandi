package it.polimi.se2019.card;

import it.polimi.se2019.player.Player;

import java.util.HashMap;

public class CardScriptExecutor
{
    private final Player contextPlayer;
    private String[] currScript;

    private HashMap<String, Player> players;
    private HashMap<String, Block> blocks;

    public CardScriptExecutor(Player contextPlayer)
    {
        this.contextPlayer = contextPlayer;
        this.players = new HashMap<>();
        this.blocks = new HashMap<>();
    }

    public CardScriptExecutor setScript(String script)
    {
        currScript = script.split("\\r?\\n");
        return this;
    }

    public void execute()
    {
        for(int i = 0; i < currScript.length; i++)
        {
            String[] line = currScript[i].split(" ");
            check(line[0], line);
        }
    }

    private void check(String str, String[] line)
    {
        

    }



}
