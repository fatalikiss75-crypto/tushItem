package com.example.tushpItems.managers;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class TrapManager {

    private final com.example.tushpItems.TushpItems plugin;
    private final Map<UUID, Location> pos1 = new HashMap<>();
    private final Map<UUID, Location> pos2 = new HashMap<>();
    private final Map<UUID, TrapSkin> selectedSkins = new HashMap<>();
    private final Map<UUID, Set<TrapSkin>> unlockedSkins = new HashMap<>();

    // Сохраненные схемы трапок
    private final Map<UUID, TrapSchematic> savedSchematics = new HashMap<>();

    // Установленные физические трапки в мире
    private final Map<Location, PlacedTrap> placedTraps = new HashMap<>();

    // Отслеживание игроков, которые уже активировали трапку (чтобы не спамить)
    private final Map<UUID, Set<Location>> activatedTraps = new HashMap<>();

    public TrapManager(com.example.tushpItems.TushpItems plugin) {
        this.plugin = plugin;
    }

    public void setPos1(Player player, Location loc) {
        pos1.put(player.getUniqueId(), loc.clone());
    }

    public void setPos2(Player player, Location loc) {
        pos2.put(player.getUniqueId(), loc.clone());
    }

    public Location getPos1(Player player) {
        return pos1.get(player.getUniqueId());
    }

    public Location getPos2(Player player) {
        return pos2.get(player.getUniqueId());
    }

    public void setSelectedSkin(Player player, TrapSkin skin) {
        selectedSkins.put(player.getUniqueId(), skin);
    }

    public TrapSkin getSelectedSkin(Player player) {
        return selectedSkins.getOrDefault(player.getUniqueId(), TrapSkin.DEFAULT);
    }

    public boolean hasSkinUnlocked(Player player, TrapSkin skin) {
        // DEFAULT и NORMAL разблокированы по умолчанию
        if (skin == TrapSkin.DEFAULT || skin == TrapSkin.NORMAL) return true;
        Set<TrapSkin> skins = unlockedSkins.get(player.getUniqueId());
        return skins != null && skins.contains(skin);
    }

    public void unlockSkin(Player player, TrapSkin skin) {
        Set<TrapSkin> skins = unlockedSkins.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        skins.add(skin);
    }

    /**
     * Сохраняет регион как схему трапки
     */
    public void saveTrapSchematic(Player player) {
        Location loc1 = getPos1(player);
        Location loc2 = getPos2(player);

        if (loc1 == null || loc2 == null) {
            player.sendMessage("§c§lОшибка! §fУстановите обе позиции сначала!");
            return;
        }

        if (!loc1.getWorld().equals(loc2.getWorld())) {
            player.sendMessage("§c§lОшибка! §fПозиции должны быть в одном мире!");
            return;
        }

        TrapSkin skin = getSelectedSkin(player);
        TrapSchematic schematic = new TrapSchematic(skin, skin.getDisplayName());

        // Определяем границы региона
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        // Сохраняем все блоки в схему
        int blockCount = 0;
        Location origin = new Location(loc1.getWorld(), minX, minY, minZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);

                    // Пропускаем воздух
                    if (block.getType() == Material.AIR) {
                        continue;
                    }

                    int relX = x - minX;
                    int relY = y - minY;
                    int relZ = z - minZ;

                    schematic.addBlock(relX, relY, relZ, block.getType(), block.getBlockData());
                    blockCount++;
                }
            }
        }

        if (blockCount == 0) {
            player.sendMessage("§c§lОшибка! §fВ выбранном регионе нет блоков!");
            return;
        }

        // Сохраняем схему для игрока
        savedSchematics.put(player.getUniqueId(), schematic);

        player.sendMessage("§a§l✓ Схема сохранена! §f(" + blockCount + " блоков)");
        player.sendMessage("§fСкин: §e" + skin.getDisplayName());
        player.sendMessage("§fТеперь используйте §e/tushpitem trap §fчтобы получить предмет!");
    }

    /**
     * Проверяет есть ли сохраненная схема
     */
    public boolean hasSavedSchematic(Player player) {
        return savedSchematics.containsKey(player.getUniqueId());
    }

    /**
     * Получает сохраненную схему
     */
    public TrapSchematic getSavedSchematic(Player player) {
        return savedSchematics.get(player.getUniqueId());
    }

    /**
     * НОВЫЙ МЕТОД - Устанавливает трапку вокруг игрока (игрок в центре наверху)
     */
    public void placeAutomaticTrap(Player player) {
        TrapSchematic schematic;
        TrapSkin skin = getSelectedSkin(player);

        // Если у игрока нет сохраненной схемы - сообщаем об ошибке
        if (!hasSavedSchematic(player)) {
            player.sendMessage("§c§lОшибка! §fСначала создайте схему трапки!");
            player.sendMessage("§e1. §f/tushpitem selector - получить селектор");
            player.sendMessage("§e2. §fПостройте трапку и выделите её");
            player.sendMessage("§e3. §fСохраните схему (ПКМ по воздуху)");
            return;
        }

        schematic = getSavedSchematic(player);

        // Обновляем схему с новым скином если он изменился
        if (schematic.getSkin() != skin) {
            // Создаем новую схему с обновленным скином но теми же блоками
            TrapSchematic newSchematic = new TrapSchematic(skin, skin.getDisplayName());
            for (TrapSchematic.BlockInfo block : schematic.getBlocks()) {
                newSchematic.addBlock(block.x, block.y, block.z, block.material, block.blockData);
            }
            schematic = newSchematic;
            savedSchematics.put(player.getUniqueId(), newSchematic);
        }

        // Вычисляем размеры трапки и находим min/max координаты
        int maxX = 0, maxY = 0, maxZ = 0;
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        for (TrapSchematic.BlockInfo block : schematic.getBlocks()) {
            maxX = Math.max(maxX, block.x);
            maxY = Math.max(maxY, block.y);
            maxZ = Math.max(maxZ, block.z);
            minX = Math.min(minX, block.x);
            minY = Math.min(minY, block.y);
            minZ = Math.min(minZ, block.z);
        }

        // Размеры трапки
        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;
        int sizeZ = maxZ - minZ + 1;

        // Позиция игрока - центр по X и Z
        Location playerLoc = player.getLocation();

        // Вычисляем origin так, чтобы игрок был в центре по X и Z
        // origin = позиция_игрока - смещение_до_центра_трапки - минимальная_координата
        double centerOffsetX = (sizeX - 1) / 2.0;
        double centerOffsetZ = (sizeZ - 1) / 2.0;

        Location origin = playerLoc.clone();
        origin.setX(playerLoc.getBlockX() - minX - centerOffsetX);
        origin.setY(playerLoc.getBlockY() - minY - sizeY + 1); // Трапка строится под игроком
        origin.setZ(playerLoc.getBlockZ() - minZ - centerOffsetZ);

        // Спавним блоки
        schematic.paste(origin);

        // Регистрируем трапку
        PlacedTrap trap = new PlacedTrap(origin, schematic, player.getUniqueId(), maxX, maxY, maxZ, minX, minY, minZ, sizeX, sizeY, sizeZ);
        placedTraps.put(origin, trap);

        // Запускаем эффекты
        startTrapEffects(trap, origin);

        // Регистрируем защиту региона
        Location pos1 = origin.clone();
        Location pos2 = origin.clone().add(maxX, maxY, maxZ);
        plugin.getRegionManager().registerTrapRegion(pos1, pos2, player.getUniqueId());

        player.sendMessage("§a§l✓ Трапка установлена вокруг вас!");
        player.sendMessage("§fСкин: §e" + skin.getDisplayName());
        player.sendMessage("§fДлительность: §e25 секунд");

        // АВТОМАТИЧЕСКОЕ УДАЛЕНИЕ ЧЕРЕЗ 25 СЕКУНД
        new BukkitRunnable() {
            @Override
            public void run() {
                removeTrap(origin, trap);
                player.sendMessage("§e§lТрапка исчезла!");
            }
        }.runTaskLater(plugin, 500L); // 25 секунд
    }

    /**
     * УСТАРЕВШИЙ МЕТОД - оставлен для совместимости
     */
    public void placeTrap(Player player, Location location, TrapSchematic schematic) {
        // Спавним блоки
        schematic.paste(location);

        // Регистрируем трапку
        int maxX = 0, maxY = 0, maxZ = 0;
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        for (TrapSchematic.BlockInfo block : schematic.getBlocks()) {
            maxX = Math.max(maxX, block.x);
            maxY = Math.max(maxY, block.y);
            maxZ = Math.max(maxZ, block.z);
            minX = Math.min(minX, block.x);
            minY = Math.min(minY, block.y);
            minZ = Math.min(minZ, block.z);
        }
        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;
        int sizeZ = maxZ - minZ + 1;

        PlacedTrap trap = new PlacedTrap(location, schematic, player.getUniqueId(), maxX, maxY, maxZ, minX, minY, minZ, sizeX, sizeY, sizeZ);
        placedTraps.put(location, trap);

        // Запускаем эффекты
        startTrapEffects(trap, location);

        // Автоудаление через 25 секунд
        new BukkitRunnable() {
            @Override
            public void run() {
                removeTrap(location, trap);
            }
        }.runTaskLater(plugin, 500L);

        // Регистрируем защиту региона
        Location pos1 = location.clone();
        Location pos2 = location.clone();

        // Вычисляем размер трапки
        pos2.add(maxX, maxY, maxZ);

        plugin.getRegionManager().registerTrapRegion(pos1, pos2, player.getUniqueId());

        player.sendMessage("§a§l✓ Трапка установлена!");
        player.sendMessage("§fСкин: §e" + schematic.getSkin().getDisplayName());
    }

    /**
     * Проверка активации трапки при движении
     */
    public void checkTrapActivation(Player player, Location location) {
        for (Map.Entry<Location, PlacedTrap> entry : placedTraps.entrySet()) {
            PlacedTrap trap = entry.getValue();
            Location trapOrigin = entry.getKey();

            if (isNearTrap(location, trapOrigin, trap)) {
                // Проверяем, не активировал ли игрок уже эту трапку
                Set<Location> activated = activatedTraps.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());

                if (!activated.contains(trapOrigin)) {
                    activateTrap(player, trap, trapOrigin);
                    activated.add(trapOrigin);

                    // Удаляем через 3 секунды, чтобы игрок мог снова попасть в трапку
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            activated.remove(trapOrigin);
                        }
                    }.runTaskLater(plugin, 60L); // 3 секунды
                }
                break;
            }
        }
    }

    private boolean isNearTrap(Location playerLoc, Location trapOrigin, PlacedTrap trap) {
        // Проверяем находится ли игрок внутри области трапки (с небольшим отступом)
        double minX = trapOrigin.getX() + trap.getMinX() - 1;
        double maxXBound = trapOrigin.getX() + trap.getMaxX() + 1;
        double minY = trapOrigin.getY() + trap.getMinY() - 1;
        double maxYBound = trapOrigin.getY() + trap.getMaxY() + 2; // +2 чтобы игрок наверху тоже попадал
        double minZ = trapOrigin.getZ() + trap.getMinZ() - 1;
        double maxZBound = trapOrigin.getZ() + trap.getMaxZ() + 1;

        return playerLoc.getX() >= minX && playerLoc.getX() <= maxXBound &&
                playerLoc.getY() >= minY && playerLoc.getY() <= maxYBound &&
                playerLoc.getZ() >= minZ && playerLoc.getZ() <= maxZBound;
    }

    private void activateTrap(Player player, PlacedTrap trap, Location trapOrigin) {
        CooldownManager cdManager = plugin.getCooldownManager();
        TrapSkin skin = trap.getSchematic().getSkin();

        // Телепортируем игрока в ЦЕНТР трапки по X/Z и на ВЕРХ по Y
        double centerX = trapOrigin.getX() + trap.getMinX() + (trap.getSizeX() - 1) / 2.0 + 0.5;
        double centerY = trapOrigin.getY() + trap.getMinY() + trap.getSizeY();
        double centerZ = trapOrigin.getZ() + trap.getMinZ() + (trap.getSizeZ() - 1) / 2.0 + 0.5;

        Location centerLoc = new Location(trapOrigin.getWorld(), centerX, centerY, centerZ);
        centerLoc.setYaw(player.getLocation().getYaw());
        centerLoc.setPitch(player.getLocation().getPitch());

        player.teleport(centerLoc);

        // Применяем эффекты в зависимости от скина
        String actionBarMessage = "";

        switch (skin) {
            case HELL:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                cdManager.setChorusCooldown(player, 5);
                cdManager.setPearlCooldown(player, 15);
                actionBarMessage = "§c§l🔥 Адская трапка!";
                break;

            case ICE:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 9));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                cdManager.setChorusCooldown(player, 5);
                cdManager.setPearlCooldown(player, 15);
                actionBarMessage = "§b§l❄ Ледяная трапка!";
                break;

            case ABANDONED:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                cdManager.setChorusCooldown(player, 5);
                cdManager.setPearlCooldown(player, 15);
                actionBarMessage = "§7§l🕸 Заброшенная трапка!";
                break;

            case MURZIK:
                if (player.getUniqueId().equals(trap.getOwner())) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0));
                    actionBarMessage = "§6§l⭐ Трапка Мурзик! §fОгнестойкость!";
                } else {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0));
                    actionBarMessage = "§6§l⭐ Трапка Мурзик! §fИссушение!";
                }
                cdManager.setChorusCooldown(player, 5);
                cdManager.setPearlCooldown(player, 15);
                break;

            case DEFAULT:
            case NORMAL:
                cdManager.setChorusCooldown(player, 5);
                cdManager.setPearlCooldown(player, 15);
                actionBarMessage = "§f§l⚡ Трапка активирована!";
                break;
        }

        // Показываем сообщение в action bar (над хотбаром)
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBarMessage));
    }

    private void startTrapEffects(PlacedTrap trap, Location center) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!placedTraps.containsValue(trap)) {
                    cancel();
                    return;
                }

                switch (trap.getSchematic().getSkin()) {
                    case HELL:
                        spawnHellEffects(center);
                        break;
                    case ICE:
                        spawnIceEffects(center);
                        break;
                    case ABANDONED:
                        spawnAbandonedEffects(center);
                        break;
                    case MURZIK:
                        spawnMurzikEffects(center);
                        break;
                    case DEFAULT:
                    case NORMAL:
                        // Минимальные эффекты для обычной трапки
                        center.getWorld().spawnParticle(Particle.CRIT, center.clone().add(0.5, 1, 0.5), 1, 0.3, 0.3, 0.3, 0.01);
                        break;
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private void spawnHellEffects(Location loc) {
        loc.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(0.5, 1, 0.5), 3, 0.5, 0.5, 0.5, 0.02);
        loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc.clone().add(0.5, 1, 0.5), 2, 0.5, 0.5, 0.5, 0.02);
    }

    private void spawnIceEffects(Location loc) {
        loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc.clone().add(0.5, 1, 0.5), 5, 0.5, 0.5, 0.5, 0.02);
        loc.getWorld().spawnParticle(Particle.GLOW, loc.clone().add(0.5, 1.2, 0.5), 3, 0.5, 0.5, 0.5, 0.02);
    }

    private void spawnAbandonedEffects(Location loc) {
        loc.getWorld().spawnParticle(Particle.ASH, loc.clone().add(0.5, 1, 0.5), 3, 0.5, 0.5, 0.5, 0.02);
        loc.getWorld().spawnParticle(Particle.DUST, loc.clone().add(0.5, 1, 0.5), 2, 0.5, 0.5, 0.5);
    }

    private void spawnMurzikEffects(Location loc) {
        loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc.clone().add(0.5, 1, 0.5), 2, 0.5, 0.5, 0.5, 0.02);
        loc.getWorld().spawnParticle(Particle.HEART, loc.clone().add(0.5, 1.3, 0.5), 1, 0.3, 0.3, 0.3, 0);
    }

    private void removeTrap(Location origin, PlacedTrap trap) {
        // Удаляем блоки трапки
        for (TrapSchematic.BlockInfo block : trap.getSchematic().getBlocks()) {
            Location blockLoc = origin.clone().add(block.x, block.y, block.z);
            blockLoc.getBlock().setType(Material.AIR);
        }

        // Удаляем из списка активных трапок
        placedTraps.remove(origin);

        // Удаляем защиту региона
        Location pos1 = origin.clone();
        Location pos2 = origin.clone().add(trap.maxX, trap.maxY, trap.maxZ);
        plugin.getRegionManager().unregisterTrapRegion(pos1, pos2);

        // Очищаем активации для этой трапки
        for (Set<Location> locs : activatedTraps.values()) {
            locs.remove(origin);
        }
    }

    public void cleanup() {
        placedTraps.clear();
        savedSchematics.clear();
        pos1.clear();
        pos2.clear();
        activatedTraps.clear();
    }

    public static class PlacedTrap {
        private final Location origin;
        private final TrapSchematic schematic;
        private final UUID owner;
        private final int maxX, maxY, maxZ;
        private final int minX, minY, minZ;
        private final int sizeX, sizeY, sizeZ;

        public PlacedTrap(Location origin, TrapSchematic schematic, UUID owner, int maxX, int maxY, int maxZ,
                          int minX, int minY, int minZ, int sizeX, int sizeY, int sizeZ) {
            this.origin = origin;
            this.schematic = schematic;
            this.owner = owner;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.sizeZ = sizeZ;
        }

        public Location getOrigin() { return origin; }
        public TrapSchematic getSchematic() { return schematic; }
        public UUID getOwner() { return owner; }
        public int getMaxX() { return maxX; }
        public int getMaxY() { return maxY; }
        public int getMaxZ() { return maxZ; }
        public int getSizeX() { return sizeX; }
        public int getSizeY() { return sizeY; }
        public int getSizeZ() { return sizeZ; }
        public int getMinX() { return minX; }
        public int getMinY() { return minY; }
        public int getMinZ() { return minZ; }
    }
}