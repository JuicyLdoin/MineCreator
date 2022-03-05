package ua.ldoin.minecreator.utils;

import org.bukkit.Bukkit;

public class Plugins {

    public static boolean WorldEdit;

    public static void init() {

        WorldEdit = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") != null;

    }
}