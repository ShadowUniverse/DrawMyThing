package com.mrstart.dmt.task;

import com.mrstart.dmt.ChatUtil;
import java.util.Iterator;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskAlert extends BukkitRunnable
{

    public TaskAlert(String message, List players)
    {
        MESSAGE = message;
        sendTo = players;
    }

    public void run()
    {
        Player p;
        for(Iterator iterator = sendTo.iterator(); iterator.hasNext(); ChatUtil.send(p, MESSAGE))
            p = (Player)iterator.next();

    }

    public void removePlayer(Player p)
    {
        if(sendTo.contains(p))
            sendTo.remove(p);
    }

    private final String MESSAGE;
    private List sendTo;
}