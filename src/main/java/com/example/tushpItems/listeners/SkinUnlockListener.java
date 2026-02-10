package com.example.tushpItems.listeners;

import com.example.tushpItems.TushpItems;
import com.example.tushpItems.managers.TrapSkin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkinUnlockListener implements Listener {

    private final TushpItems plugin;
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public SkinUnlockListener(TushpItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Проверка: ПКМ + шифт
        if (!player.isSneaking()) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (item == null || item.getType() != Material.PAPER) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || !meta.hasLore()) {
            return;
        }

        String displayName = meta.getDisplayName();

        // Проверяем, является ли это скин-бумажкой
        TrapSkin skinToUnlock = identifySkinPaper(displayName, meta);

        if (skinToUnlock == null) {
            return;
        }

        event.setCancelled(true);

        // Проверка уже разблокирован ли скин
        if (plugin.getTrapManager().hasSkinUnlocked(player, skinToUnlock)) {
            player.sendMessage("§e§lВнимание! §fЭтот скин уже разблокирован!");
            return;
        }

        // Разблокировка скина
        plugin.getTrapManager().unlockSkin(player, skinToUnlock);

        // Эффекты
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);

        player.sendMessage("§a§l✔ Разблокировано! §fСкин трапки: " + skinToUnlock.getDisplayName());
        player.sendMessage("§fИспользуйте §e/customtraps §fдля выбора скина!");

        // Удаление предмета
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }

    private TrapSkin identifySkinPaper(String displayName, ItemMeta meta) {
        String strippedName = ChatColor.stripColor(displayName).toLowerCase();

        // Проверяем название
        if (strippedName.contains("мурзик") || strippedName.contains("myrzik")) {
            // Дополнительная проверка лора для безопасности
            if (hasValidLore(meta, "мурзик")) {
                return TrapSkin.MURZIK;
            }
        }

        if (strippedName.contains("адск")) {
            if (hasValidLore(meta, "адск")) {
                return TrapSkin.HELL;
            }
        }

        if (strippedName.contains("ледян") || strippedName.contains("лед")) {
            if (hasValidLore(meta, "ледян")) {
                return TrapSkin.ICE;
            }
        }

        if (strippedName.contains("заброшенн")) {
            if (hasValidLore(meta, "заброшенн")) {
                return TrapSkin.ABANDONED;
            }
        }

        return null;
    }

    private boolean hasValidLore(ItemMeta meta, String keyword) {
        if (!meta.hasLore()) {
            return false;
        }

        for (String line : meta.getLore()) {
            String stripped = ChatColor.stripColor(line).toLowerCase();
            if (stripped.contains(keyword) && stripped.contains("дается")) {
                return true;
            }
        }

        return false;
    }
}