package com.example.tushpItems.commands;

import com.example.tushpItems.managers.TrapManager;
import com.example.tushpItems.managers.TrapSkin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomTrapsCommand implements CommandExecutor {

    private final TrapManager trapManager;
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public CustomTrapsCommand(TrapManager trapManager) {
        this.trapManager = trapManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭту команду может использовать только игрок!");
            return true;
        }

        Player player = (Player) sender;
        openTrapMenu(player);

        return true;
    }

    private void openTrapMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, colorize("&#FFD700✦ Скины трапок ✦"));

        // Расставляем скины
        menu.setItem(10, createTrapItem(player, TrapSkin.DEFAULT, Material.GRASS_BLOCK));
        menu.setItem(11, createTrapItem(player, TrapSkin.NORMAL, Material.PAPER));
        menu.setItem(13, createTrapItem(player, TrapSkin.MURZIK, Material.GOLDEN_APPLE));
        menu.setItem(15, createTrapItem(player, TrapSkin.HELL, Material.BLAZE_POWDER));
        menu.setItem(16, createTrapItem(player, TrapSkin.ICE, Material.ICE));
        menu.setItem(19, createTrapItem(player, TrapSkin.ABANDONED, Material.COBWEB));

        // Заполняем пустые слоты стеклом
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        for (int i = 0; i < 27; i++) {
            if (menu.getItem(i) == null) {
                menu.setItem(i, glass);
            }
        }

        player.openInventory(menu);
    }

    private ItemStack createTrapItem(Player player, TrapSkin skin, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        boolean unlocked = trapManager.hasSkinUnlocked(player, skin);
        boolean selected = trapManager.getSelectedSkin(player) == skin;

        String displayName = colorize(skin.getColor() + "Трапка: " + skin.getDisplayName());
        if (selected) {
            displayName = colorize("&#00FF00✔ " + displayName);
        }

        meta.setDisplayName(displayName);

        if (unlocked) {
            if (skin == TrapSkin.DEFAULT) {
                meta.setLore(Arrays.asList(
                        "",
                        colorize("&#00FF00✔ Разблокировано по умолчанию"),
                        colorize("&#AAAAAA▶ Использует вашу схему"),
                        selected ? colorize("&#FFD700▶ Выбрано") : colorize("&#AAAAAA▶ Нажмите, чтобы выбрать")
                ));
            } else {
                meta.setLore(Arrays.asList(
                        "",
                        colorize("&#00FF00✔ Разблокировано"),
                        selected ? colorize("&#FFD700▶ Выбрано") : colorize("&#AAAAAA▶ Нажмите, чтобы выбрать")
                ));
            }
        } else {
            meta.setLore(Arrays.asList(
                    "",
                    colorize("&#FF5555✖ Заблокировано"),
                    colorize("&#AAAAAA▶ Используйте скин-бумажку")
            ));
            meta.setCustomModelData(1); // Помечаем как заблокированное
        }

        item.setItemMeta(meta);
        return item;
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