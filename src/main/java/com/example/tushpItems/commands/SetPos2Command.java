package com.example.tushpItems.commands;

import com.example.tushpItems.managers.TrapManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPos2Command implements CommandExecutor {

    private final TrapManager trapManager;

    public SetPos2Command(TrapManager trapManager) {
        this.trapManager = trapManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭту команду может использовать только игрок!");
            return true;
        }

        Player player = (Player) sender;
        trapManager.setPos2(player, player.getLocation());
        player.sendMessage("§a§lПозиция 2 установлена! §f(" +
                player.getLocation().getBlockX() + ", " +
                player.getLocation().getBlockY() + ", " +
                player.getLocation().getBlockZ() + ")");

        return true;
    }
}