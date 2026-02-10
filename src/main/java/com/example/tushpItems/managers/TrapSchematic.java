package com.example.tushpItems.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class TrapSchematic {

    private final List<BlockInfo> blocks = new ArrayList<>();
    private final TrapSkin skin;
    private final String name;

    public TrapSchematic(TrapSkin skin, String name) {
        this.skin = skin;
        this.name = name;
    }

    public void addBlock(int relativeX, int relativeY, int relativeZ, Material material, BlockData blockData) {
        blocks.add(new BlockInfo(relativeX, relativeY, relativeZ, material, blockData));
    }

    public void paste(Location origin) {
        for (BlockInfo info : blocks) {
            Location loc = origin.clone().add(info.x, info.y, info.z);
            Block block = loc.getBlock();
            block.setType(info.material);
            if (info.blockData != null) {
                block.setBlockData(info.blockData);
            }
        }
    }

    public List<BlockInfo> getBlocks() {
        return new ArrayList<>(blocks);
    }

    public TrapSkin getSkin() {
        return skin;
    }

    public String getName() {
        return name;
    }

    public int getBlockCount() {
        return blocks.size();
    }

    public static class BlockInfo {
        public final int x, y, z;
        public final Material material;
        public final BlockData blockData;

        public BlockInfo(int x, int y, int z, Material material, BlockData blockData) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.material = material;
            this.blockData = blockData;
        }
    }
}