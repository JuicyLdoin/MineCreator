package ua.ldoin.minecreator.mine.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ua.ldoin.minecreator.MineCreatorPlugin;
import ua.ldoin.minecreator.mine.Mine;
import ua.ldoin.minecreator.utils.block.SerializableBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CuboidMine extends Mine implements ConfigurationSerializable {

    public CuboidMine(String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world, int resetDelay, int toReset) {

        super(name, Types.CUBOID, minX, minY, minZ, maxX, maxY, maxZ, world, true, resetDelay, toReset);

        blocks = new HashMap<>();
        surface = new SerializableBlock("0:0");

    }

    public CuboidMine(String name, Location minPoint, Location maxPoint, World world, int resetDelay, int toReset) {

        super(name, Types.CUBOID, minPoint, maxPoint, world, true, resetDelay, toReset);

        blocks = new HashMap<>();
        surface = new SerializableBlock("0:0");

    }

    public CuboidMine(Map<String, Object> mine) {

        super((String) mine.get("name"), Types.CUBOID, (Integer) mine.get("minX"), (Integer) mine.get("minY"), (Integer) mine.get("minZ"),
                (Integer) mine.get("maxX"), (Integer) mine.get("maxY"), (Integer) mine.get("maxZ"), Bukkit.getWorld((String) mine.get("world")),
                true, (Integer) mine.get("resetDelay"), (Integer) mine.get("toReset"));

        String stringBlocks = (String) mine.get("blocks");
        blocks = new HashMap<>();

        if (!stringBlocks.isEmpty())
            for (String block : stringBlocks.split("_"))
                blocks.put(new SerializableBlock(block.split("-")[0]), Double.parseDouble(block.split("-")[1]));

        surface = new SerializableBlock((String) mine.get("surface"));

    }

    public Map<String, Object> serialize() {

        Map<String, Object> me = new HashMap<>();

        me.put("name", getName());

        me.put("type", getType().name());

        me.put("minX", getMinX());
        me.put("minY", getMinY());
        me.put("minZ", getMinZ());

        me.put("maxX", getMaxX());
        me.put("maxY", getMaxY());
        me.put("maxZ", getMaxZ());

        me.put("world", getWorld().getName());

        StringBuilder b = new StringBuilder();

        for (Map.Entry<SerializableBlock, Double> entry : blocks.entrySet())
            b.append(entry.getKey().toString()).append("-").append(entry.getValue()).append("_");

        me.put("blocks", b.toString());
        me.put("teleport", isTeleport());

        me.put("resetDelay", getResetDelay());
        me.put("toReset", getToReset());

        if (surface != null)
            me.put("surface", surface.toString());
        else
            me.put("surface", "0:0");

        return me;

    }

    private final Map<SerializableBlock, Double> blocks;

    private SerializableBlock surface;

    public Map<SerializableBlock, Double> getBlocks() {

        return blocks;

    }

    public void setBlock(SerializableBlock block, double chance) {

        blocks.put(block, chance);

    }

    public void removeBlock(SerializableBlock block) {

        blocks.remove(block);

    }

    public void setSurface(SerializableBlock surface) {

        this.surface = surface;

    }

    public int getMinedBlocks() {

        ArrayList<Block> blocks = new ArrayList<>();

        for (int x = getMinX(); x <= getMaxX(); x++)
            for (int y = getMinY(); y <= getMaxY(); y++)
                for (int z = getMinZ(); z <= getMaxZ(); z++)
                    if (getWorld().getBlockAt(x, y, z).isEmpty())
                        blocks.add(getWorld().getBlockAt(x, y, z));

        return blocks.size();

    }

    public int getTotalBlocksInMine() {

        ArrayList<Block> blocks = new ArrayList<>();

        for (int x = getMinX(); x <= getMaxX(); x++)
            for (int y = getMinY(); y <= getMaxY(); y++)
                for (int z = getMinZ(); z <= getMaxZ(); z++)
                    blocks.add(getWorld().getBlockAt(x, y, z));

        return blocks.size();

    }

    public boolean isAllowToBreakBlock(Block b) {

        if (b == null)
            return false;

        Location l = b.getLocation();

        boolean contains = (b.getType().equals(Material.AIR) || (surface != null && !surface.getBlock().equals(Material.AIR) && surface.getBlock().equals(b.getType()) && this.surface.getData() == b.getData()));

        if (!contains)
            for (SerializableBlock block : blocks.keySet())
                if (block.getBlock().equals(b.getType()) && block.getData() == b.getData()) {

                    contains = true;
                    break;

                }

        return (contains && l.getWorld().equals(getWorld()) && l
                .getX() >= getMinX() && l.getX() <= getMaxX() && l
                .getY() >= getMinY() && l.getY() <= getMaxY() && l
                .getZ() >= getMinZ() && l.getZ() <= getMaxZ());

    }

    public void fill() {

        if (blocks.isEmpty())
            return;

        if (isTeleport())
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {

                Location l = p.getLocation();

                if (isInside(p))
                    p.teleport(new Location(getWorld(), l.getX(), getMaxY() + 2.0D, l.getZ()));

            }

        int processors = Runtime.getRuntime().availableProcessors();

        for (int threadNumber = 0; threadNumber < processors; threadNumber++) {

            final int finalThreadNumber = threadNumber;

            new BukkitRunnable() {

                public void run() {

                    for (int x = getMinX() + finalThreadNumber; x <= getMaxX(); x += processors)
                        for (int y = getMinY(); y <= getMaxY(); y++)
                            for (int z = getMinZ(); z <= getMaxZ(); z++) {

                                if ((surface != null && !surface.getBlock().equals(Material.AIR)) && y == getMaxY()) {

                                    Block block = getWorld().getBlockAt(x, y, z);

                                    if (isAllowToBreakBlock(block)) {

                                        block.setType(surface.getBlock());

                                        try {

                                            block.setData(surface.getData());

                                        } catch (Exception ignored) {}
                                    }

                                    continue;

                                }

                                double r = ThreadLocalRandom.current().nextDouble();

                                for (Map.Entry<SerializableBlock, Double> map : blocks.entrySet())
                                    if (r <= map.getValue()) {

                                        Block block = getWorld().getBlockAt(x, y, z);

                                        block.setType(map.getKey().getBlock());

                                        try {

                                            block.setData(map.getKey().getData());

                                        } catch (Exception ignored) {}

                                        break;

                                    }
                            }
                }
            }.runTask(MineCreatorPlugin.plugin);
        }
    }
}