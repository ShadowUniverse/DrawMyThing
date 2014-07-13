package com.mrstart.dmt.game;

import com.mrstart.dmt.*;
import com.mrstart.dmt.cuboid.CuboidZone;
import com.mrstart.dmt.task.*;
import java.util.*;
import me.confuser.barapi.BarAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public class Game
    implements Listener
{

    public Game(CuboidZone build, Location spawn, Location bspawn, String name, DrawMyThing instance)
    {
        score = new HashMap();
        ready = new HashMap();
        hasBeenBuilder = new HashMap();
        inventories = new HashMap();
        gamemode = new HashMap();
        foodLevel = new HashMap();
        hasFound = new ArrayList();
        minplayers = 2;
        maxplayers = 12;
        buildPerPlayer = 2;
        wordHasBeenFound = false;
        playerFound = 0;
        tasks = new ArrayList();
        acceptWords = true;
        signs = new ArrayList();
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        pencil = new ItemStack(Material.STICK);
        ItemMeta pm = pencil.getItemMeta();
        pm.setDisplayName((new StringBuilder()).append(ChatColor.AQUA).append("Pencil").toString());
        pencil.setItemMeta(pm);
        bp = new ItemStack(Material.BLAZE_ROD);
        ItemMeta bpm = bp.getItemMeta();
        bpm.setDisplayName((new StringBuilder()).append(ChatColor.YELLOW).append("Big Pencil").toString());
        bp.setItemMeta(bpm);
        eraser = new ItemStack(Material.WOOL, 1, DyeColor.RED.getData());
        ItemMeta em = eraser.getItemMeta();
        em.setDisplayName((new StringBuilder()).append(ChatColor.RED).append("Eraser").toString());
        eraser.setItemMeta(em);
        colorpicker = new ItemStack(Material.COMPASS);
        ItemMeta ccm = colorpicker.getItemMeta();
        ccm.setDisplayName((new StringBuilder()).append(ChatColor.GREEN).append("Choose Color").toString());
        colorpicker.setItemMeta(ccm);
        buildzone = build;
        this.spawn = spawn;
        this.bspawn = bspawn;
        this.name = name;
        this.instance = instance;
        objective = board.registerNewObjective((new StringBuilder(String.valueOf(this.name))).append("_points").toString(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName((new StringBuilder()).append(ChatColor.GOLD).append("Scores").toString());
    }

    public void cancelTasks()
    {
        BukkitRunnable r;
        for(Iterator iterator = tasks.iterator(); iterator.hasNext(); r.cancel())
            r = (BukkitRunnable)iterator.next();

    }

    public void leave(Player player)
    {
        if(score.containsKey(player))
        {
            if(!started && players == maxplayers)
                cancelTasks();
            player.setFoodLevel(((Integer)foodLevel.get(player)).intValue());
            foodLevel.remove(player);
            score.remove(player);
            ready.remove(player);
            hasBeenBuilder.remove(player);
            if(hasFound(player))
                playerFound--;
            hasFound.remove(player);
            if(inventories.get(player) != null)
                player.getInventory().setContents((ItemStack[])inventories.get(player));
            inventories.remove(player);
            player.setDisplayName(player.getName());
            player.setGameMode((GameMode)gamemode.get(player));
            BarAPI.removeBar(player);
            gamemode.remove(player);
            board.resetScores(player);
            player.setScoreboard(manager.getMainScoreboard());
            players--;
            player.teleport(LocationUtil.StringToLoc(((MetadataValue)player.getMetadata("oldLoc").get(0)).asString()));
            player.removeMetadata("oldLoc", instance);
            player.removeMetadata("inbmt", instance);
            updateSigns();
            if(players > 1)
                sendMessage((new StringBuilder(String.valueOf(player.getName()))).append(" has left the game! (").append(String.valueOf(players)).append("/").append(String.valueOf((new StringBuilder(String.valueOf(maxplayers))).append(")").toString())).toString());
            else
            if(isStarted())
            {
                cancelTasks();
                stop();
            }
        }
    }

    public void join(Player player)
    {
        if(!isStarted())
        {
            if(!score.containsKey(player))
            {
                player.setMetadata("oldLoc", new FixedMetadataValue(instance, LocationUtil.LocationToString(player.getLocation())));
                player.teleport(spawn);
                if(players < maxplayers)
                {
                    ItemStack inventory[] = player.getInventory().getContents();
                    ItemStack saveInventory[] = new ItemStack[inventory.length];
                    for(int i = 0; i < inventory.length; i++)
                        if(inventory[i] != null)
                            saveInventory[i] = inventory[i].clone();

                    inventories.put(player, saveInventory);
                    foodLevel.put(player, Integer.valueOf(player.getFoodLevel()));
                    player.setFoodLevel(20);
                    player.setDisplayName(player.getName());
                    gamemode.put(player, player.getGameMode());
                    player.setGameMode(GameMode.ADVENTURE);
                    player.getInventory().clear();
                    BarAPI.removeBar(player);
                    player.setMetadata("inbmt", new FixedMetadataValue(instance, getName()));
                    player.setScoreboard(board);
                    score.put(player, Integer.valueOf(0));
                    ready.put(player, Boolean.valueOf(false));
                    players++;
                    if(players > 0)
                    {
                        Player p;
                        for(Iterator iterator = score.keySet().iterator(); iterator.hasNext(); ChatUtil.send(p, (new StringBuilder()).append(ChatColor.GRAY).append(player.getName()).append(" has joined the game! (").append(String.valueOf(players)).append("/").append(String.valueOf((new StringBuilder(String.valueOf(maxplayers))).append(")").toString())).toString()))
                            p = (Player)iterator.next();

                    }
                    if(players >= minplayers)
                    {
                        setTimedBarForAll((new StringBuilder()).append(ChatColor.GOLD).append(ChatColor.BOLD).append("Game Starting").toString(), 10);
                        TaskStart start = new TaskStart(this);
                        start.runTaskLater(instance, 200L);
                        tasks.add(start);
                    }
                    updateSigns();
                } else
                {
                    ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("The game is currently full!").toString());
                }
            }
        } else
        {
            ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("The game is started!").toString());
        }
    }

    private String getNewWord()
    {
        return instance.getRandomWord();
    }

    public void start()
    {
        if(!started)
        {
            cancelTasks();
            started = true;
            word = null;
            if(players < 3)
                buildPerPlayer = 3;
            hasBeenBuilder.clear();
            Player p;
            for(Iterator iterator = score.keySet().iterator(); iterator.hasNext(); hasBeenBuilder.put(p, Integer.valueOf(0)))
                p = (Player)iterator.next();

            playSoundForAll(Sound.NOTE_PIANO);
            startRound();
            updateSigns();
        }
    }

    public void startRound()
    {
        if(word != null)
            sendMessage((new StringBuilder()).append(ChatColor.GOLD).append(ChatColor.BOLD).append("The word was ").append(word).toString());
        cancelTasks();
        wordHasBeenFound = false;
        playerFound = 0;
        hasFound.clear();
        acceptWords = true;
        word = getNewWord();
        wordf = new StringBuilder(word.replaceAll("[a-zA-Z]", "_"));
        buildzone.clear();
        buildzone.setWool(DyeColor.WHITE);
        getNextBuilder();
        TaskAddLetter letter1 = new TaskAddLetter(this);
        TaskAddLetter letter2 = new TaskAddLetter(this);
        TaskAddLetter letter3 = new TaskAddLetter(this);
        TaskAlert alert1 = new TaskAlert((new StringBuilder()).append(ChatColor.GOLD).append("30 seconds left until the end of the round!").toString(), getPlayers());
        TaskAlert alert2 = new TaskAlert((new StringBuilder()).append(ChatColor.GOLD).append("10 seconds left until the end of the round!").toString(), getPlayers());
        TaskNextRound endRound = new TaskNextRound(this);
        endRound.runTaskLater(instance, 1500L);
        tasks.add(endRound);
        TaskAlert endRoundMsg = new TaskAlert((new StringBuilder()).append(ChatColor.GOLD).append("Out of time! Next round starting in 5 seconds!").toString(), getPlayers());
        endRoundMsg.runTaskLater(instance, 1400L);
        alert1.runTaskLater(instance, 600L);
        alert2.runTaskLater(instance, 1200L);
        letter1.runTaskLater(instance, 800L);
        letter2.runTaskLater(instance, 1000L);
        letter3.runTaskLater(instance, 1100L);
        tasks.add(alert1);
        tasks.add(alert2);
        tasks.add(letter1);
        tasks.add(letter2);
        tasks.add(letter3);
        tasks.add(endRoundMsg);
    }

    public void removePlayerFromAlerts(Player p)
    {
        for(Iterator iterator = tasks.iterator(); iterator.hasNext();)
        {
            BukkitRunnable task = (BukkitRunnable)iterator.next();
            if(task instanceof TaskAlert)
            {
                TaskAlert taskAlert = (TaskAlert)task;
                taskAlert.removePlayer(p);
            }
        }

    }

    public List getPlayers()
    {
        List result = new ArrayList();
        Player p;
        for(Iterator iterator = score.keySet().iterator(); iterator.hasNext(); result.add(p))
            p = (Player)iterator.next();

        return result;
    }

    public void stop()
    {
        started = false;
        buildzone.clear();
        buildzone.setWool(DyeColor.WHITE);
        removeBarForAll();
        List toKick = new ArrayList();
        Player p;
        for(Iterator iterator = score.keySet().iterator(); iterator.hasNext(); toKick.add(p))
            p = (Player)iterator.next();

        Player p;
        for(Iterator iterator1 = toKick.iterator(); iterator1.hasNext(); leave(p))
            p = (Player)iterator1.next();

    }

    private void getNextBuilder()
    {
        if(getBuilder() != null)
        {
            builder.setGameMode(GameMode.ADVENTURE);
            builder.setFlying(false);
            builder.teleport(spawn);
            builder.setDisplayName(builder.getName());
            builder.getInventory().clear();
            BarAPI.removeBar(builder);
            builder = null;
        }
        for(int i = 0; i < buildPerPlayer; i++)
        {
            for(Iterator iterator = hasBeenBuilder.keySet().iterator(); iterator.hasNext();)
            {
                Player p = (Player)iterator.next();
                if(((Integer)hasBeenBuilder.get(p)).intValue() <= i)
                {
                    setBuilder(p);
                    return;
                }
            }

        }

        sendMessage((new StringBuilder()).append(ChatColor.RED).append("GAME OVER").toString());
        Player winner = null;
        for(Iterator iterator1 = score.keySet().iterator(); iterator1.hasNext();)
        {
            Player p = (Player)iterator1.next();
            if(winner != null)
            {
                if(((Integer)score.get(p)).intValue() > ((Integer)score.get(winner)).intValue())
                    winner = p;
            } else
            {
                winner = p;
            }
        }

        if(score.containsKey(winner))
        {
            sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Scores:").toString());
            Player p;
            for(Iterator iterator2 = score.keySet().iterator(); iterator2.hasNext(); sendMessage((new StringBuilder()).append(ChatColor.AQUA).append(p.getName()).append(":").append(ChatColor.YELLOW).append(" ").append(String.valueOf(score.get(p))).toString()))
                p = (Player)iterator2.next();

            sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("WINNER: ").append(winner.getName()).toString());
        }
        stop();
    }

    private void setBuilder(Player p)
    {
        builder = p;
        hasBeenBuilder.put(p, Integer.valueOf(((Integer)hasBeenBuilder.get(p)).intValue() + 1));
        p.teleport(bspawn);
        p.setGameMode(GameMode.CREATIVE);
        p.setFlying(true);
        p.getInventory().addItem(new ItemStack[] {
            pencil
        });
        p.getInventory().addItem(new ItemStack[] {
            bp
        });
        p.getInventory().addItem(new ItemStack[] {
            eraser
        });
        p.getInventory().addItem(new ItemStack[] {
            colorpicker
        });
        p.setDisplayName((new StringBuilder()).append(ChatColor.RED).append("[BUILDER] ").append(p.getName()).toString());
        sendMessage((new StringBuilder()).append(ChatColor.GOLD).append(ChatColor.BOLD).append(p.getName()).append(" is the drawer this round!").toString());
        setBarForAll((new StringBuilder()).append(ChatColor.YELLOW).append(ChatColor.BOLD).append("Guess ").append(ChatColor.WHITE).append(ChatColor.BOLD).append(wordf).toString());
        BarAPI.setMessage(p, (new StringBuilder()).append(ChatColor.YELLOW).append("Your word is ").append(ChatColor.GOLD).append(ChatColor.BOLD).append(word).toString());
        BarAPI.setHealth(p, 100F);
    }

    public void sendMessage(String message)
    {
        Player p;
        for(Iterator iterator = score.keySet().iterator(); iterator.hasNext(); ChatUtil.send(p, message))
            p = (Player)iterator.next();

    }

    public void setBarForAll(String message)
    {
        Player p;
        for(Iterator iterator = score.keySet().iterator(); iterator.hasNext(); BarAPI.setMessage(p, message))
            p = (Player)iterator.next();

    }

    public void setTimedBarForAll(String message, int seconds)
    {
        Player p;
        for(Iterator iterator = score.keySet().iterator(); iterator.hasNext(); BarAPI.setMessage(p, message, seconds))
            p = (Player)iterator.next();

    }

    public void removeBarForAll()
    {
        Player p;
        for(Iterator iterator = score.keySet().iterator(); iterator.hasNext(); BarAPI.removeBar(p))
            p = (Player)iterator.next();

    }

    public void teleportAllToGameSpawn()
    {
        Player p;
        for(Iterator iterator = score.keySet().iterator(); iterator.hasNext(); p.teleport(spawn))
            p = (Player)iterator.next();

    }

    public void playSoundForAll(Sound sound)
    {
        Player p;
        for(Iterator iterator = score.keySet().iterator(); iterator.hasNext(); p.playSound(p.getLocation(), sound, 1.0F, 1.0F))
            p = (Player)iterator.next();

    }

    public boolean isStarted()
    {
        return started;
    }

    public String getName()
    {
        return name;
    }

    public void save(FileConfiguration file)
    {
        file.set((new StringBuilder("games")).append(getName()).append(".pos1").toString(), LocationUtil.LocationToString(buildzone.getCorner1().getLocation()));
        file.set((new StringBuilder("games")).append(getName()).append(".pos2").toString(), LocationUtil.LocationToString(buildzone.getCorner2().getLocation()));
        file.set((new StringBuilder("games")).append(getName()).append(".spawn").toString(), LocationUtil.LocationToString(spawn));
        file.set((new StringBuilder("games")).append(getName()).append(".bspawn").toString(), LocationUtil.LocationToString(bspawn));
        file.set((new StringBuilder("games")).append(getName()).append(".bspawn").toString(), LocationUtil.LocationToString(bspawn));
        file.set((new StringBuilder("games")).append(getName()).append(".minplayers").toString(), Integer.valueOf(minplayers));
        file.set((new StringBuilder("games")).append(getName()).append(".maxplayers").toString(), Integer.valueOf(maxplayers));
        List signData = new ArrayList();
        for(Iterator iterator = signs.iterator(); iterator.hasNext();)
        {
            Block s = (Block)iterator.next();
            if(s.getType() == Material.WALL_SIGN)
            {
                String loc = LocationUtil.LocationToString(s.getLocation());
                String display;
                if(s.hasMetadata("display"))
                    display = (new StringBuilder(";")).append(((MetadataValue)s.getMetadata("display").get(0)).asString()).toString();
                else
                    display = ";none";
                String result = (new StringBuilder(String.valueOf(loc))).append(display).toString();
                signData.add(result);
            }
        }

        file.set((new StringBuilder("games")).append(getName()).append(".signs").toString(), signData);
    }

    public void remove(FileConfiguration file)
    {
        stop();
        file.set(getName(), null);
        instance.saveConfig();
    }

    public static Game load(FileConfiguration file, String name, DrawMyThing instance)
    {
        Location corner1 = LocationUtil.StringToLoc(file.getString((new StringBuilder("games")).append(name).append(".pos1").toString()));
        Location corner2 = LocationUtil.StringToLoc(file.getString((new StringBuilder("games")).append(name).append(".pos2").toString()));
        Location spawn = LocationUtil.StringToLoc(file.getString((new StringBuilder("games")).append(name).append(".spawn").toString()));
        Location bspawn = LocationUtil.StringToLoc(file.getString((new StringBuilder("games")).append(name).append(".bspawn").toString()));
        Game b = new Game(new CuboidZone(corner1.getBlock(), corner2.getBlock()), spawn, bspawn, name, instance);
        b.setMinPlayers(file.getInt((new StringBuilder("games")).append(name).append(".minplayers").toString()));
        b.setMaxPlayers(file.getInt((new StringBuilder("games")).append(name).append(".maxplayers").toString()));
        if(file.getList((new StringBuilder("games")).append(name).append(".signs").toString()) != null)
        {
            List signLoc = file.getList((new StringBuilder("games")).append(name).append(".signs").toString());
            for(Iterator iterator = signLoc.iterator(); iterator.hasNext();)
            {
                String s = (String)iterator.next();
                String loc = s.split(";")[0];
                String display = s.split(";")[1];
                Location l = LocationUtil.StringToLoc(loc);
                Block block = l.getBlock();
                if((block.getState() instanceof Sign) && block.getType() == Material.WALL_SIGN)
                    b.registerSign(block, display);
            }

        }
        return b;
    }

    public void addRandomLetter()
    {
        Random r = new Random();
        int randomLetter = r.nextInt(word.length());
        wordf.setCharAt(randomLetter, word.charAt(randomLetter));
        setBarForAll((new StringBuilder()).append(ChatColor.YELLOW).append(ChatColor.BOLD).append("Guess ").append(ChatColor.WHITE).append(ChatColor.BOLD).append(wordf).toString());
        playSoundForAll(Sound.ITEM_PICKUP);
        BarAPI.setMessage(builder, (new StringBuilder()).append(ChatColor.YELLOW).append("Your word is ").append(ChatColor.GOLD).append(ChatColor.BOLD).append(word).toString());
    }

    public void setMinPlayers(int i)
    {
        minplayers = i;
    }

    public void setMaxPlayers(int i)
    {
        maxplayers = i;
    }

    public Player getBuilder()
    {
        return builder;
    }

    public CuboidZone getBuildZone()
    {
        return buildzone;
    }

    public String getWord()
    {
        return word;
    }

    public void increaseScore(Player p, int value)
    {
        if(score.containsKey(p))
        {
            score.put(p, Integer.valueOf(((Integer)score.get(p)).intValue() + value));
            Score scoreBoard = objective.getScore(p);
            scoreBoard.setScore(((Integer)score.get(p)).intValue());
        }
    }

    public void wordFoundBy(Player player)
    {
        if(acceptWords && !hasFound.contains(player))
        {
            hasFound.add(player);
            Player p;
            for(Iterator iterator = score.keySet().iterator(); iterator.hasNext(); p.getWorld().playSound(p.getLocation(), Sound.NOTE_PIANO, 1.0F, 1.0F))
                p = (Player)iterator.next();

            if(!wordHasBeenFound)
            {
                sendMessage((new StringBuilder()).append(ChatColor.GOLD).append(ChatColor.BOLD).append("+3 ").append(ChatColor.GREEN).append(ChatColor.BOLD).append(player.getName()).append(" has found the word!").toString());
                builder.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append(ChatColor.BOLD).append("+2 ").append(ChatColor.GREEN).append(ChatColor.BOLD).append("Somebody has found your word!").toString());
                increaseScore(player, 3);
                increaseScore(builder, 2);
                wordHasBeenFound = true;
            } else
            {
                sendMessage((new StringBuilder()).append(ChatColor.GOLD).append(ChatColor.BOLD).append("+1 ").append(ChatColor.GREEN).append(ChatColor.BOLD).append(player.getName()).append(" has found the word!").toString());
                increaseScore(player, 1);
            }
            playerFound++;
        }
        if(playerFound == players - 1)
        {
            sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("The next round will start in 5 seconds!").toString());
            cancelTasks();
            TaskNextRound endRound = new TaskNextRound(this);
            endRound.runTaskLater(instance, 100L);
            tasks.add(endRound);
        }
    }

    public void setNotAcceptWords()
    {
        acceptWords = false;
    }

    public int getMinPlayers()
    {
        return minplayers;
    }

    public int getMaxPlayers()
    {
        return maxplayers;
    }

    public boolean hasFound(Player player)
    {
        return hasFound.contains(player);
    }

    public void updateSigns()
    {
        for(Iterator iterator = signs.iterator(); iterator.hasNext();)
        {
            Block b = (Block)iterator.next();
            if(b.getState() instanceof Sign)
            {
                Sign s = (Sign)b.getState();
                s.setLine(0, (new StringBuilder()).append(ChatColor.GOLD).append("[DMT]").toString());
                s.setLine(1, (new StringBuilder()).append(ChatColor.YELLOW).append(getName()).toString());
                s.setLine(2, (new StringBuilder()).append(ChatColor.GRAY).append(String.valueOf(players)).append("/").append(maxplayers).toString());
                s.setLine(3, started ? (new StringBuilder()).append(ChatColor.RED).append("INGAME").toString() : (new StringBuilder()).append(ChatColor.GREEN).append("Join").toString());
                s.update();
            }
        }

    }

    public void registerSign(Block block)
    {
        registerSign(block, "none");
    }

    public void registerSign(Block block, String display)
    {
        if(block.getState() instanceof Sign)
        {
            signs.add(block);
            block.setMetadata("bmtjoinsign", new FixedMetadataValue(instance, getName()));
            updateSigns();
        }
    }

    public void removeSign(Block block)
    {
        if(block.getState() instanceof Sign)
        {
            signs.remove(block);
            if(block.hasMetadata("bmtjoinsign"))
                block.removeMetadata("bmtjoinsign", instance);
        }
    }

    private DrawMyThing instance;
    private CuboidZone buildzone;
    private Location spawn;
    private Location bspawn;
    private Map score;
    private Map ready;
    private Map hasBeenBuilder;
    private Map inventories;
    private Map gamemode;
    private Map foodLevel;
    private List hasFound;
    private Player builder;
    private int players;
    private int minplayers;
    private int maxplayers;
    private int buildPerPlayer;
    private String name;
    private String word;
    private StringBuilder wordf;
    private boolean started;
    private boolean wordHasBeenFound;
    private int playerFound;
    private List tasks;
    private boolean acceptWords;
    private List signs;
    ScoreboardManager manager;
    Scoreboard board;
    Objective objective;
    public ItemStack pencil;
    public ItemStack bp;
    public ItemStack eraser;
    public ItemStack colorpicker;
}