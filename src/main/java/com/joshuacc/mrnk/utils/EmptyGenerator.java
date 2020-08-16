package com.joshuacc.mrnk.utils;

import java.util.Map;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

public class EmptyGenerator extends Generator {

    private ChunkManager chunkManager;

    public EmptyGenerator(Map<?, ?> options) {
        /* empty */
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void init(ChunkManager chunkManager, NukkitRandom nukkitRandom) {
        this.chunkManager = chunkManager;
    }

    @Override
    public void generateChunk(int chX, int chZ) {

    }

    @Override
    public void populateChunk(int i, int i1) {
        /* empty */
    }

    @Override
    public Map<String, Object> getSettings() {
        return null;
    }

    @Override
    public String getName() {
        return "emptyworld";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(128.0, 65.0, 128.0);
    }

    @Override
    public ChunkManager getChunkManager() {
        return chunkManager;
    }
}
