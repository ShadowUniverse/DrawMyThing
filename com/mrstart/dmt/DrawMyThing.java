package com.mrstart.dmt;

import com.mrstart.dmt.cuboid.CuboidZone;
import com.mrstart.dmt.game.Game;
import com.mrstart.dmt.game.GameListener;
import com.mrstart.dmt.sign.SignListener;
import java.util.*;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

// Referenced classes of package com.mrstart.dmt:
//            ChatUtil, LocationUtil

public class DrawMyThing extends JavaPlugin
{

    public DrawMyThing()
    {
        games = new ArrayList();
        logger = Logger.getLogger("Minecraft");
        plugin = this;
        words = new ArrayList();
        white = new ItemStack(Material.WOOL);
        ItemMeta wm = white.getItemMeta();
        wm.setDisplayName((new StringBuilder()).append(ChatColor.WHITE).append("White").toString());
        white.setItemMeta(wm);
        black = new ItemStack(Material.WOOL, 1, DyeColor.BLACK.getData());
        ItemMeta bm = black.getItemMeta();
        bm.setDisplayName((new StringBuilder()).append(ChatColor.BLACK).append("Black").toString());
        black.setItemMeta(bm);
        red = new ItemStack(Material.WOOL, 1, DyeColor.RED.getData());
        ItemMeta rm = red.getItemMeta();
        rm.setDisplayName((new StringBuilder()).append(ChatColor.RED).append("Red").toString());
        red.setItemMeta(rm);
        orange = new ItemStack(Material.WOOL, 1, DyeColor.ORANGE.getData());
        ItemMeta om = orange.getItemMeta();
        om.setDisplayName((new StringBuilder()).append(ChatColor.GOLD).append("Orange").toString());
        orange.setItemMeta(om);
        yellow = new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getData());
        ItemMeta ym = yellow.getItemMeta();
        ym.setDisplayName((new StringBuilder()).append(ChatColor.YELLOW).append("Yellow").toString());
        yellow.setItemMeta(ym);
        green = new ItemStack(Material.WOOL, 1, DyeColor.LIME.getData());
        ItemMeta gm = green.getItemMeta();
        gm.setDisplayName((new StringBuilder()).append(ChatColor.GREEN).append("Green").toString());
        green.setItemMeta(gm);
        blue = new ItemStack(Material.WOOL, 1, DyeColor.LIGHT_BLUE.getData());
        ItemMeta blm = blue.getItemMeta();
        blm.setDisplayName((new StringBuilder()).append(ChatColor.AQUA).append("Blue").toString());
        blue.setItemMeta(blm);
        purple = new ItemStack(Material.WOOL, 1, DyeColor.PURPLE.getData());
        ItemMeta pm = purple.getItemMeta();
        pm.setDisplayName((new StringBuilder()).append(ChatColor.DARK_PURPLE).append("Purple").toString());
        purple.setItemMeta(pm);
        brown = new ItemStack(Material.WOOL, 1, DyeColor.BROWN.getData());
        ItemMeta brm = brown.getItemMeta();
        brm.setDisplayName((new StringBuilder()).append(ChatColor.GRAY).append("Brown").toString());
        brown.setItemMeta(brm);
    }

    public void onDisable()
    {
        PluginDescriptionFile pdfFile = getDescription();
        getLogger().info((new StringBuilder(String.valueOf(pdfFile.getName()))).append("Disabled !").toString());
        List names = new ArrayList();
        Game b;
        for(Iterator iterator = games.iterator(); iterator.hasNext(); b.save(getConfig()))
        {
            b = (Game)iterator.next();
            names.add(b.getName());
            b.stop();
        }

        getConfig().set("games", names);
        games.clear();
        saveConfig();
    }

    public void onEnable()
    {
        PluginDescriptionFile pdfFile = getDescription();
        getLogger().info((new StringBuilder(String.valueOf(pdfFile.getName()))).append(" has been enabled!").toString());
        loadConfig();
        cci = Bukkit.createInventory(null, 9, "Choose Pencil Color");
        GameListener bListener = new GameListener(this);
        SignListener sListener = new SignListener(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(bListener, this);
        pm.registerEvents(sListener, this);
    }

    public String getRandomWord()
    {
        int i = words.size();
        Random r = new Random();
        return (String)words.get(r.nextInt(i));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[])
    {
        if(cmd.getName().equals("dmt"))
            if(sender instanceof Player)
            {
                Player player = (Player)sender;
                if(args.length > 0)
                {
                    if(args[0].equals("setcanvaspos1") && player.hasPermission("dmt.admin"))
                    {
                        ChatUtil.send(player, (new StringBuilder()).append(ChatColor.GREEN).append("Position 1 has been set!").toString());
                        player.setMetadata("bmtp1", new FixedMetadataValue(this, LocationUtil.LocationToString(player.getLocation())));
                        return true;
                    }
                    if(args[0].equals("setcanvaspos2") && player.hasPermission("dmt.admin"))
                    {
                        ChatUtil.send(player, (new StringBuilder()).append(ChatColor.GREEN).append("Position 2 has been set!").toString());
                        player.setMetadata("bmtp2", new FixedMetadataValue(this, LocationUtil.LocationToString(player.getLocation())));
                        return true;
                    }
                    if(args[0].equals("setspawn") && player.hasPermission("dmt.admin"))
                    {
                        ChatUtil.send(player, (new StringBuilder()).append(ChatColor.GREEN).append("The spawnpoint has been set!").toString());
                        player.setMetadata("bmtspec", new FixedMetadataValue(this, LocationUtil.LocationToString(player.getLocation())));
                        return true;
                    }
                    if(args[0].equals("setbuilderspawn") && player.hasPermission("dmt.admin"))
                    {
                        ChatUtil.send(player, (new StringBuilder()).append(ChatColor.GREEN).append("The builder spawnpoint has been set!").toString());
                        player.setMetadata("bmtbspawn", new FixedMetadataValue(this, LocationUtil.LocationToString(player.getLocation())));
                        return true;
                    }
                    if(args[0].equals("create") && player.hasPermission("dmt.admin"))
                    {
                        if(args.length > 1)
                        {
                            if(player.hasMetadata("bmtp1") && player.hasMetadata("bmtp2") && player.hasMetadata("bmtspec"))
                            {
                                if(getGameByName(args[1]) != null)
                                {
                                    ChatUtil.send(player, "A game with this name already exist!");
                                } else
                                {
                                    Location loc1 = LocationUtil.StringToLoc(((MetadataValue)player.getMetadata("bmtp1").get(0)).asString());
                                    Location loc2 = LocationUtil.StringToLoc(((MetadataValue)player.getMetadata("bmtp2").get(0)).asString());
                                    Location spawn = LocationUtil.StringToLoc(((MetadataValue)player.getMetadata("bmtspec").get(0)).asString());
                                    Location bspawn = LocationUtil.StringToLoc(((MetadataValue)player.getMetadata("bmtbspawn").get(0)).asString());
                                    Game b = new Game(new CuboidZone(loc1.getBlock(), loc2.getBlock()), spawn, bspawn, args[1], this);
                                    if(args.length > 2 && isInteger(args[2]))
                                        b.setMaxPlayers(Integer.parseInt(args[2]));
                                    games.add(b);
                                    ChatUtil.send(player, (new StringBuilder()).append(ChatColor.GREEN).append("The game ").append(args[1]).append(" has been created!").toString());
                                }
                            } else
                            {
                                ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("The game could not be created!").toString());
                            }
                        } else
                        {
                            ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("You must specify a game name!").toString());
                        }
                    } else
                    if(args[0].equals("delete") && player.hasPermission("dmt.admin"))
                    {
                        if(args.length > 1)
                        {
                            if(getGameByName(args[1]) != null)
                            {
                                getGameByName(args[1]).remove(getConfig());
                                games.remove(getGameByName(args[1]));
                                ChatUtil.send(player, (new StringBuilder()).append(ChatColor.GREEN).append("The game ").append(args[1]).append(" has been deleted!").toString());
                            } else
                            {
                                ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("The game ").append(args[1]).append(" does not exist!").toString());
                            }
                        } else
                        {
                            ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("You must specify a game name!").toString());
                        }
                    } else
                    if(args[0].equals("setmax") && player.hasPermission("dmt.admin"))
                    {
                        if(args.length > 1)
                        {
                            if(getGameByName(args[1]) != null)
                            {
                                if(args.length > 2)
                                {
                                    getGameByName(args[1]).stop();
                                    getGameByName(args[1]).setMaxPlayers(Integer.valueOf(args[2]).intValue());
                                    ChatUtil.send(player, (new StringBuilder()).append(ChatColor.GREEN).append("The game ").append(args[1]).append(" has now has a max player limit of ").append(args[2]).append("!").toString());
                                }
                            } else
                            {
                                ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("The game ").append(args[1]).append(" does not exist!").toString());
                            }
                        } else
                        {
                            ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("You must specify a game name!").toString());
                        }
                    } else
                    if(args[0].equals("setmin") && player.hasPermission("dmt.admin"))
                    {
                        if(args.length > 1)
                        {
                            if(getGameByName(args[1]) != null)
                            {
                                if(args.length > 2)
                                {
                                    getGameByName(args[1]).stop();
                                    getGameByName(args[1]).setMinPlayers(Integer.valueOf(args[2]).intValue());
                                    ChatUtil.send(player, (new StringBuilder()).append(ChatColor.GREEN).append("The game ").append(args[1]).append(" must now have ").append(args[2]).append(" players to start!").toString());
                                }
                            } else
                            {
                                ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("The game ").append(args[1]).append(" does not exist!").toString());
                            }
                        } else
                        {
                            ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("You must specify a game name!").toString());
                        }
                    } else
                    if(args[0].equals("reload") && player.hasPermission("dmt.admin"))
                    {
                        stopAllGames();
                        reloadConfig();
                        loadConfig();
                    } else
                    if(args[0].equals("join") && player.hasPermission("dmt.default"))
                    {
                        if(player.hasMetadata("inbmt"))
                            ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("You are already in a game!").toString());
                        else
                        if(args.length > 1)
                        {
                            if(getGameByName(args[1]) != null)
                                getGameByName(args[1]).join(player);
                            else
                                ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("The game ").append(args[1]).append(" does not exist!").toString());
                        } else
                        {
                            ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("You must specify a game name!").toString());
                        }
                    } else
                    if(args[0].equals("leave") && player.hasPermission("dmt.default"))
                    {
                        if(player.hasMetadata("inbmt"))
                            getGameByName(((MetadataValue)player.getMetadata("inbmt").get(0)).asString()).leave(player);
                        else
                            ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("You are not currently in a game!").toString());
                    } else
                    if(args[0].equals("help"))
                    {
                        if(player.hasPermission("dmt.admin"))
                        {
                            player.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/dmt setcanvaspos1 ").append(ChatColor.GRAY).append("Set the first point to your current position").toString());
                            player.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/dmt setcanvaspos2 ").append(ChatColor.GRAY).append("Set the second point to your current position").toString());
                            player.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/dmt setspawn ").append(ChatColor.GRAY).append("Set the spawn point to your current position").toString());
                            player.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/dmt create <gameName> ").append(ChatColor.GRAY).append("Create a new game with the specified name").toString());
                            player.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/dmt delete <gameName> ").append(ChatColor.GRAY).append("Remove the game with the specified name").toString());
                            player.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/dmt setmin <gameName> ").append(ChatColor.GRAY).append("Change min players to start the game of the specified name").toString());
                            player.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/dmt setmax <gameName> ").append(ChatColor.GRAY).append("Change player limit of the game wih the specified name").toString());
                            player.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/dmt reload").append(ChatColor.GRAY).append("Reload the plugin").toString());
                        }
                        if(player.hasPermission("dmt.default"))
                        {
                            player.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/dmt join <gameName> ").append(ChatColor.GRAY).append("Join the game with the specified name").toString());
                            player.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/dmt leave ").append(ChatColor.GRAY).append("Leave your current game").toString());
                        }
                    } else
                    {
                        ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("Unknown command!").toString());
                    }
                } else
                {
                    ChatUtil.send(player, (new StringBuilder()).append(ChatColor.RED).append("Unknown command!").toString());
                }
            } else
            {
                sender.sendMessage("Sorry this command can only be run by a player");
            }
        return false;
    }

    private void stopAllGames()
    {
        Game b;
        for(Iterator iterator = games.iterator(); iterator.hasNext(); b.stop())
            b = (Game)iterator.next();

    }

    private void loadConfig()
    {
        getConfig().options().copyDefaults(true);
        getConfig().options().header("Default Draw My Thing Config!");
        if(!getConfig().contains("words"))
            getConfig().set("words", DEFAULT_WORDS);
        getConfig().addDefault("tool-sound", Boolean.valueOf(true));
        saveConfig();
        this.games.clear();
        this.words.clear();
        if(getConfig().getList("games") != null && getConfig().getList("games").size() > 0 && (getConfig().getList("games").get(0) instanceof String))
        {
            List games = getConfig().getList("games");
            String s;
            for(Iterator iterator = games.iterator(); iterator.hasNext(); this.games.add(Game.load(getConfig(), s, this)))
                s = (String)iterator.next();

        }
        if(getConfig().getList("words") != null && getConfig().getList("words").size() > 0 && (getConfig().getList("words").get(0) instanceof String))
        {
            List words = getConfig().getList("words");
            for(Iterator iterator1 = words.iterator(); iterator1.hasNext();)
            {
                String s = (String)iterator1.next();
                if(s != null)
                    this.words.add(s);
            }

        }
    }

    public static boolean isInteger(String s)
    {
        try
        {
            Integer.parseInt(s);
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    public Game getGameByName(String name)
    {
        Game result = null;
        for(Iterator iterator = games.iterator(); iterator.hasNext();)
        {
            Game b = (Game)iterator.next();
            if(b.getName().equals(name))
                result = b;
        }

        return result;
    }

    public void addCCIItems()
    {
        cci.addItem(new ItemStack[] {
            white
        });
        cci.addItem(new ItemStack[] {
            black
        });
        cci.addItem(new ItemStack[] {
            red
        });
        cci.addItem(new ItemStack[] {
            orange
        });
        cci.addItem(new ItemStack[] {
            yellow
        });
        cci.addItem(new ItemStack[] {
            green
        });
        cci.addItem(new ItemStack[] {
            blue
        });
        cci.addItem(new ItemStack[] {
            purple
        });
        cci.addItem(new ItemStack[] {
            brown
        });
    }

    private List games;
    public Logger logger;
    public DrawMyThing plugin;
    public Inventory cci;
    public static final List DEFAULT_WORDS = new ArrayList(Arrays.asList(new String[] {
        "motorbike", "noodle", "sea", "ocean", "leash", "pumpkin", "pyramid", "rainbow", "witch", "house", 
        "santa", "flower", "pants", "donut", "snake", "snail", "yawn", "stamp", "horse", "apple", 
        "goblin", "boat", "computer", "river", "cupcake", "football", "chocolate", "frog", "night", "creeper", 
        "ship", "bow", "tree", "smile", "watch"
    }));
    private List words;
    private ItemStack white;
    private ItemStack black;
    private ItemStack red;
    private ItemStack orange;
    private ItemStack yellow;
    private ItemStack green;
    private ItemStack blue;
    private ItemStack purple;
    private ItemStack brown;

}