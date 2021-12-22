package ua.ldoin.minecreator.mine;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import ua.ldoin.minecreator.MineCreatorPlugin;

public class MineManager {

    public static List<Mine> mines = new ArrayList<>();

    public static void init() {

        File[] mineFiles = (new File(MineCreatorPlugin.plugin.getDataFolder(), "Mines")).listFiles(new IsMineFile());

        if (mineFiles != null)
            for (File file : mineFiles) {

                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

                try {

                    Object o = yamlConfiguration.get("Mine");

                    if (!(o instanceof Mine))
                        Bukkit.getConsoleSender().sendMessage("Mine wasn't a Mine object! Something is off with serialization!");
                    else {

                        Mine mine = (Mine) o;
                        mines.add(mine);

                        mine.run();

                        MineStats.display(mine);

                    }
                } catch (Throwable t) {

                    Bukkit.getConsoleSender().sendMessage("Unable to load mine!");

                }
            }

        Bukkit.getConsoleSender().sendMessage("Loaded " + mines.size() + " Mines!");

    }

    public static class IsMineFile implements FilenameFilter {

        public boolean accept(File file, String s) {

            return s.contains("Mine.yml");

        }
    }

    public static Mine[] matchMines(String in) {

        List<Mine> matches = new LinkedList<>();

        for (Mine mine : mines)
            if (mine.getName().toLowerCase().contains(in.toLowerCase()))
                matches.add(mine);

        return matches.toArray(new Mine[0]);

    }

    public static Mine getMineByName(String name) {

        Mine[] mines = matchMines(name);

        if (mines.length > 1)
            throw new ArrayStoreException("More than 1 mine found!");

        return mines[0];

    }

    public static boolean mineCreated(String name) {

        return matchMines(name).length > 0;

    }

    public static void save() {

        for (Mine mine : mines) {

            File mineFile = getMineFile(mine);

            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(mineFile);
            yamlConfiguration.set("Mine", mine);

            try {

                yamlConfiguration.save(mineFile);

            } catch (IOException e) {

                e.printStackTrace();

            }
        }
    }

    public static void deleteMine(Mine mine) {

        mines.remove(mine);
        getMineFile(mine).delete();

    }

    private static File getMineFile(Mine mine) {

        return new File(new File(MineCreatorPlugin.plugin.getDataFolder(), "Mines"), mine.getName().replace(" ", "") + "Mine.yml");

    }
}