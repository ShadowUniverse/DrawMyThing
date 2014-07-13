package com.mrstart.dmt.task;

import com.mrstart.dmt.game.Game;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskStart extends BukkitRunnable
{

    public TaskStart(Game b)
    {
        this.b = b;
    }

    public void run()
    {
        b.start();
    }

    Game b;
}