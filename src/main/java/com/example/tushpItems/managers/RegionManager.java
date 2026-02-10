package com.example.tushpItems.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class RegionManager {

    private final Set<ProtectedRegion> protectedRegions = new HashSet<>();

    public void registerTrapRegion(Location pos1, Location pos2, UUID owner) {
        protectedRegions.add(new ProtectedRegion(pos1, pos2, owner));
    }

    public void unregisterTrapRegion(Location pos1, Location pos2) {
        protectedRegions.removeIf(region ->
                region.getPos1().equals(pos1) && region.getPos2().equals(pos2));
    }

    public boolean canBuild(Location location, Player player) {
        for (ProtectedRegion region : protectedRegions) {
            if (isInRegion(location, region)) {
                // Только владелец может строить в своей трапке
                return region.getOwner().equals(player.getUniqueId());
            }
        }
        return true; // Не в защищенном регионе
    }

    public boolean canBreak(Location location, Player player) {
        for (ProtectedRegion region : protectedRegions) {
            if (isInRegion(location, region)) {
                // Только владелец может ломать в своей трапке
                return region.getOwner().equals(player.getUniqueId());
            }
        }
        return true; // Не в защищенном регионе
    }

    private boolean isInRegion(Location loc, ProtectedRegion region) {
        Location min = region.getPos1().clone();
        Location max = region.getPos2().clone();

        double minX = Math.min(min.getX(), max.getX());
        double maxX = Math.max(min.getX(), max.getX());
        double minY = Math.min(min.getY(), max.getY());
        double maxY = Math.max(min.getY(), max.getY());
        double minZ = Math.min(min.getZ(), max.getZ());
        double maxZ = Math.max(min.getZ(), max.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    public static class ProtectedRegion {
        private final Location pos1;
        private final Location pos2;
        private final UUID owner;

        public ProtectedRegion(Location pos1, Location pos2, UUID owner) {
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.owner = owner;
        }

        public Location getPos1() {
            return pos1;
        }

        public Location getPos2() {
            return pos2;
        }

        public UUID getOwner() {
            return owner;
        }
    }
}