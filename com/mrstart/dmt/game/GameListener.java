package com.mrstart.dmt.game;

import com.mrstart.dmt.ChatUtil;
import com.mrstart.dmt.DrawMyThing;
import java.util.List;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

// Referenced classes of package com.mrstart.dmt.game:
//            Game

public class GameListener
    implements Listener
{

    public GameListener(DrawMyThing instance)
    {
        color = DyeColor.BLACK;
        this.instance = instance;
    }

    public void onPlayerLogOut(PlayerQuitEvent event)
    {
        if(event.getPlayer().hasMetadata("inbmt"))
            instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).leave(event.getPlayer());
    }

    public void onPlayerPlaceBlock(BlockPlaceEvent event)
    {
        if(event.getPlayer().hasMetadata("inbmt") && instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()) != null)
            event.setCancelled(true);
    }

    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(!event.getPlayer().hasMetadata("inbmt"))
            return;
        if(instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()) == null)
            return;
        if(instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).getBuilder() == null)
            return;
        if(instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).getBuilder().getName() != event.getPlayer().getName())
            return;
        if(event.getPlayer().getItemInHand() == null)
            return;
        if(event.getPlayer().getItemInHand().getType() == Material.STICK && event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().isBlocking())
        {
            Block b = event.getPlayer().getTargetBlock(null, 100);
            if(b.getType() != Material.WOOL)
                return;
            b.setTypeIdAndData(Material.WOOL.getId(), color.getData(), true);
            if(instance.getConfig().getBoolean("tool-sound"))
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.FIZZ, 1.0F, 1.0F);
            return;
        }
        if(event.getPlayer().getItemInHand().getType() == Material.WOOL && event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
                return;
            Block b = event.getPlayer().getTargetBlock(null, 100);
            if(b.getType() != Material.WOOL)
                return;
            b.setTypeIdAndData(Material.WOOL.getId(), DyeColor.WHITE.getData(), true);
            if(instance.getConfig().getBoolean("tool-sound"))
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BAT_TAKEOFF, 1.0F, 1.0F);
            return;
        }
        if(event.getPlayer().getItemInHand().getType() == Material.BLAZE_ROD && event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Block b = event.getPlayer().getTargetBlock(null, 100);
            Block b1 = b.getLocation().add(1.0D, 0.0D, 0.0D).getBlock();
            Block b2 = b.getLocation().add(0.0D, 1.0D, 0.0D).getBlock();
            Block b3 = b.getLocation().subtract(1.0D, 0.0D, 0.0D).getBlock();
            Block b4 = b.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
            if(b.getType() != Material.WOOL)
                return;
            b.setTypeIdAndData(Material.WOOL.getId(), color.getData(), true);
            b1.setTypeIdAndData(Material.WOOL.getId(), color.getData(), true);
            b2.setTypeIdAndData(Material.WOOL.getId(), color.getData(), true);
            b3.setTypeIdAndData(Material.WOOL.getId(), color.getData(), true);
            b4.setTypeIdAndData(Material.WOOL.getId(), color.getData(), true);
            if(instance.getConfig().getBoolean("tool-sound"))
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.FIZZ, 1.0F, 1.0F);
            return;
        }
        if(event.getPlayer().getItemInHand().getType() == Material.COMPASS)
        {
            if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            {
                return;
            } else
            {
                instance.cci.clear();
                instance.addCCIItems();
                event.getPlayer().openInventory(instance.cci);
                return;
            }
        } else
        {
            return;
        }
    }

    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        if(event.getPlayer().hasMetadata("inbmt") && !event.getMessage().startsWith("/dmt") && !event.getPlayer().hasPermission("dmt.admin"))
        {
            ChatUtil.send(event.getPlayer(), (new StringBuilder()).append(ChatColor.RED).append("You cannot execute commands while ingame!").toString());
            event.setCancelled(true);
        }
    }

    public void onPlayerBreakBlock(BlockBreakEvent event)
    {
        if(event.getPlayer().hasMetadata("inbmt"))
            event.setCancelled(true);
    }

    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        if(event.getPlayer().hasMetadata("inbmt"))
            event.setCancelled(true);
    }

    public void onPlayerHit(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player)
        {
            Player p = (Player)event.getEntity();
            if(p.hasMetadata("inbmt"))
                event.setCancelled(true);
        }
    }

    public void onPlayerHungerChange(FoodLevelChangeEvent event)
    {
        if(event.getEntity().hasMetadata("inbmt"))
            event.setCancelled(true);
    }

    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        if(event.getPlayer().hasMetadata("inbmt") && instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()) != null && instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).isStarted())
            if(instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).getBuilder().getName() == event.getPlayer().getName())
            {
                ChatUtil.send(event.getPlayer(), (new StringBuilder()).append(ChatColor.RED).append("You cannot chat while you are a drawer!").toString());
                event.setCancelled(true);
            } else
            {
                event.setCancelled(true);
                String word = instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).getWord();
                if(instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).hasFound(event.getPlayer()))
                    ChatUtil.send(event.getPlayer(), (new StringBuilder()).append(ChatColor.RED).append("You have already found the word!").toString());
                else
                if(event.getMessage().toLowerCase().contains(word))
                    instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).wordFoundBy(event.getPlayer());
                else
                    instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).sendMessage((new StringBuilder()).append(ChatColor.BOLD).append(event.getPlayer().getName()).append(": ").append(ChatColor.RESET).append(event.getMessage().toLowerCase()).toString());
            }
    }

    public void onPlayerMove(PlayerMoveEvent event)
    {
        if(event.getPlayer().hasMetadata("inbmt") && instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()) != null && instance.getGameByName(((MetadataValue)event.getPlayer().getMetadata("inbmt").get(0)).asString()).isStarted() && event.getPlayer().getDisplayName().contains("[BUILDER]"))
        {
            if(event.getTo().getX() == event.getFrom().getX() && event.getTo().getY() == event.getFrom().getY() && event.getTo().getZ() == event.getFrom().getZ())
                return;
            event.setTo(event.getFrom());
        }
    }

    public void onInvClick(InventoryClickEvent event)
    {
        Player p = (Player)event.getWhoClicked();
        if(p.hasMetadata("inbmt"))
            if(event.getInventory().getName() == instance.cci.getName())
            {
                if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
                    return;
                if(event.getCurrentItem().getItemMeta().getDisplayName().contains("White"))
                {
                    setPencilColor(DyeColor.WHITE);
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    p.closeInventory();
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Black"))
                {
                    setPencilColor(DyeColor.BLACK);
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    p.closeInventory();
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Red"))
                {
                    setPencilColor(DyeColor.RED);
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    p.closeInventory();
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Orange"))
                {
                    setPencilColor(DyeColor.ORANGE);
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    p.closeInventory();
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Yellow"))
                {
                    setPencilColor(DyeColor.YELLOW);
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    p.closeInventory();
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Green"))
                {
                    setPencilColor(DyeColor.LIME);
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    p.closeInventory();
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Blue"))
                {
                    setPencilColor(DyeColor.LIGHT_BLUE);
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    p.closeInventory();
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Purple"))
                {
                    setPencilColor(DyeColor.PURPLE);
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    p.closeInventory();
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Brown"))
                {
                    setPencilColor(DyeColor.BROWN);
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    p.closeInventory();
                    return;
                }
            } else
            {
                event.setCancelled(true);
                p.closeInventory();
                return;
            }
    }

    public void setPencilColor(DyeColor color)
    {
        this.color = color;
    }

    public DyeColor getColor()
    {
        return color;
    }

    private DrawMyThing instance;
    private Game game;
    private DyeColor color;
}