package com.example.tushpItems.listeners;

import com.example.tushpItems.TushpItems;
import com.example.tushpItems.managers.CooldownManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.Material;

public class TrapListener implements Listener {

    private final TushpItems plugin;

    public TrapListener(TushpItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Проверяем, не зашел ли игрок в трапку
        plugin.getTrapManager().checkTrapActivation(player, event.getTo());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        CooldownManager cdManager = plugin.getCooldownManager();

        // Проверка телепортации эндер-жемчугом
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (cdManager.hasPearlCooldown(player)) {
                event.setCancelled(true);
                // УБРАЛИ СПАМ СООБЩЕНИЯ - игрок уже знает что в трапке
                return;
            }

            // Проверка на стан зону
            if (plugin.getStunManager().isInStunZone(player.getLocation())) {
                event.setCancelled(true);
                // Только при стане показываем сообщение (это отдельная механика)
                int remaining = cdManager.getPearlRemainingSeconds(player);
                if (remaining > 0) {
                    player.sendMessage("§c§lЗона стана! §fЖемчуг заблокирован!");
                }
                return;
            }
        }
    }

    // Новый метод для обработки хоруса
    @EventHandler
    public void onChorusFruitEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        // Проверяем, что игрок ест хорус
        if (event.getItem().getType() != Material.CHORUS_FRUIT) {
            return;
        }

        CooldownManager cdManager = plugin.getCooldownManager();

        // Проверка кулдауна хоруса
        if (cdManager.hasChorusCooldown(player)) {
            event.setCancelled(true);
            // УБРАЛИ СПАМ СООБЩЕНИЯ
            return;
        }

        // Проверка на стан зону
        if (plugin.getStunManager().isInStunZone(player.getLocation())) {
            event.setCancelled(true);
            // Только при стане показываем сообщение
            int remaining = cdManager.getChorusRemainingSeconds(player);
            if (remaining > 0) {
                player.sendMessage("§c§lЗона стана! §fХорус заблокирован!");
            }
            return;
        }
    }
}
