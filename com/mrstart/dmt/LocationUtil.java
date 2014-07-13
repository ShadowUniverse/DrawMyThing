package com.mrstart.dmt;

import org.bukkit.*;

public class LocationUtil
{

    public LocationUtil()
    {
    }

    public static String LocationToString(Location l)
    {
        return (new StringBuilder(String.valueOf(String.valueOf((new StringBuilder(String.valueOf(l.getWorld().getName()))).append(":").append(l.getBlockX()).toString())))).append(":").append(String.valueOf(l.getBlockY())).append(":").append(String.valueOf(l.getBlockZ())).toString();
    }

    public static Location StringToLoc(String s)
    {
        Location l = null;
        try
        {
            World world = Bukkit.getWorld(s.split(":")[0]);
            Double x = Double.valueOf(Double.parseDouble(s.split(":")[1]));
            Double y = Double.valueOf(Double.parseDouble(s.split(":")[2]));
            Double z = Double.valueOf(Double.parseDouble(s.split(":")[3]));
            l = new Location(world, x.doubleValue(), y.doubleValue(), z.doubleValue());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return l;
    }
}