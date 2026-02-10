package com.example.tushpItems.commands;

import com.example.tushpItems.TushpItems;
import com.example.tushpItems.managers.TrapSkin;
import com.example.tushpItems.managers.TrapSchematic;
import com.example.tushpItems.utils.ItemCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiveItemCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭту команду может использовать только игрок!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("§c§lОшибка! §fИспользуйте: /tushpitem <тип>");
            player.sendMessage("");
            player.sendMessage("§e§lДоступные команды:");
            player.sendMessage("§6Скины для разблокировки:");
            player.sendMessage("§e  • murzik §f- Скин Мурзик");
            player.sendMessage("§e  • hell §f- Скин Адская");
            player.sendMessage("§e  • ice §f- Скин Ледяная");
            player.sendMessage("§e  • abandoned §f- Скин Заброшенная");
            player.sendMessage("");
            player.sendMessage("§6Предметы:");
            player.sendMessage("§e  • stun §f- Предмет Стан (30x30x30, 30 сек кд)");
            player.sendMessage("§e  • trap §f- Светящийся мешок (установка трапки)");
            player.sendMessage("§e  • selector §f- Селектор трапок (для создания схемы)");
            player.sendMessage("");
            player.sendMessage("§8Совет: §7Сначала создай схему селектором,");
            player.sendMessage("§7       выбери скин в /customtraps,");
            player.sendMessage("§7       затем используй светящийся мешок!");
            return true;
        }

        ItemStack item = null;
        String itemName = "";

        switch (args[0].toLowerCase()) {
            case "murzik":
            case "мурзик":
                item = ItemCreator.createSkinPaper(TrapSkin.MURZIK);
                itemName = "Скин Мурзик";
                break;

            case "hell":
            case "адская":
            case "ад":
                item = ItemCreator.createSkinPaper(TrapSkin.HELL);
                itemName = "Скин Адская";
                break;

            case "ice":
            case "ледяная":
            case "лед":
                item = ItemCreator.createSkinPaper(TrapSkin.ICE);
                itemName = "Скин Ледяная";
                break;

            case "abandoned":
            case "заброшенная":
                item = ItemCreator.createSkinPaper(TrapSkin.ABANDONED);
                itemName = "Скин Заброшенная";
                break;

            case "stun":
            case "стан":
                item = ItemCreator.createStunItem();
                itemName = "Стан";
                player.sendMessage("");
                player.sendMessage("§e§lКАК ИСПОЛЬЗОВАТЬ:");
                player.sendMessage("§fПКМ - создать зону стана 30x30x30");
                player.sendMessage("§fБлокирует жемчуг и хорус на 20 секунд");
                player.sendMessage("§fКулдаун: 30 секунд");
                player.sendMessage("");
                break;

            case "trap":
            case "трапка":
                item = ItemCreator.createTrapItem();
                itemName = "Трапка (Светящийся мешок)";
                player.sendMessage("");
                player.sendMessage("§e§lКАК ИСПОЛЬЗОВАТЬ:");
                player.sendMessage("§f1. Создай схему селектором §7(/tushpitem selector)");
                player.sendMessage("§f2. Выбери скин §7(/customtraps)");
                player.sendMessage("§f3. ПКМ светящимся мешком - трапка появится вокруг тебя!");
                player.sendMessage("");
                player.sendMessage("§6⚡ Длительность: §e25 секунд");
                player.sendMessage("§c⏳ Кулдаун: §e35 секунд");
                player.sendMessage("");
                break;

            case "selector":
            case "селектор":
                item = ItemCreator.createTrapSelector();
                itemName = "Селектор трапок";
                player.sendMessage("");
                player.sendMessage("§e§lИНСТРУКЦИЯ:");
                player.sendMessage("§f1. Выберите скин: §e/customtraps");
                player.sendMessage("§f2. Постройте трапку из блоков");
                player.sendMessage("§f3. ПКМ по первому углу (позиция 1)");
                player.sendMessage("§f4. Shift+ПКМ по противоположному углу (позиция 2)");
                player.sendMessage("§f5. ПКМ по воздуху - сохранить схему");
                player.sendMessage("§f6. §e/tushpitem trap §f- получить светящийся мешок");
                player.sendMessage("");
                break;

            case "placer":
            case "установщик":
                // УСТАРЕВШИЙ - оставлен для совместимости
                if (!TushpItems.getInstance().getTrapManager().hasSavedSchematic(player)) {
                    player.sendMessage("§c§lОшибка! §fУ вас нет сохраненной схемы!");
                    player.sendMessage("§fСначала создайте схему с помощью селектора:");
                    player.sendMessage("§e  1. /tushpitem selector");
                    player.sendMessage("§e  2. Выделите построенную трапку");
                    player.sendMessage("§e  3. Сохраните схему (ПКМ по воздуху)");
                    player.sendMessage("");
                    player.sendMessage("§8Подсказка: §7Используйте §e/tushpitem trap §7вместо placer!");
                    return true;
                }

                TrapSchematic schematic = TushpItems.getInstance().getTrapManager().getSavedSchematic(player);
                item = ItemCreator.createTrapPlacer(schematic);
                itemName = "Трапка [" + schematic.getSkin().getDisplayName() + "]";

                player.sendMessage("");
                player.sendMessage("§e§lВНИМАНИЕ: §fЭто устаревший предмет!");
                player.sendMessage("§fРекомендуем использовать: §e/tushpitem trap");
                player.sendMessage("");
                player.sendMessage("§a§l✓ Установщик создан!");
                player.sendMessage("§fСкин: §e" + schematic.getSkin().getDisplayName());
                player.sendMessage("§fБлоков: §e" + schematic.getBlockCount());
                player.sendMessage("");
                player.sendMessage("§e§lКак использовать:");
                player.sendMessage("§f▶ ПКМ по блоку - поставить трапку в мир");
                player.sendMessage("");
                break;

            default:
                player.sendMessage("§c§lОшибка! §fНеизвестный тип предмета: " + args[0]);
                player.sendMessage("§fИспользуйте /tushpitem для списка доступных предметов");
                return true;
        }

        if (item != null) {
            player.getInventory().addItem(item);
            player.sendMessage("§a§l✓ Получено! §f" + itemName);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("murzik", "hell", "ice", "abandoned", "stun", "trap", "selector", "placer");
        }
        return new ArrayList<>();
    }
}