package com.mrstart.dmt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtil
{

    public ChatUtil()
    {
    }

    public static void send(Player p, String message)
    {
        p.sendMessage((new StringBuilder(String.valueOf(PREFIX))).append(message).toString());
    }

    public static void broadcast(String message)
    {
        Bukkit.broadcastMessage((new StringBuilder(String.valueOf(PREFIX))).append(message).toString());
    }

    private static final String PREFIX;

    static 
    {
        PREFIX = (new StringBuilder()).append(ChatColor.BLUE).append("[DrawMyThing] ").append(ChatColor.WHITE).toString();
    }
}