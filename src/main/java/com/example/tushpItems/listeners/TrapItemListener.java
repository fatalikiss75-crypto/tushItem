package com.example.tushpItems.listeners;

import com.example.tushpItems.TushpItems;
import com.example.tushpItems.utils.ItemCreator;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Листенер для нового предмета - светящегося чернильного мешка
 * Устанавливает трапку вокруг игрока с кулдауном 35 секунд
 */
public class TrapItemListener implements Listener {

    private final TushpItems plugin;

    public TrapItemListener(TushpItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Проверка только главной руки
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Проверяем что это светящийся чернильный мешок (предмет трапки)
        if (!ItemCreator.isTrapItem(item)) {
            return;
        }

        // Только ПКМ
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        // Проверка кулдауна (35 секунд)
        if (plugin.getCooldownManager().hasTrapCooldown(player)) {
            int remaining = plugin.getCooldownManager().getTrapRemainingSeconds(player);
            player.sendMessage("§c§lКулдаун! §fТрапку можно установить через §e" + remaining + " §fсек.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            return;
        }

        // УСТАНОВКА ТРАПКИ ВОКРУГ ИГРОКА
        // Если нет схемы - будет использована дефолтная 3x3
        plugin.getTrapManager().placeAutomaticTrap(player);

        // Устанавливаем кулдаун 35 секунд
        plugin.getCooldownManager().setTrapCooldown(player, 35);

        // Эффекты
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation().add(0, 1, 0), 5, 1, 1, 1);
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0, 1, 0), 20, 1, 1, 1, 0.1);

        // Удаляем предмет из инвентаря
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }
}