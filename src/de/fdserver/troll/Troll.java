package de.fdserver.troll;

import de.myfdweb.minecraft.api.CoreAPI;
import de.myfdweb.minecraft.api.items.ItemBuilder;
import de.myfdweb.minecraft.api.items.Pages;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.platform.PlayerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.logging.Level;

public class Troll extends JavaPlugin implements CommandExecutor, Listener, TabExecutor {

    public static final String LINE = "§6-----------------------------------------------------";
    public static final String PREFIX = CoreAPI.getPrefix("Troll");
    public static final String PERMISSION = "fdserver.troll";
    private static Troll instance;
    private static boolean active = true;
    private final ArrayList<TrollFunction> functions = new ArrayList<>();
    private final ArrayList<Player> trolling = new ArrayList<>();

    public static Troll getInstance() {
        return instance;
    }

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean active) {
        Troll.active = active;
    }

    @Override
    public void onEnable() {
        instance = this;
        getCommand("troll").setExecutor(this);
        getCommand("notroll").setExecutor(new NoTroll());
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new NoTroll(), this);
        Bukkit.getPluginManager().registerEvents(new Vanish(), this);
        Bukkit.getPluginManager().registerEvents(new Freeze(), this);
        functions.clear();
        addFunction(new TrollFunction("fly", 0, "", "Lässt dich fliegen", new ItemBuilder(Material.FEATHER).setDisplayName("§aFlugmodus").build()) {

            @Override
            public void run(Player executor, String[] args) {
                executor.setAllowFlight(!executor.getAllowFlight());
                executor.sendMessage(Troll.PREFIX + "Du kannst nun " + (executor.getAllowFlight() ? "" : "nicht mehr ") + "fliegen!");
            }
        });
        addFunction(new TrollFunction("vanish", 0, "", "Macht dich unsichtbar", new ItemBuilder(Material.GLASS_BOTTLE).setDisplayName("§aUnsichtbarkeit").build()) {

            @Override
            public void run(Player executor, String[] args) {
                Vanish.vanish(executor);
            }
        });
        addFunction(new PlayerTrollFunction("letfly", "Lässt einen Spieler fliegen", new ItemBuilder(Material.FEATHER).setDisplayName("§aFlugmodus für Spieler").build()) {

            @Override
            public void run(Player executor, Player target) {
                target.setAllowFlight(!target.getAllowFlight());
                target.sendMessage(Troll.PREFIX + "Du kannst nun " + (target.getAllowFlight() ? "" : "nicht mehr ") + "fliegen!");
                executor.sendMessage(Troll.PREFIX + target.getDisplayName() + " §akann nun " + (target.getAllowFlight() ? "" : "nicht mehr ") + "fliegen!");
            }
        });
        addFunction(new PlayerTrollFunction("letvanish", "Macht einen Spieler unsichtbar", new ItemBuilder(Material.GLASS_BOTTLE).setDisplayName("§aUnsichtbarkeit für Spieler").build()) {

            @Override
            public void run(Player executor, Player target) {
                Vanish.vanish(target);
                executor.sendMessage(Troll.PREFIX + target.getDisplayName() + " §aist nun " + (Vanish.isVanished(target) ? "unsichtbar." : "sichtbar."));
            }
        });
        addFunction(new PlayerTrollFunction("tp", "Teleportiert dich zu einem Spieler", new ItemBuilder(Material.ENDER_PEARL).setDisplayName("§aTeleportieren").build()) {

            @Override
            public void run(Player executor, Player target) {
                executor.teleport(target);
                executor.sendMessage(Troll.PREFIX + "Du wurdest zu " + target.getDisplayName() + " §ateleportiert.");
            }
        });
        addFunction(new PlayerTrollFunction("push", "Gibt einem Spieler KnockBack", new ItemBuilder(Material.STICK).setDisplayName("§aKnockback geben").build()) {

            @Override
            public void run(Player executor, Player target) {
                target.setVelocity(target.getLocation().getDirection().multiply(2));
                executor.sendMessage(Troll.PREFIX + target.getDisplayName() + " §ahat Knockback erhalten.");
            }
        });
        addFunction(new PlayerTrollFunction("freeze", "Macht einen Spieler bewegungsunfähig", new ItemBuilder(Material.ICE).setDisplayName("§aEinfrieren").build()) {

            @Override
            public void run(Player executor, Player target) {
                Freeze.freeze(target);
                executor.sendMessage(Troll.PREFIX + target.getDisplayName() + " §akann sich nun " + (Freeze.isFreezed(target) ? "nicht mehr" : "wieder") + " bewegen.");
            }
        });
        addFunction(new PlayerTrollFunction("inv", "Zeigt einem Spieler ein leeres Inventar", new ItemBuilder(Material.CHEST).setDisplayName("§aInventar zeigen").build()) {

            @Override
            public void run(Player executor, Player target) {
                target.openInventory(Bukkit.createInventory(null, 27));
                executor.sendMessage(Troll.PREFIX + target.getDisplayName() + " §asieht nun ein leeres Inventar.");
            }
        });
        addFunction(new PlayerTrollFunction("speed", "Gibt einem Spieler für 5 Sekunden maximalen Speed", new ItemBuilder(Material.FEATHER).setDisplayName("§aSpeed geben").build()) {

            @Override
            public void run(Player executor, Player target) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 255, true, false));
                executor.sendMessage(Troll.PREFIX + target.getDisplayName() + " §ahat nun für 5 Sekunden maximalen Speed.");
            }
        });
        addFunction(new PlayerTrollFunction("sound", "Speilt ein Geräusch bei dem Spieler ab", new ItemBuilder(Material.WITHER_SKELETON_SKULL).setDisplayName("§aSound abspielen").build()) {

            @Override
            public void run(Player executor, Player target) {
                target.playSound(target.getLocation(), Bukkit.getVersion().split("\\.")[1].equals("8") ? Sound.valueOf("WITHER_DEATH") : Sound.valueOf("ENTITY_WITHER_DEATH"), 100, 1);
                executor.sendMessage(Troll.PREFIX + target.getDisplayName() + " §ahat ein Geräusch gehört.");
            }
        });
    }

    @Override
    public void onDisable() {
        for (Player p : new ArrayList<>(trolling))
            troll(p);
        for (Player p : Freeze.getFreezed())
            Freeze.freeze(p);
    }

    public void addFunction(TrollFunction function) {
        functions.add(function);
    }

    public static boolean canOverride(Player p1, Player p2) {
        LuckPerms lp = LuckPermsProvider.get();
        PlayerAdapter<Player> pa = lp.getPlayerAdapter(Player.class);
        GroupManager gm = lp.getGroupManager();
        OptionalInt w1 = gm.getGroup(pa.getUser(p1).getPrimaryGroup()).getWeight();
        OptionalInt w2 = gm.getGroup(pa.getUser(p2).getPrimaryGroup()).getWeight();
        return (!w1.isPresent() || !w2.isPresent() || w1.getAsInt() >= w2.getAsInt()) && !p1.equals(p2);
    }

    public boolean isTrolling(Player p) {
        return trolling.contains(p);
    }

    public void troll(Player p) {
        if (isTrolling(p)) {
            if (p.getAllowFlight() && !p.getGameMode().equals(GameMode.CREATIVE) && !p.getGameMode().equals(GameMode.SPECTATOR))
                p.setAllowFlight(false);
            if (Vanish.isVanished(p))
                Vanish.vanish(p);
            p.closeInventory();
            trolling.remove(p);
            for (Player p2 : Bukkit.getOnlinePlayers())
                if (p2.hasPermission(PERMISSION) || isTrolling(p2))
                    p2.sendMessage(Troll.PREFIX + p.getName() + " kann nun nicht mehr trollen!");
            getLogger().log(Level.INFO, p.getName() + " kann nun nicht mehr trollen!");
        } else {
            if (NoTroll.isDisableTroll()) {
                p.sendMessage(Troll.PREFIX + NoTroll.getOperator().getDisplayName() + " §chat NoTroll aktiviert! Folglich darfst du nicht trollen");
                return;
            }
            trolling.add(p);
            p.setAllowFlight(true);
            for (Player p2 : Bukkit.getOnlinePlayers())
                if (p2.hasPermission(PERMISSION) || isTrolling(p2))
                    p2.sendMessage(Troll.PREFIX + p.getName() + " kann nun trollen!");
            getLogger().log(Level.INFO, p.getName() + " kann nun trollen!");
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cNur Spieler können trollen.");
            return true;
        }
        Player p = (Player) commandSender;
        if (args.length == 0) {
            if (isTrolling(p) || p.hasPermission(PERMISSION))
                troll(p);
            else
                p.sendMessage(CoreAPI.PERMISSION_ERROR);
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sendHelp(p);
                return true;
            } else if (isTrolling(p) && isActive())
                if (args[0].equalsIgnoreCase("gui")) {
                    Pages pages = new Pages("§b§lTroll Funktion auswählen");
                    for (TrollFunction function : functions)
                        if (function.getArgCount() == 0)
                            pages.addContent(new Pages.Item(function.getItem(p), (player, item) -> {
                                p.chat("/troll " + function.getCMD());
                            }));
                        else if (function instanceof PlayerTrollFunction)
                            pages.addContent(new Pages.Item(function.getItem(p), (player, item) -> {
                                pages.setCloseOnClick(false);
                                Pages pages2 = new Pages("§b§lSpieler auswählen");
                                for (Player p2 : Bukkit.getOnlinePlayers())
                                    if (canOverride(p, p2))
                                        pages2.addContent(new Pages.Item(new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("§a" + p2.getName()).setSkullOwner(p2.getName()).build(), (player1, item1) -> {
                                            p.chat("/troll " + function.getCMD() + " " + p2.getName());
                                        }));
                                if(!pages2.open(p))
                                    p.sendMessage(Troll.PREFIX + "§cEs gibt momentan niemanden den du trollen könntest.");
                            }));
                    pages.open(p);
                    return true;
                } else if (p.hasPermission("game.troll.other") && Bukkit.getPlayer(args[0]) != null) {
                    troll(Bukkit.getPlayer(args[0]));
                    return true;
                }
        }
        if (isTrolling(p) && isActive()) {
            String cmd = args[0];
            args = Arrays.copyOfRange(args, 1, args.length);
            for (TrollFunction function : functions)
                if (function.getCMD().equalsIgnoreCase(cmd))
                    if (function.getArgCount() == args.length)
                        function.run(p, args);
                    else
                        sendHelp(p);
        } else
            sendHelp(p);
        return true;
    }

    public void sendHelp(Player p) {
        p.sendMessage(LINE);
        p.sendMessage("§eTroll Commands");
        p.sendMessage("§a/troll: §bTroll Modus de-/aktivieren");
        p.sendMessage("§a/troll help: §bÖffnet diese Hilfe");
        if (isTrolling(p) && isActive()) {
            p.sendMessage("§a/troll gui: §bÖffnet das Graphical User Interface");
            for (TrollFunction function : functions)
                p.sendMessage("§a/troll " + function.getCMD() + " " + function.getArgs() + ": §b" + function.getDescription());
        }
        p.sendMessage(LINE);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command cmd, String s, String[] args) {
        ArrayList<String> completion = new ArrayList<>();
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (args.length < 2) {
                ArrayList<String> commands = new ArrayList<>();
                commands.add("help");
                if (isTrolling(p) && isActive()) {
                    commands.add("fly");
                    commands.add("vanish");
                    for (TrollFunction function : functions)
                        commands.add(function.getCMD().toLowerCase());
                }
                if (args.length == 0)
                    return commands;
                else
                    for (String c : commands)
                        if (c.startsWith(args[0].toLowerCase()))
                            completion.add(c);
            } else if (args.length == 2 && isTrolling(p) && isActive()) {
                ArrayList<String> commands = new ArrayList<>();
                commands.add("fly");
                commands.add("vanish");
                for (TrollFunction function : functions)
                    commands.add(function.getCMD().toLowerCase());
                if (commands.contains(args[0].toLowerCase()))
                    for (Player p2 : Bukkit.getOnlinePlayers())
                        if (canOverride(p, p2) && p2.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                            completion.add(p2.getName());
            }
        }
        return completion;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (isTrolling(e.getPlayer()))
            troll(e.getPlayer());
    }
}
