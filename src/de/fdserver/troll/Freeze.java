package de.fdserver.troll;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class Freeze implements Listener {

    private static final ArrayList<Player> allowFlight = new ArrayList<>();
    private static final HashMap<Player, Location> freezed = new HashMap<>();

    public static ArrayList<Player> getFreezed() {
        return new ArrayList<>(freezed.keySet());
    }

    public static boolean isFreezed(Player p) {
        return freezed.containsKey(p);
    }

    public static void freeze(Player p) {
        if (isFreezed(p)) {
            p.setFlySpeed(0.1f);
            p.setFlying(false);
            p.setAllowFlight(allowFlight.contains(p));
            allowFlight.remove(p);
            freezed.remove(p);
        } else {
            p.setFlySpeed(0f);
            if (p.getAllowFlight())
                allowFlight.add(p);
            p.setAllowFlight(true);
            p.setFlying(true);
            p.teleport(p.getLocation().add(0, 0.1, 0));
            freezed.put(p, p.getLocation());
        }
    }

    @EventHandler
    public void onToggleFly(PlayerToggleFlightEvent e) {
        if (isFreezed(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (isFreezed(e.getPlayer()) && e.getTo() != null) {
            Location loc = freezed.get(e.getPlayer());
            if (loc.getX() != e.getTo().getX() || loc.getY() != e.getTo().getY() || loc.getZ() != e.getTo().getZ()) {
                e.setCancelled(true);
                e.getPlayer().teleport(loc);
            }
        }
    }

}
