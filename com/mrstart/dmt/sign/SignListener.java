package com.mrstart.dmt.sign;

import com.mrstart.dmt.ChatUtil;
import com.mrstart.dmt.DrawMyThing;
import com.mrstart.dmt.game.Game;
import com.mrstart.dmt.task.TaskRegisterSign;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;

public class SignListener
    implements Listener
{

    public SignListener(DrawMyThing plugin)
    {
        this.plugin = plugin;
    }

    public void onBlockPlaced(SignChangeEvent Event)
    {
        if(Event.getLine(0).startsWith("[joindmt]"))
        {
            if(Event.getLine(1).length() > 0 && Event.getPlayer().hasPermission("dmt.admin"))
            {
                String line2 = Event.getLine(1);
                if(Event.getBlock().getType() == Material.WALL_SIGN)
                {
                    if(plugin.getGameByName(line2) != null)
                    {
                        if(Event.getLine(2) != null)
                            (new TaskRegisterSign(Event.getBlock(), plugin.getGameByName(line2), Event.getLine(2))).runTaskLater(plugin, 20L);
                        else
                            (new TaskRegisterSign(Event.getBlock(), plugin.getGameByName(line2), "none")).runTaskLater(plugin, 20L);
                        ChatUtil.send(Event.getPlayer(), (new StringBuilder()).append(ChatColor.GREEN).append("Join sign successfully created!").toString());
                    } else
                    {
                        ChatUtil.send(Event.getPlayer(), (new StringBuilder("Unknown game: ")).append(line2).toString());
                    }
                } else
                {
                    ChatUtil.send(Event.getPlayer(), (new StringBuilder()).append(ChatColor.RED).append("The game sign must be a wall sign!").toString());
                }
            }
        } else
        if(Event.getLine(0).startsWith("[leavedmt]") && Event.getPlayer().hasPermission("dmt.admin"))
        {
            Event.setLine(0, (new StringBuilder()).append(ChatColor.GOLD).append("[DMT]").toString());
            Event.setLine(1, (new StringBuilder()).append(ChatColor.RED).append("Leave").toString());
            Event.setLine(2, "");
            Event.setLine(3, "");
        }
    }

    public void onBlockBroken(BlockBreakEvent Event)
    {
        Block b = Event.getBlock();
        if((b.getState() instanceof Sign) && b.hasMetadata("bmtjoinsign"))
            if(Event.getPlayer().hasPermission("dmt.admin"))
            {
                String name = ((MetadataValue)b.getMetadata("bmtjoinsign").get(0)).asString();
                if(plugin.getGameByName(name) != null)
                {
                    plugin.getGameByName(name).removeSign(b);
                    ChatUtil.send(Event.getPlayer(), (new StringBuilder()).append(ChatColor.RED).append("Sign removed").toString());
                }
            } else
            {
                Event.setCancelled(true);
            }
    }

    public void onInteract(PlayerInteractEvent Event)
    {
        if(Event.getClickedBlock() != null && Event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
        {
            Player player = Event.getPlayer();
            Block block = Event.getClickedBlock();
            if(block.getState() instanceof Sign)
            {
                Sign sign = (Sign)block.getState();
                if(block.hasMetadata("bmtjoinsign"))
                {
                    String game = ((MetadataValue)sign.getMetadata("bmtjoinsign").get(0)).asString();
                    if(plugin.getGameByName(game) != null && player.hasPermission("dmt.default"))
                        plugin.getGameByName(game).join(player);
                } else
                if(sign.getLine(0).equals((new StringBuilder()).append(ChatColor.GOLD).append("[DMT]").toString()) && sign.getLine(1).equals((new StringBuilder()).append(ChatColor.RED).append("Leave").toString()) && player.hasPermission("dmt.default") && player.hasMetadata("inbmt") && plugin.getGameByName(((MetadataValue)player.getMetadata("inbmt").get(0)).asString()) != null)
                    plugin.getGameByName(((MetadataValue)player.getMetadata("inbmt").get(0)).asString()).leave(player);
            }
        }
    }

    private final DrawMyThing plugin;
}