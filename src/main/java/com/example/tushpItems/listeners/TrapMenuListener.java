package com.example.tushpItems.listeners;

import com.example.tushpItems.TushpItems;
import com.example.tushpItems.managers.TrapSkin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TrapMenuListener implements Listener {

    private final TushpItems plugin;

    public TrapMenuListener(TushpItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        String title = event.getView().getTitle();
        if (!title.contains("Скины трапок")) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            return;
        }

        TrapSkin selectedSkin = null;

        // Определяем какой скин был выбран по слоту
        switch (event.getSlot()) {
            case 10:
                selectedSkin = TrapSkin.DEFAULT;
                break;
            case 11:
                selectedSkin = TrapSkin.NORMAL;
                break;
            case 13:
                selectedSkin = TrapSkin.MURZIK;
                break;
            case 15:
                selectedSkin = TrapSkin.HELL;
                break;
            case 16:
                selectedSkin = TrapSkin.ICE;
                break;
            case 19:
                selectedSkin = TrapSkin.ABANDONED;
                break;
        }

        if (selectedSkin == null) {
            return;
        }

        // Проверка разблокировки
        if (!plugin.getTrapManager().hasSkinUnlocked(player, selectedSkin)) {
            player.sendMessage("§c§lЗаблокировано! §fИспользуйте скин-бумажку для разблокировки!");
            player.closeInventory();
            return;
        }

        // Выбор скина
        plugin.getTrapManager().setSelectedSkin(player, selectedSkin);
        player.sendMessage("§a§lВыбрано! §fСкин трапки: " + selectedSkin.getDisplayName());
        player.closeInventory();
    }
}