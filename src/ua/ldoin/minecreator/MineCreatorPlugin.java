package ua.ldoin.minecreator;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import ua.ldoin.minecreator.mine.*;
import ua.ldoin.minecreator.mine.types.*;

public class MineCreatorPlugin extends JavaPlugin {

    public static MineCreatorPlugin plugin;

    static {

        ConfigurationSerialization.registerClass(CuboidMine.class);
        ConfigurationSerialization.registerClass(OverlayMine.class);

    }

    public void onEnable() {

        plugin = this;

        saveDefaultConfig();

        MineManager.init();

        getCommand("minecreator").setExecutor(new Commands());

        Bukkit.getPluginManager().registerEvents(new Listeners(), this);

    }

    public static void sendMessage(CommandSender sender, String message) {

        sender.sendMessage(replace(message, null));

    }

    public static String getMessageConfig(String message, Mine mine) {

        return replace(plugin.getConfig().getString("messages." + message), mine);

    }

    public static String replace(String string, Mine mine) {

        if (string == null)
            return "";

        if (mine == null)
            return string.replace("&", "§");

        return string.replace("&", "§")
                .replace("%mine%", mine.getName())
                .replace("%to_reset%", String.valueOf(mine.getToReset()))
                .replace("%total_blocks_in_mine%", String.valueOf(mine.getTotalBlocksInMine()))
                .replace("%blocks_mined%", String.valueOf(mine.getMinedBlocks()));

    }

    public void onDisable() {

        MineManager.save();

    }
}