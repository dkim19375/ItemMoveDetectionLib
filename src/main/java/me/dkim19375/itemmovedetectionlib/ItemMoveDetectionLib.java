package me.dkim19375.itemmovedetectionlib;

import me.dkim19375.itemmovedetectionlib.listener.ItemMoveListeners;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemMoveDetectionLib {
    private static Plugin plugin = null;
    public static void register() {
        if (!isRegistered()) {
            plugin = JavaPlugin.getProvidingPlugin(ItemMoveDetectionLib.class);
            registerListeners();
        }
    }

    public static boolean isRegistered() {
        return plugin != null;
    }

    private static void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ItemMoveListeners(), plugin);
    }
}
