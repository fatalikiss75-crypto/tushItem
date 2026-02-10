package com.example.tushpItems;

import com.example.tushpItems.commands.*;
import com.example.tushpItems.listeners.*;
import com.example.tushpItems.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class TushpItems extends JavaPlugin {

    private static TushpItems instance;
    private TrapManager trapManager;
    private CooldownManager cooldownManager;
    private StunManager stunManager;
    private RegionManager regionManager;

    @Override
    public void onEnable() {
        instance = this;

        // Инициализация менеджеров
        trapManager = new TrapManager(this);
        cooldownManager = new CooldownManager();
        stunManager = new StunManager(this);
        regionManager = new RegionManager();

        // Регистрация команд с проверкой на null
        try {
            getCommand("tushpitempos1").setExecutor(new SetPos1Command(trapManager));
            getCommand("tushpitempos2").setExecutor(new SetPos2Command(trapManager));
            getCommand("tushpitemsave").setExecutor(new SaveTrapCommand(trapManager));
            getCommand("customtraps").setExecutor(new CustomTrapsCommand(trapManager));

            // Команда для выдачи предметов
            GiveItemCommand giveItemCmd = new GiveItemCommand();
            getCommand("tushpitem").setExecutor(giveItemCmd);
            getCommand("tushpitem").setTabCompleter(giveItemCmd);

            getLogger().info("Все команды успешно зарегистрированы!");
        } catch (NullPointerException e) {
            getLogger().severe("ОШИБКА: Не удалось зарегистрировать команды! Проверьте plugin.yml");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Регистрация событий
        getServer().getPluginManager().registerEvents(new TrapListener(this), this);
        getServer().getPluginManager().registerEvents(new StunListener(this), this);
        getServer().getPluginManager().registerEvents(new TrapMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new SkinUnlockListener(this), this);
        getServer().getPluginManager().registerEvents(new RegionProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new TrapPlacerListener(this), this);

        // НОВЫЙ ЛИСТЕНЕР - для светящегося чернильного мешка
        getServer().getPluginManager().registerEvents(new TrapItemListener(this), this);

        getLogger().info("╔════════════════════════════════════╗");
        getLogger().info("║   TushpItems успешно запущен!     ║");
        getLogger().info("║                                    ║");
        getLogger().info("║ ✓ Стан: 30x30x30, кд 30 сек       ║");
        getLogger().info("║ ✓ Трапка: авто, 25 сек, кд 35 сек ║");
        getLogger().info("╚════════════════════════════════════╝");
    }

    @Override
    public void onDisable() {
        if (stunManager != null) {
            stunManager.cleanup();
        }
        if (trapManager != null) {
            trapManager.cleanup();
        }
        getLogger().info("TushpItems плагин выключен!");
    }

    public static TushpItems getInstance() {
        return instance;
    }

    public TrapManager getTrapManager() {
        return trapManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public StunManager getStunManager() {
        return stunManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }
}