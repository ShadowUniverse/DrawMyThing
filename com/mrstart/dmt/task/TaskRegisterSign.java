package com.mrstart.dmt.task;

import com.mrstart.dmt.game.Game;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskRegisterSign extends BukkitRunnable
{

    public TaskRegisterSign(Block b, Game bz, String display)
    {
        this.b = b;
        this.bz = bz;
        this.display = display;
    }

    public void run()
    {
        bz.registerSign(b, display);
    }

    private Block b;
    private Game bz;
    private String display;
}