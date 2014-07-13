package com.mrstart.dmt.cuboid;

import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;

public class CuboidZone
{

    public CuboidZone(Block corner1, Block corner2)
    {
        if(corner1.getWorld().equals(corner2.getWorld()))
        {
            this.corner1 = corner1;
            this.corner2 = corner2;
            world = corner1.getWorld();
        } else
        {
            throw new IllegalArgumentException("All cuboid blocks aren't in the same world !");
        }
    }

    public void set(Material material)
    {
        Block b;
        for(Iterator iterator = toArray().iterator(); iterator.hasNext(); b.setType(material))
            b = (Block)iterator.next();

    }

    public boolean contains(Block b)
    {
        return toArray().contains(b);
    }

    public List toArray()
    {
        List result = new ArrayList();
        int minX = Math.min(corner1.getX(), corner2.getX());
        int minY = Math.min(corner1.getY(), corner2.getY());
        int minZ = Math.min(corner1.getZ(), corner2.getZ());
        int maxX = Math.max(corner1.getX(), corner2.getX());
        int maxY = Math.max(corner1.getY(), corner2.getY());
        int maxZ = Math.max(corner1.getZ(), corner2.getZ());
        for(int x = minX; x <= maxX; x++)
        {
            for(int y = minY; y <= maxY; y++)
            {
                for(int z = minZ; z <= maxZ; z++)
                    result.add(world.getBlockAt(new Location(world, x, y, z)));

            }

        }

        return result;
    }

    public String toString()
    {
        Location l = corner1.getLocation();
        String s = (new StringBuilder(String.valueOf(String.valueOf((new StringBuilder(String.valueOf(world.getName()))).append(":").append(l.getBlockX()).toString())))).append(":").append(String.valueOf(l.getBlockY())).append(":").append(String.valueOf(l.getBlockZ())).toString();
        Location l1 = corner2.getLocation();
        String s1 = (new StringBuilder(String.valueOf(String.valueOf((new StringBuilder(String.valueOf(world.getName()))).append(":").append(l1.getBlockX()).toString())))).append(":").append(String.valueOf(l1.getBlockY())).append(":").append(String.valueOf(l1.getBlockZ())).toString();
        String result = (new StringBuilder(String.valueOf(s))).append(";").append(s1).toString();
        return result;
    }

    public Block getCorner1()
    {
        return corner1;
    }

    public Block getCorner2()
    {
        return corner2;
    }

    public Location getBottomCenter()
    {
        int minY = Math.min(corner1.getY(), corner2.getY());
        int minX = Math.min(corner1.getX(), corner2.getX());
        int minZ = Math.min(corner1.getZ(), corner2.getZ());
        int maxX = Math.max(corner1.getX(), corner2.getX());
        int maxZ = Math.max(corner1.getZ(), corner2.getZ());
        return new Location(world, minX + (maxX - minX) / 2, minY, minZ + (maxZ - minZ) / 2);
    }

    public void clear()
    {
        Block b;
        for(Iterator iterator = toArray().iterator(); iterator.hasNext(); b.setType(Material.AIR))
            b = (Block)iterator.next();

    }

    public void setWool(DyeColor dc)
    {
        Block b;
        for(Iterator iterator = toArray().iterator(); iterator.hasNext(); b.setTypeIdAndData(Material.WOOL.getId(), dc.getData(), true))
            b = (Block)iterator.next();

    }

    public Block corner1;
    private Block corner2;
    private World world;
}