package com.example.tushpItems.listeners;

import com.example.tushpItems.TushpItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class RegionProtectionListener implements Listener {

    private final TushpItems plugin;

    public RegionProtectionListener(TushpItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getRegionManager().canBreak(event.getBlock().getLocation(), player)) {
            event.setCancelled(true);
            player.sendMessage("§c§lЗапрещено! §fВы не можете ломать блоки в чужой трапке!");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getRegionManager().canBuild(event.getBlock().getLocation(), player)) {
            event.setCancelled(true);
            player.sendMessage("§c§lЗапрещено! §fВы не можете строить в чужой трапке!");
        }
    }
}