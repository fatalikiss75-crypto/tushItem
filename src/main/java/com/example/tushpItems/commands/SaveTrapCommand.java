package com.example.tushpItems.commands;

import com.example.tushpItems.managers.TrapManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveTrapCommand implements CommandExecutor {

    private final TrapManager trapManager;

    public SaveTrapCommand(TrapManager trapManager) {
        this.trapManager = trapManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭту команду может использовать только игрок!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("§c§lОшибка! §fИспользуйте: /tushpItemSave <тип>");
            player.sendMessage("§fДоступные типы: normal, hell, ice, abandoned, myrzik");
            return true;
        }

        trapManager.saveTrapSchematic(player);

        return true;
    }
}