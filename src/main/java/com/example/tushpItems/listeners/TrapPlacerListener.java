package com.example.tushpItems.listeners;

import com.example.tushpItems.TushpItems;
import com.example.tushpItems.managers.TrapSchematic;
import com.example.tushpItems.utils.ItemCreator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;

public class TrapPlacerListener implements Listener {

    private final TushpItems plugin;

    public TrapPlacerListener(TushpItems plugin) {
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

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        // ========================================
        // СЕЛЕКТОР ТРАПОК (для создания схемы)
        // ========================================
        if (ItemCreator.isTrapSelector(item)) {
            event.setCancelled(true);

            // ПКМ по блоку = позиция 1
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()) {
                plugin.getTrapManager().setPos1(player, event.getClickedBlock().getLocation());
                player.sendMessage("§a§l✓ Позиция 1 установлена!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);

                // Визуальный эффект
                Location loc = event.getClickedBlock().getLocation().add(0.5, 1, 0.5);
                player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 10, 0.3, 0.3, 0.3);
                return;
            }

            // Shift + ПКМ по блоку = позиция 2
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.isSneaking()) {
                plugin.getTrapManager().setPos2(player, event.getClickedBlock().getLocation());
                player.sendMessage("§a§l✓ Позиция 2 установлена!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);

                // Визуальный эффект
                Location loc = event.getClickedBlock().getLocation().add(0.5, 1, 0.5);
                player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 10, 0.3, 0.3, 0.3);
                return;
            }

            // ПКМ по воздуху = сохранить схему
            if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                plugin.getTrapManager().saveTrapSchematic(player);
                return;
            }
        }

        // ========================================
        // ПРЕДМЕТ-ТРАПКА (для размещения)
        // ========================================
        if (ItemCreator.isTrapPlacer(item)) {
            event.setCancelled(true);

            // Только ПКМ по блоку
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                player.sendMessage("§c§lОшибка! §fКликните ПКМ по блоку чтобы поставить трапку!");
                return;
            }

            // Проверяем есть ли сохраненная схема
            if (!plugin.getTrapManager().hasSavedSchematic(player)) {
                player.sendMessage("§c§lОшибка! §fУ вас нет сохраненной схемы!");
                player.sendMessage("§fИспользуйте селектор трапок для создания схемы!");
                return;
            }

            TrapSchematic schematic = plugin.getTrapManager().getSavedSchematic(player);

            // Определяем место установки (на блоке над которым кликнули)
            Location placementLoc = event.getClickedBlock().getLocation().add(0, 1, 0);

            // Проверка что место свободно (хотя бы частично)
            // TODO: можно добавить более умную проверку коллизий

            // Устанавливаем трапку
            plugin.getTrapManager().placeTrap(player, placementLoc, schematic);

            // Эффекты
            player.playSound(placementLoc, Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);
            player.playSound(placementLoc, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
            placementLoc.getWorld().spawnParticle(Particle.EXPLOSION, placementLoc.add(0.5, 0.5, 0.5), 3, 0.5, 0.5, 0.5);

            // Удаляем предмет из инвентаря
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }
    }
}