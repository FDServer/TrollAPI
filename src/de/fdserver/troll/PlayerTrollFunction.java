package de.fdserver.troll;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PlayerTrollFunction extends TrollFunction {

    public PlayerTrollFunction(String cmd, String description, ItemStack itemStack) {
        super(cmd, 1, "<Spieler>", description, itemStack);
    }

    @Override
    public void run(Player executor, String[] args) {
        Player p2 = Bukkit.getPlayer(args[0]);
        if (p2 == null)
            executor.sendMessage(Troll.PREFIX + "§cDieser Spieler ist nicht online!");
        else if (executor.equals(p2))
            executor.sendMessage(Troll.PREFIX + "§cDu darfst dich nicht selber trollen du Affe!");
        else if (!Troll.canOverride(executor, p2))
            executor.sendMessage(Troll.PREFIX + "§cDu darfst diesen Spieler nicht trollen!");
        else
            run(executor, p2);
    }

    public abstract void run(Player executor, Player target);

}
