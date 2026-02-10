package com.example.tushpItems.utils;

import com.example.tushpItems.managers.TrapSkin;
import com.example.tushpItems.managers.TrapSchematic;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemCreator {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    // CustomModelData для разных предметов
    private static final int SKIN_MURZIK_CMD = 1001;
    private static final int SKIN_HELL_CMD = 1002;
    private static final int SKIN_ICE_CMD = 1003;
    private static final int SKIN_ABANDONED_CMD = 1004;
    private static final int STUN_ITEM_CMD = 9999;
    private static final int TRAP_SELECTOR_CMD = 7777;
    private static final int TRAP_PLACER_CMD = 8888;
    private static final int TRAP_ITEM_CMD = 6666; // Новый - светящийся чернильный мешок

    public static ItemStack createSkinPaper(TrapSkin skin) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();

        switch (skin) {
            case MURZIK:
                meta.setDisplayName(colorize("&#FFD700⭐ Скин трапки [Мурзик]"));
                meta.setLore(Arrays.asList(
                        colorize("&#888888Shift + ПКМ для разблокировки"),
                        "",
                        colorize("&#FFD700Эффекты:"),
                        colorize("&#AAAAAA  ● Владелец: Огнестойкость"),
                        colorize("&#AAAAAA  ● Враги: Иссушение"),
                        colorize("&#888888Блокировка телепорта: 5-15 сек")
                ));
                meta.setCustomModelData(SKIN_MURZIK_CMD);
                break;

            case HELL:
                meta.setDisplayName(colorize("&#FF5555🔥 Скин трапки [Адская]"));
                meta.setLore(Arrays.asList(
                        colorize("&#888888Shift + ПКМ для разблокировки"),
                        "",
                        colorize("&#FF5555Эффекты:"),
                        colorize("&#AAAAAA  ● Замедление + Слепота"),
                        colorize("&#AAAAAA  ● Огненные частицы"),
                        colorize("&#888888Блокировка телепорта: 5-15 сек")
                ));
                meta.setCustomModelData(SKIN_HELL_CMD);
                break;

            case ICE:
                meta.setDisplayName(colorize("&#55FFFF❄ Скин трапки [Ледяная]"));
                meta.setLore(Arrays.asList(
                        colorize("&#888888Shift + ПКМ для разблокировки"),
                        "",
                        colorize("&#55FFFFЭффекты:"),
                        colorize("&#AAAAAA  ● Сильное замедление + Слепота"),
                        colorize("&#AAAAAA  ● Снежные частицы"),
                        colorize("&#888888Блокировка телепорта: 5-15 сек")
                ));
                meta.setCustomModelData(SKIN_ICE_CMD);
                break;

            case ABANDONED:
                meta.setDisplayName(colorize("&#888888🕸 Скин трапки [Заброшенная]"));
                meta.setLore(Arrays.asList(
                        colorize("&#888888Shift + ПКМ для разблокировки"),
                        "",
                        colorize("&#888888Эффекты:"),
                        colorize("&#AAAAAA  ● Замедление + Слепота"),
                        colorize("&#AAAAAA  ● Пыль и пепел"),
                        colorize("&#888888Блокировка телепорта: 5-15 сек")
                ));
                meta.setCustomModelData(SKIN_ABANDONED_CMD);
                break;

            default:
                return null;
        }

        paper.setItemMeta(meta);
        return paper;
    }

    public static ItemStack createStunItem() {
        ItemStack stun = new ItemStack(Material.WHITE_DYE);
        ItemMeta meta = stun.getItemMeta();

        meta.setDisplayName(colorize("&#FFD700⚡ СТАН ⚡"));
        meta.setLore(Arrays.asList(
                colorize("&#888888┌────────────────┐"),
                colorize("&#FFD700Создает невидимую зону"),
                colorize("&#FFD700размером 30x30x30 блоков"),
                colorize("&#888888└────────────────┘"),
                colorize("&#FF5555⛔ Блокирует:"),
                colorize("&#AAAAAA  • Эндер-жемчуг"),
                colorize("&#AAAAAA  • Хорус"),
                colorize("&#888888└────────────────┘"),
                colorize("&#00FF00⏱ Длительность: 20 сек"),
                colorize("&#FF5555⏳ Кулдаун: 30 сек")
        ));
        meta.setCustomModelData(STUN_ITEM_CMD);

        stun.setItemMeta(meta);
        return stun;
    }

    /**
     * НОВЫЙ ПРЕДМЕТ - Светящийся чернильный мешок для установки трапок
     */
    public static ItemStack createTrapItem() {
        ItemStack trapItem = new ItemStack(Material.GLOW_INK_SAC);
        ItemMeta meta = trapItem.getItemMeta();

        meta.setDisplayName(colorize("&#00FFFF✦ ТРАПКА ✦"));
        meta.setLore(Arrays.asList(
                colorize("&#888888┌────────────────┐"),
                colorize("&#AAAAAAПКМ - установить трапку вокруг себя"),
                colorize("&#888888└────────────────┘"),
                "",
                colorize("&#00FF00✓ Автоматическая установка"),
                colorize("&#00FF00✓ Использует выбранный скин"),
                colorize("&#00FF00✓ Спавнится вокруг игрока"),
                "",
                colorize("&#FFD700⏱ Действует: 25 сек"),
                colorize("&#FF5555⏳ Кулдаун: 35 сек"),
                "",
                colorize("&#888888Выбери скин: /customtraps")
        ));
        meta.setCustomModelData(TRAP_ITEM_CMD);

        trapItem.setItemMeta(meta);
        return trapItem;
    }

    /**
     * Проверяет является ли предмет светящимся чернильным мешком (новая трапка)
     */
    public static boolean isTrapItem(ItemStack item) {
        if (item == null || item.getType() != Material.GLOW_INK_SAC) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData()) {
            return false;
        }

        return meta.getCustomModelData() == TRAP_ITEM_CMD;
    }

    public static ItemStack createTrapSelector() {
        ItemStack selector = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta meta = selector.getItemMeta();

        meta.setDisplayName(colorize("&#9615F9✦ Селектор трапок ✦"));
        meta.setLore(Arrays.asList(
                colorize("&#888888┌────────────────┐"),
                colorize("&#AAAAAA▶ ПКМ по блоку - позиция 1"),
                colorize("&#AAAAAA▶ Shift + ПКМ - позиция 2"),
                colorize("&#AAAAAA▶ ПКМ по воздуху - сохранить схему"),
                colorize("&#888888└────────────────┘"),
                "",
                colorize("&#FFD7001. Построй трапку из блоков"),
                colorize("&#FFD7002. Выдели её селектором"),
                colorize("&#FFD7003. Сохрани схему"),
                colorize("&#FFD7004. Используй светящийся мешок!"),
                "",
                colorize("&#00FF00Используй /customtraps для выбора скина")
        ));
        meta.setCustomModelData(TRAP_SELECTOR_CMD);
        meta.setUnbreakable(true);

        selector.setItemMeta(meta);
        return selector;
    }

    /**
     * Создает предмет-трапку с сохраненной схемой (УСТАРЕВШИЙ - оставлен для совместимости)
     */
    public static ItemStack createTrapPlacer(TrapSchematic schematic) {
        Material material;

        switch (schematic.getSkin()) {
            case HELL:
                material = Material.NETHERITE_AXE;
                break;
            case ICE:
                material = Material.DIAMOND_HOE;
                break;
            case ABANDONED:
                material = Material.IRON_SHOVEL;
                break;
            case MURZIK:
                material = Material.GOLDEN_PICKAXE;
                break;
            default:
                material = Material.STONE_PICKAXE;
                break;
        }

        ItemStack placer = new ItemStack(material);
        ItemMeta meta = placer.getItemMeta();

        String skinName = schematic.getSkin().getDisplayName();
        String skinColor = schematic.getSkin().getColor();

        meta.setDisplayName(colorize(skinColor + "⚡ ТРАПКА [" + skinName + "] ⚡"));

        List<String> lore = new ArrayList<>();
        lore.add(colorize("&#888888┌────────────────┐"));
        lore.add(colorize("&#FFFFFF▶ ПКМ по блоку - ПОСТАВИТЬ ТРАПКУ"));
        lore.add(colorize("&#888888└────────────────┘"));
        lore.add("");
        lore.add(colorize("&#FFD700Схема: " + schematic.getName()));
        lore.add(colorize("&#FFD700Блоков: " + schematic.getBlockCount()));
        lore.add("");

        switch (schematic.getSkin()) {
            case HELL:
                lore.add(colorize("&#FF5555🔥 АДСКАЯ ТРАПКА"));
                lore.add(colorize("&#AAAAAA  ● Замедление + Слепота"));
                lore.add(colorize("&#AAAAAA  ● Огненные эффекты"));
                break;
            case ICE:
                lore.add(colorize("&#55FFFF❄ ЛЕДЯНАЯ ТРАПКА"));
                lore.add(colorize("&#AAAAAA  ● Сильное замедление"));
                lore.add(colorize("&#AAAAAA  ● Заморозка врагов"));
                break;
            case ABANDONED:
                lore.add(colorize("&#888888🕸 ЗАБРОШЕННАЯ ТРАПКА"));
                lore.add(colorize("&#AAAAAA  ● Замедление + Слепота"));
                lore.add(colorize("&#AAAAAA  ● Пыльные эффекты"));
                break;
            case MURZIK:
                lore.add(colorize("&#FFD700⭐ ТРАПКА МУРЗИК"));
                lore.add(colorize("&#AAAAAA  ● Огнестойкость (свой)"));
                lore.add(colorize("&#AAAAAA  ● Иссушение (враги)"));
                break;
            default:
                lore.add(colorize("&#FFFFFF⚪ ОБЫЧНАЯ ТРАПКА"));
                break;
        }

        lore.add("");
        lore.add(colorize("&#FF5555⛔ Блокирует телепорт: 5-15 сек"));

        meta.setLore(lore);
        meta.setCustomModelData(TRAP_PLACER_CMD);
        meta.setUnbreakable(true);

        placer.setItemMeta(meta);
        return placer;
    }

    public static TrapSkin getSkinFromPaper(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData()) {
            return null;
        }

        switch (meta.getCustomModelData()) {
            case SKIN_MURZIK_CMD:
                return TrapSkin.MURZIK;
            case SKIN_HELL_CMD:
                return TrapSkin.HELL;
            case SKIN_ICE_CMD:
                return TrapSkin.ICE;
            case SKIN_ABANDONED_CMD:
                return TrapSkin.ABANDONED;
            default:
                return null;
        }
    }

    public static boolean isTrapPlacer(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.hasCustomModelData() && meta.getCustomModelData() == TRAP_PLACER_CMD;
    }

    public static boolean isTrapSelector(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.hasCustomModelData() && meta.getCustomModelData() == TRAP_SELECTOR_CMD;
    }

    private static String colorize(String message) {
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