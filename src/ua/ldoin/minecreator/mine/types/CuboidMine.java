package ua.ldoin.minecreator.mine.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ua.ldoin.minecreator.MineCreatorPlugin;
import ua.ldoin.minecreator.mine.Mine;
import ua.ldoin.minecreator.utils.CompositionEntry;
import ua.ldoin.minecreator.utils.LocationUtil;
import ua.ldoin.minecreator.utils.SerializableBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CuboidMine extends Mine implements ConfigurationSerializable {

    public CuboidMine(String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world, Location stats, int resetDelay, int toReset) {

        super(name, Types.CUBOID, minX, minY, minZ, maxX, maxY, maxZ, world, stats, true, resetDelay, toReset);

        blocks = new HashMap<>();

    }

    public CuboidMine(String name, Location minPoint, Location maxPoint, World world, Location stats, int resetDelay, int toReset) {

        super(name, Types.CUBOID, minPoint, maxPoint, world, stats, true, resetDelay, toReset);

        blocks = new HashMap<>();

    }

    public CuboidMine(Map<String, Object> mine) {

        super((String) mine.get("name"), Types.CUBOID, (Integer) mine.get("minX"), (Integer) mine.get("minY"), (Integer) mine.get("minZ"),
                (Integer) mine.get("maxX"), (Integer) mine.get("maxY"), (Integer) mine.get("maxZ"), Bukkit.getWorld((String) mine.get("world")),
                LocationUtil.getLocation((String) mine.get("stats")), true, (Integer) mine.get("resetDelay"), (Integer) mine.get("toReset"));

        String stringBlocks = (String) mine.get("blocks");
        blocks = new HashMap<>();

        if (!stringBlocks.isEmpty())
            for (String block : stringBlocks.split(".next="))
                blocks.put(new SerializableBlock(Integer.parseInt(block.split(".chance=")[0].split(":")[0]), Byte.parseByte(block.split(".chance=")[0].split(":")[1])),
                        Double.parseDouble(block.split(".chance=")[1]));

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
            b.append(entry.getKey().toString()).append(".chance=").append(entry.getValue()).append(".next=");

        me.put("blocks", b.toString());

        me.put("stats", LocationUtil.getLocation(getStats()));
        me.put("teleport", isTeleport());

        me.put("resetDelay", getResetDelay());
        me.put("toReset", getToReset());

        return me;

    }

    private final Map<SerializableBlock, Double> blocks;

    public Map<SerializableBlock, Double> getBlocks() {

        return blocks;

    }

    public void setBlock(SerializableBlock block, double chance) {

        blocks.put(block, chance);

    }

    public void removeBlock(SerializableBlock block) {

        blocks.remove(block);

    }

    public void fill() {

        if (blocks.isEmpty())
            return;

        final List<CompositionEntry> probabilityMap = CompositionEntry.mapBlocks(blocks);

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

                                double r = ThreadLocalRandom.current().nextDouble();

                                for (CompositionEntry ce : probabilityMap)
                                    if (r <= ce.getChance()) {

                                        getWorld().getBlockAt(x, y, z).setTypeIdAndData(ce.getBlock().getBlock(), ce.getBlock().getData(), true);
                                        break;

                                    }
                            }
                }
            }.runTask(MineCreatorPlugin.plugin);
        }
    }
}