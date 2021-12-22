package ua.ldoin.minecreator.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

    public static String getLocation(Location location) {

        if (location == null)
            return "";

        return location.getWorld().getName() + " " + location
                .getX() + " " + location
                .getY() + " " + location
                .getZ();

    }

    public static Location getLocation(String location) {

        if (location.equals(""))
            return null;

        World world = null;

        double x = 0.0D;
        double y = 0.0D;
        double z = 0.0D;

        double yaw = 0.0D;
        double pitch = 0.0D;

        String[] _loc = location.split(" ");

        try {

            world = Bukkit.getWorld(_loc[0]);

            x = Double.parseDouble(_loc[1]);
            y = Double.parseDouble(_loc[2]);
            z = Double.parseDouble(_loc[3]);

            try {

                yaw = Double.parseDouble(_loc[4]);
                pitch = Double.parseDouble(_loc[5]);

            } catch (IndexOutOfBoundsException ex2) {

                yaw = 0.0D;
                pitch = 0.0D;

            }
        } catch (Exception ex) {

            ex.printStackTrace();

        }

        Location loc = new Location(world, x, y, z, (float)yaw, (float)pitch);
        return (loc.getWorld() != null) ? loc : null;

    }
}