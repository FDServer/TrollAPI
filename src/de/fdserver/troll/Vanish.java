package de.fdserver.troll;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class Vanish implements Listener {

    private static final ArrayList<Player> vanished = new ArrayList<>();

    public static boolean isVanished(Player p) {
        return vanished.contains(p);
    }

    public static void vanish(Player p) {
        if (isVanished(p)) {
            for (Player p2 : Bukkit.getOnlinePlayers())
                if (p != p2)
                    if (!p2.canSee(p)) {
                        p2.showPlayer(p);
                        p2.sendMessage("§7[§a+§7] " + p.getDisplayName());
                    }
            vanished.remove(p);
            p.sendMessage(Troll.PREFIX + "Du bist nun sichtbar!");
        } else {
            for (Player p2 : Bukkit.getOnlinePlayers())
                if (!Troll.canOverride(p2, p)) {
                    p2.hidePlayer(p);
                    p2.sendMessage("§7[§c-§7] " + p.getDisplayName());
                }
            vanished.add(p);
            p.sendMessage(Troll.PREFIX + "Du bist nun unsichtbar!");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        for (Player p2 : Bukkit.getOnlinePlayers())
            if (isVanished(p2) && !Troll.canOverride(p2, p))
                p.hidePlayer(p2);
    }

}
