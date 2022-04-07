package de.fdserver.troll;

import de.myfdweb.minecraft.api.CoreAPI;
import de.myfdweb.minecraft.api.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class NoTroll implements CommandExecutor, Listener {

    public static final String PREFIX = CoreAPI.getPrefix("NoTroll");
    public static final String PERMISSION = "fdserver.notroll";
    private static boolean disableTroll;
    private static Player operator;

    public static boolean isDisableTroll() {
        return disableTroll;
    }

    public static Player getOperator() {
        return operator;
    }

    private static void set(Player p, boolean disableTroll, boolean disableNoTroll) {
        if (operator != null && p != null && !Troll.canOverride(p, operator) && !p.equals(operator)) {
            p.sendMessage("§cDu darfst den Trollschutz nicht verändern, da er von einem Höhergestellten gesetzt wurde.");
            return;
        }
        for (Player p2 : Bukkit.getOnlinePlayers())
            if (operator == null)
                if (disableTroll) {
                    if(Troll.getInstance().isTrolling(p2))
                        Troll.getInstance().troll(p2);
                    if(Freeze.isFreezed(p))
                        Freeze.freeze(p);
                    p2.sendMessage(PREFIX + p.getDisplayName() + " §ahat den Trollschutz aktiviert.");
                }else
                    p2.sendMessage(PREFIX + p.getDisplayName() + " §ahat den Trollschutz verboten.");
            else if (p == null)
                p2.sendMessage(PREFIX + "§cDer Trollschutz von " + operator.getDisplayName() + " §cwurde aufgehoben.");
            else
                p2.sendMessage(PREFIX + p.getDisplayName() + " §chat den Trollschutz von " + operator.getDisplayName() + " §caufgehoben.");
        NoTroll.operator = !disableTroll && !disableNoTroll ? null : p;
        NoTroll.disableTroll = disableTroll;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        open(p);
        return true;
    }

    private static void open(Player p) {
        if (!p.hasPermission(PERMISSION)) {
            p.sendMessage(PREFIX + "§cDu darfst NoTroll nicht verwenden!");
            return;
        }
        Inventory inv = Bukkit.createInventory(null, operator == null ? 27 : 36, "§b§lNoTroll");
        if (operator == null)
            inv.setItem(13, new ItemBuilder(Material.GREEN_DYE).setDisplayName("§aTrollen aktiviert").setLore("§eLinks-Klick zum Deaktivieren von Troll", "§eRechts-Klick zum Deaktivieren von NoTroll").build());
        else {
            inv.setItem(13, new ItemBuilder(Material.PLAYER_HEAD).setDisplayName(operator.getDisplayName()).setSkullOwner(operator.getName()).build());
            inv.setItem(22, new ItemBuilder(disableTroll ? Material.GREEN_DYE : Material.RED_DYE).setDisplayName(disableTroll ? "§cTrollen deaktiviert" : "§aTrollen aktiviert").setLore(disableTroll ? "§eRechts-Klick zum Aktivieren von Troll" : "§eLinks-Klick zum Aktivieren von NoTroll").build());
        }
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§b§lNoTroll")) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() != null && e.getCurrentItem().getType().name().contains("DYE")) {
                if (e.getClick().isLeftClick()) {
                    if (operator == null)
                        set(p, true, false);
                    else if (!disableTroll)
                        set(p, false, false);
                } else if (e.getClick().isRightClick()) {
                    if (operator == null)
                        set(p, false, true);
                    else if (disableTroll)
                        set(p, false, false);
                }
                open(p);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (e.getPlayer().equals(operator))
            set(null, false, false);
    }
}
