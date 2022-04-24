package ua.ldoin.minecreator.mine;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ua.ldoin.minecreator.MineCreatorPlugin;
import ua.ldoin.minecreator.mine.types.CuboidMine;
import ua.ldoin.minecreator.mine.types.OverlayMine;
import ua.ldoin.minecreator.mine.types.Types;

public abstract class Mine implements ConfigurationSerializable {

    public Mine(String name, Types type, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world, boolean teleport, int resetDelay, int toReset) {

        if (world == null)
            throw new NullPointerException("The world in which the mine '" + name + "' is located has not been found!");

        this.name = name;

        this.type = type;

        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        this.world = world;

        this.teleport = teleport;

        this.resetDelay = resetDelay;
        this.toReset = toReset;

    }

    public Mine(String name, Types type, Location minPoint, Location maxPoint, World world, boolean teleport, int resetDelay, int toReset) {

        if (world == null)
            throw new NullPointerException("The world in which the mine '" + name + "' is located has not been found!");

        this.name = name;

        this.type = type;

        if (minPoint.getX() > maxPoint.getX()) {

            int x = maxPoint.getBlockX();

            maxPoint.setX(minPoint.getBlockX());
            minPoint.setX(x);

        }

        if (minPoint.getY() > maxPoint.getY()) {

            int y = maxPoint.getBlockY();

            maxPoint.setY(minPoint.getBlockY());
            minPoint.setY(y);

        }

        if (minPoint.getZ() > maxPoint.getZ()) {

            int z = maxPoint.getBlockZ();

            maxPoint.setZ(minPoint.getBlockZ());
            minPoint.setZ(z);

        }

        minX = minPoint.getBlockX();
        minY = minPoint.getBlockY();
        minZ = minPoint.getBlockZ();

        maxX = maxPoint.getBlockX();
        maxY = maxPoint.getBlockY();
        maxZ = maxPoint.getBlockZ();

        this.world = world;

        this.teleport = teleport;

        this.resetDelay = resetDelay;
        this.toReset = toReset;

    }

    private final String name;

    private final Types type;

    private int minX;
    private int minY;
    private int minZ;

    private int maxX;
    private int maxY;
    private int maxZ;

    private World world;
    private boolean teleport;

    private int resetDelay;
    private int toReset;

    public String getName() {

        return name;

    }

    public Types getType() {

        return type;

    }

    public int getMinX() {

        return minX;

    }

    public int getMinY() {

        return minY;

    }

    public int getMinZ() {

        return minZ;

    }

    public int getMaxX() {

        return maxX;

    }

    public int getMaxY() {

        return maxY;

    }

    public int getMaxZ() {

        return maxZ;

    }

    public World getWorld() {

        return world;

    }

    public boolean isTeleport() {

        return teleport;

    }

    public int getResetDelay() {

        return resetDelay;

    }

    public int getToReset() {

        return toReset;

    }

    public abstract int getMinedBlocks();

    public abstract int getTotalBlocksInMine();

    public abstract boolean isAllowToBreakBlock(Block b);

    public boolean isInside(Player p) {

        Location l = p.getLocation();

        return (l.getWorld().equals(world) && l
                .getX() >= minX && l.getX() <= maxX && l
                .getY() >= minY && l.getY() <= maxY && l
                .getZ() >= minZ && l.getZ() <= maxZ);

    }

    public void setWorld(World world) {

        this.world = world;

    }

    public void setLocation(Location minPoint, Location maxPoint) {

        if (minPoint.getX() > maxPoint.getX()) {

            int x = maxPoint.getBlockX();

            maxPoint.setX(minPoint.getBlockX());
            minPoint.setX(x);

        }

        if (minPoint.getY() > maxPoint.getY()) {

            int y = maxPoint.getBlockY();

            maxPoint.setY(minPoint.getBlockY());
            minPoint.setY(y);

        }

        if (minPoint.getZ() > maxPoint.getZ()) {

            int z = maxPoint.getBlockZ();

            maxPoint.setZ(minPoint.getBlockZ());
            minPoint.setZ(z);

        }

        minX = minPoint.getBlockX();
        minY = minPoint.getBlockY();
        minZ = minPoint.getBlockZ();

        maxX = maxPoint.getBlockX();
        maxY = maxPoint.getBlockY();
        maxZ = maxPoint.getBlockZ();

    }

    public void setTeleport(boolean teleport) {

        this.teleport = teleport;

    }

    public void setResetDelay(int resetDelay) {

        this.resetDelay = resetDelay;
        reset();

    }

    public void callFill() {

        if (type.equals(Types.CUBOID))
            ((CuboidMine) this).fill();

        if (type.equals(Types.OVERLAY))
            ((OverlayMine) this).fill();

    }

    public void reset() {

        if (resetDelay < 0)
            return;

        toReset = resetDelay;

    }

    public void run() {

        (new BukkitRunnable() {

            public void run() {

                if (toReset <= 0) {

                    reset();
                    return;

                }

                toReset--;

            }
        }).runTaskTimer(MineCreatorPlugin.plugin, 0L, 20L);
    }
}