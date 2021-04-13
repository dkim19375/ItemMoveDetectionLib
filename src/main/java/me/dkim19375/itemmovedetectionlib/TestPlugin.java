package me.dkim19375.itemmovedetectionlib;

import me.dkim19375.itemmovedetectionlib.event.InventoryItemTransferEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        ItemMoveDetectionLib.register();
        // Testing purposes
        // Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

/*    @EventHandler
    private void onTransfer(InventoryItemTransferEvent event) {
        if (event.getType().isOther()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Don't steal!!");
            event.setCancelled(true);
        }
    }*/
}
