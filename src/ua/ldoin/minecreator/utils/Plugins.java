package ua.ldoin.minecreator.utils;

import org.bukkit.Bukkit;

public class Plugins {

    public static boolean HologramList;

    public static void init() {

        HologramList = Bukkit.getServer().getPluginManager().getPlugin("HologramList") != null;

    }

    public static boolean hasHologramsPlugin() {

        return HologramList;

    }
}