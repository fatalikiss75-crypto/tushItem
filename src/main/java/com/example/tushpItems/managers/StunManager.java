package com.example.tushpItems.managers;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class StunManager {

    private final com.example.tushpItems.TushpItems plugin;
    private final Map<Location, StunZone> activeStuns = new HashMap<>();

    public StunManager(com.example.tushpItems.TushpItems plugin) {
        this.plugin = plugin;
    }

    public void createStunZone(Player player, Location center) {
        StunZone zone = new StunZone(center, player.getUniqueId());
        activeStuns.put(center, zone);

        player.sendMessage("§e§lСтан активирован! §fЗона 30x30x30 создана!");

        // Визуальные эффекты границ зоны
        showStunBorders(zone);

        // Автоматическое удаление через 20 секунд
        new BukkitRunnable() {
            @Override
            public void run() {
                activeStuns.remove(center);
                player.sendMessage("§e§lСтан деактивирован!");
            }
        }.runTaskLater(plugin, 400L); // 20 секунд
    }

    private void showStunBorders(StunZone zone) {
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!activeStuns.containsValue(zone) || ticks >= 400) {
                    cancel();
                    return;
                }

                Location center = zone.getCenter();
                double radius = 15.0; // ИЗМЕНЕНО: половина от 30 (было 7.5)

                // Рисуем границы куба частицами
                for (int i = 0; i < 8; i++) {
                    double angle = Math.PI * 2 * i / 8;
                    double x = center.getX() + Math.cos(angle) * radius;
                    double z = center.getZ() + Math.sin(angle) * radius;

                    for (double y = -radius; y <= radius; y += 1.0) {
                        Location particleLoc = new Location(center.getWorld(), x, center.getY() + y, z);
                        center.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
                    }
                }

                ticks += 20;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public boolean isInStunZone(Location location) {
        for (StunZone zone : activeStuns.values()) {
            if (isInZone(location, zone)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInZone(Location loc, StunZone zone) {
        Location center = zone.getCenter();
        double radius = 15.0; // ИЗМЕНЕНО: половина от 30 (было 7.5)

        return Math.abs(loc.getX() - center.getX()) <= radius &&
                Math.abs(loc.getY() - center.getY()) <= radius &&
                Math.abs(loc.getZ() - center.getZ()) <= radius;
    }

    public void cleanup() {
        activeStuns.clear();
    }

    public static class StunZone {
        private final Location center;
        private final UUID owner;

        public StunZone(Location center, UUID owner) {
            this.center = center;
            this.owner = owner;
        }

        public Location getCenter() {
            return center;
        }

        public UUID getOwner() {
            return owner;
        }
    }
}