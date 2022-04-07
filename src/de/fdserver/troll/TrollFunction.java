package de.fdserver.troll;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class TrollFunction {

    private final String cmd, args, description;
    private final int argCount;
    private final ItemStack itemStack;

    public TrollFunction(String cmd, int argCount, String args, String description, ItemStack itemStack) {
        this.cmd = cmd;
        this.argCount = argCount;
        this.args = args;
        this.description = description;
        this.itemStack = itemStack;
    }

    public String getCMD() {
        return cmd;
    }

    public int getArgCount() {
        return argCount;
    }

    public String getArgs() {
        return args;
    }

    public String getDescription() {
        return description;
    }

    public ItemStack getItem(Player p) {
        return itemStack;
    }

    public abstract void run(Player executor, String[] args);

}
