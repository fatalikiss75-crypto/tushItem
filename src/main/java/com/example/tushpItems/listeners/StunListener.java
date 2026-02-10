package com.example.tushpItems.listeners;

import com.example.tushpItems.TushpItems;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StunListener implements Listener {

    private final TushpItems plugin;
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public StunListener(TushpItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.WHITE_DYE) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        String displayName = meta.getDisplayName();

        // Проверяем, является ли это предметом стана
        if (displayName.contains("Стан") || isStunItem(meta)) {
            event.setCancelled(true);

            // Проверка кулдауна
            if (plugin.getCooldownManager().hasStunCooldown(player)) {
                int remaining = plugin.getCooldownManager().getStunRemainingSeconds(player);
                player.sendMessage("§c§lКулдаун! §fСтан можно использовать через " + remaining + " сек.");
                return;
            }

            // Создание зоны стана
            plugin.getStunManager().createStunZone(player, player.getLocation());
            plugin.getCooldownManager().setStunCooldown(player, 30);

            // Удаление предмета
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }
    }

    private boolean isStunItem(ItemMeta meta) {
        // Дополнительная проверка на специальные маркеры
        if (meta.hasLore()) {
            for (String lore : meta.getLore()) {
                if (lore.contains("Стан") || lore.contains("зону")) {
                    return true;
                }
            }
        }
        return meta.hasCustomModelData() && meta.getCustomModelData() == 9999; // Специальный ID для стана
    }

    private String colorize(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.COLOR_CHAR + "x"
                    + ChatColor.COLOR_CHAR + group.charAt(0) + ChatColor.COLOR_CHAR + group.charAt(1)
                    + ChatColor.COLOR_CHAR + group.charAt(2) + ChatColor.COLOR_CHAR + group.charAt(3)
                    + ChatColor.COLOR_CHAR + group.charAt(4) + ChatColor.COLOR_CHAR + group.charAt(5));
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }
}