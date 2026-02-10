package com.example.tushpItems.managers;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Long> chorusCooldowns = new HashMap<>();
    private final Map<UUID, Long> pearlCooldowns = new HashMap<>();
    private final Map<UUID, Long> stunCooldowns = new HashMap<>();
    private final Map<UUID, Long> trapCooldowns = new HashMap<>(); // НОВОЕ - кулдаун на установку трапок

    public void setChorusCooldown(Player player, int seconds) {
        chorusCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public void setPearlCooldown(Player player, int seconds) {
        pearlCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public void setStunCooldown(Player player, int seconds) {
        stunCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    // НОВЫЙ МЕТОД - установка кулдауна на трапку
    public void setTrapCooldown(Player player, int seconds) {
        trapCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public boolean hasChorusCooldown(Player player) {
        Long cooldown = chorusCooldowns.get(player.getUniqueId());
        if (cooldown == null) return false;

        if (System.currentTimeMillis() >= cooldown) {
            chorusCooldowns.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    public boolean hasPearlCooldown(Player player) {
        Long cooldown = pearlCooldowns.get(player.getUniqueId());
        if (cooldown == null) return false;

        if (System.currentTimeMillis() >= cooldown) {
            pearlCooldowns.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    public boolean hasStunCooldown(Player player) {
        Long cooldown = stunCooldowns.get(player.getUniqueId());
        if (cooldown == null) return false;

        if (System.currentTimeMillis() >= cooldown) {
            stunCooldowns.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    // НОВЫЙ МЕТОД - проверка кулдауна на трапку
    public boolean hasTrapCooldown(Player player) {
        Long cooldown = trapCooldowns.get(player.getUniqueId());
        if (cooldown == null) return false;

        if (System.currentTimeMillis() >= cooldown) {
            trapCooldowns.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    public int getChorusRemainingSeconds(Player player) {
        Long cooldown = chorusCooldowns.get(player.getUniqueId());
        if (cooldown == null) return 0;

        long remaining = (cooldown - System.currentTimeMillis()) / 1000;
        return (int) Math.max(0, remaining);
    }

    public int getPearlRemainingSeconds(Player player) {
        Long cooldown = pearlCooldowns.get(player.getUniqueId());
        if (cooldown == null) return 0;

        long remaining = (cooldown - System.currentTimeMillis()) / 1000;
        return (int) Math.max(0, remaining);
    }

    public int getStunRemainingSeconds(Player player) {
        Long cooldown = stunCooldowns.get(player.getUniqueId());
        if (cooldown == null) return 0;

        long remaining = (cooldown - System.currentTimeMillis()) / 1000;
        return (int) Math.max(0, remaining);
    }

    // НОВЫЙ МЕТОД - получение оставшегося времени кулдауна трапки
    public int getTrapRemainingSeconds(Player player) {
        Long cooldown = trapCooldowns.get(player.getUniqueId());
        if (cooldown == null) return 0;

        long remaining = (cooldown - System.currentTimeMillis()) / 1000;
        return (int) Math.max(0, remaining);
    }
}