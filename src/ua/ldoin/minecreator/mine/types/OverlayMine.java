package ua.ldoin.minecreator.mine.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ua.ldoin.minecreator.MineCreatorPlugin;
import ua.ldoin.minecreator.mine.Mine;
import ua.ldoin.minecreator.utils.LocationUtil;
import ua.ldoin.minecreator.utils.block.OverlayBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class OverlayMine extends Mine implements ConfigurationSerializable {

    public OverlayMine(String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world, Location stats, int resetDelay, int toReset) {

        super(name, Types.OVERLAY, minX, minY, minZ, maxX, maxY, maxZ, world, stats, false, resetDelay, toReset);

        blocks = new HashMap<>();

    }

    public OverlayMine(String name, Location minPoint, Location maxPoint, World world, Location stats, int resetDelay, int toReset) {

        super(name, Types.OVERLAY, minPoint, maxPoint, world, stats, false, resetDelay, toReset);

        blocks = new HashMap<>();

    }

    public OverlayMine(Map<String, Object> mine) {

        super((String) mine.get("name"), Types.OVERLAY, (Integer) mine.get("minX"), (Integer) mine.get("minY"), (Integer) mine.get("minZ"),
                (Integer) mine.get("maxX"), (Integer) mine.get("maxY"), (Integer) mine.get("maxZ"), Bukkit.getWorld((String) mine.get("world")),
                LocationUtil.getLocation((String) mine.get("stats")), false, (Integer) mine.get("resetDelay"), (Integer) mine.get("toReset"));

        String stringBlocks = (String) mine.get("blocks");
        blocks = new HashMap<>();

        if (!stringBlocks.isEmpty())
            for (String block : stringBlocks.split("_"))
                blocks.put(new OverlayBlock(block.split("-")[0]), Double.parseDouble(block.split("-")[1]));

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

        for (Map.Entry<OverlayBlock, Double> entry : blocks.entrySet())
            b.append(entry.getKey().toString()).append("-").append(entry.getValue()).append("_");

        me.put("blocks", b.toString());

        me.put("stats", LocationUtil.getLocation(getStats()));
        me.put("teleport", isTeleport());

        me.put("resetDelay", getResetDelay());
        me.put("toReset", getToReset());

        return me;

    }

    private final Map<OverlayBlock, Double> blocks;

    public Map<OverlayBlock, Double> getBlocks() {

        return blocks;

    }

    public void setBlock(OverlayBlock block, double chance) {

        blocks.put(block, chance);

    }

    public void removeBlock(OverlayBlock block) {

        blocks.remove(block);

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

                                double r = ThreadLocalRandom.current().nextDouble();

                                for (Map.Entry<OverlayBlock, Double> map : blocks.entrySet())
                                    if (getWorld().getBlockAt(x, y - 1, z).getTypeId() == map.getKey().getGround().getBlock() &&
                                            getWorld().getBlockAt(x, y - 1, z).getData() == map.getKey().getGround().getData())
                                        if (r <= map.getValue()) {

                                            getWorld().getBlockAt(x, y, z).setTypeIdAndData(map.getKey().getBlock().getBlock(), map.getKey().getBlock().getData(), true);
                                            break;

                                        }
                            }
                }
            }.runTask(MineCreatorPlugin.plugin);
        }
    }
}