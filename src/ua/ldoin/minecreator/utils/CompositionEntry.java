package ua.ldoin.minecreator.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CompositionEntry {

    public CompositionEntry(SerializableBlock block, double chance) {

        this.block = block;
        this.chance = chance;

    }

    private final SerializableBlock block;
    private final double chance;

    public SerializableBlock getBlock() {

        return this.block;

    }

    public double getChance() {

        return this.chance;

    }

    public static ArrayList<CompositionEntry> mapBlocks(Map<SerializableBlock, Double> compositionIn) {

        ArrayList<CompositionEntry> probabilityMap = new ArrayList<>();
        Map<SerializableBlock, Double> blocks = new HashMap<>(compositionIn);

        double max = 0.0D;

        for (Map.Entry<SerializableBlock, Double> entry : blocks.entrySet())
            max += entry.getValue();

        if (max < 100.0D) {

            blocks.put(new SerializableBlock(0), 100.0D - max);
            max = 100.0D;

        }

        double i = 0.0D;

        for (Map.Entry<SerializableBlock, Double> entry : blocks.entrySet()) {

            double v = entry.getValue() / max;
            i += v;

            probabilityMap.add(new CompositionEntry(entry.getKey(), i));

        }

        return probabilityMap;

    }
}