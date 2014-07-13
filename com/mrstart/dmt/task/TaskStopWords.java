package com.mrstart.dmt.task;

import com.mrstart.dmt.game.Game;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskStopWords extends BukkitRunnable
{

    public TaskStopWords()
    {
    }

    public void run()
    {
        buildzone.setNotAcceptWords();
    }

    private Game buildzone;
}