package ua.ldoin.minecreator.mine;

import org.bukkit.scheduler.BukkitRunnable;
import ua.Ldoin.HologramList.Hologram;
import ua.ldoin.minecreator.MineCreatorPlugin;
import ua.ldoin.minecreator.utils.Plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MineStats {

    private static final Map<Mine, Hologram> holograms_HologramList = new HashMap<>();

    public static void display(Mine mine) {

        if (mine.getStats() != null)
            if (Plugins.hasHologramsPlugin()) {
                if (Plugins.HologramList) {

                    Hologram hologram = new Hologram(mine.getName(), MineCreatorPlugin.plugin.getConfig().getStringList("mine.hologram").size(),
                            "", false, getLines(mine), mine.getStats());

                    holograms_HologramList.put(mine, hologram);

                    new BukkitRunnable() {

                        public void run() {

                            holograms_HologramList.get(mine).UpdateLines(getLines(mine));

                        }
                    }.runTaskTimer(MineCreatorPlugin.plugin, 0L, MineCreatorPlugin.plugin.getConfig().getInt("mine.statistic_cooldown"));
                }
            }
    }

    public static void breakDisplay(Mine mine) {

        if (holograms_HologramList.containsKey(mine)) {

            holograms_HologramList.get(mine).delete();
            holograms_HologramList.remove(mine);

        }
    }

    private static List<String> getLines(Mine mine) {

        List<String> lines = new ArrayList<>();

        for (String line : MineCreatorPlugin.plugin.getConfig().getStringList("mine.hologram"))
            lines.add(MineCreatorPlugin.replace(line, mine));

        return lines;

    }
}