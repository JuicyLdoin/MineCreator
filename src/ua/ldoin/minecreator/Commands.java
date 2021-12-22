package ua.ldoin.minecreator;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.ldoin.minecreator.mine.*;
import ua.ldoin.minecreator.mine.types.CuboidMine;
import ua.ldoin.minecreator.utils.SerializableBlock;

import java.util.HashMap;
import java.util.Map;

public class Commands implements CommandExecutor {

    private final Map<Player, Location> pos1 = new HashMap<>();
    private final Map<Player, Location> pos2 = new HashMap<>();

    public boolean onCommand(CommandSender s, Command cmd, String command, String[] args) {

        if (!(s instanceof Player)) {

            MineCreatorPlugin.sendMessage(s, MineCreatorPlugin.getMessageConfig("for_players", null));
            return false;

        }

        Player p = (Player) s;

        if (command.equalsIgnoreCase("minecreator") || command.equalsIgnoreCase("mc")) {

            if (!p.hasPermission("minecreator.manage")) {

                MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("no_permissions", null));
                return false;

            }

            if (args.length == 1 && args[0].equals("list")) {

                MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.mine_list", null));

                for (Mine mine : MineManager.mines)
                    p.sendMessage("- " + mine.getName());

                return true;

            }

            if (args.length == 2) {

                if (args[0].equals("setpos")) {

                    if (!args[1].matches("^-?\\d+$")) {

                        MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("not_an_integer", null));
                        return false;

                    }

                    int point = Integer.parseInt(args[1]);

                    if (point > 2) {

                        MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.many_points", null));
                        return false;

                    }

                    if (point == 1)
                        pos1.put(p, p.getLocation());
                    else if (point == 2)
                        pos2.put(p, p.getLocation());

                    MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.set_point", null));
                    return true;

                }

                if (args[0].equals("create")) {

                    String name = args[1];

                    if (MineManager.mineCreated(name)) {

                        MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.already_created", null).replace("%mine%", name));
                        return false;

                    }

                    Mine mine = new CuboidMine(name, pos1.get(p), pos2.get(p), p.getWorld(), null, -1, -1);

                    MineManager.mines.add(mine);
                    MineManager.save();

                    MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.created", mine));
                    return true;

                }

                if (args[0].equals("delete")) {

                    String name = args[1];

                    if (!MineManager.mineCreated(name)) {

                        MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.not_found", null).replace("%mine%", name));
                        return false;

                    }

                    Mine mine = MineManager.getMineByName(name);

                    MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.deleted", mine));

                    MineManager.deleteMine(mine);
                    MineManager.save();

                    return true;

                }

                if (args[0].equals("reset")) {

                    String name = args[1];

                    if (!MineManager.mineCreated(name)) {

                        MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.not_found", null).replace("%mine%", name));
                        return false;

                    }

                    Mine mine = MineManager.getMineByName(name);

                    ((CuboidMine) mine).fill();

                    MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.reseted", mine));
                    return true;

                }

                if (args[0].equals("stats")) {

                    String name = args[1];

                    if (!MineManager.mineCreated(name)) {

                        MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.not_found", null).replace("%mine%", name));
                        return false;

                    }

                    Mine mine = MineManager.getMineByName(name);

                    mine.setStats(p.getLocation());
                    MineManager.save();

                    MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.stats", mine));
                    return true;

                }
            }

            if (args.length == 3) {

                if (args[0].equals("remove")) {

                    String name = args[1];

                    if (!MineManager.mineCreated(name)) {

                        MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.not_found", null).replace("%mine%", name));
                        return false;

                    }

                    Mine mine = MineManager.getMineByName(name);

                    SerializableBlock block = new SerializableBlock(args[2]);

                    if (mine instanceof CuboidMine) {

                        CuboidMine cuboidMine = (CuboidMine) mine;

                        if (cuboidMine.getBlocks().containsKey(block)) {

                            cuboidMine.removeBlock(block);
                            MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.remove".replace("%block%", block.toString()), mine));

                        } else
                            MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.not_fit".replace("%block%", block.toString()), mine));

                    }

                    MineManager.save();

                    MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.remove", mine));
                    return true;

                }

                if (args[0].equals("resetDelay")) {

                    if (!args[2].matches("^-?\\d+$")) {

                        MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("not_an_integer", null));
                        return false;

                    }

                    int time = Integer.parseInt(args[2]);

                    String name = args[1];

                    if (!MineManager.mineCreated(name)) {

                        MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.not_found", null).replace("%mine%", name));
                        return false;

                    }

                    Mine mine = MineManager.getMineByName(name);

                    mine.setResetDelay(time);
                    MineManager.save();

                    MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.reset_delay", mine).replace("%time%", String.valueOf(time)));
                    return true;

                }
            }

            if (args.length == 4 && args[0].equals("set")) {

                String name = args[1];

                if (!MineManager.mineCreated(name)) {

                    MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.not_found", null).replace("%mine%", name));
                    return false;

                }

                Mine mine = MineManager.getMineByName(name);

                SerializableBlock block = new SerializableBlock(args[2]);
                double chance = Double.parseDouble(args[3]);

                if (mine instanceof CuboidMine) {

                    CuboidMine cuboidMine = (CuboidMine) mine;

                    cuboidMine.setBlock(block, chance);

                }

                MineManager.save();

                MineCreatorPlugin.sendMessage(p, MineCreatorPlugin.getMessageConfig("mines.set", mine)
                        .replace("%block%", block.toString()).replace("%percentage%", String.valueOf(chance)));
                return true;

            }

            MineCreatorPlugin.sendMessage(p, "&e&lMineCreator Help");
            MineCreatorPlugin.sendMessage(p, "&e/minecreator &f- look all commands of &eMineCreator");

            MineCreatorPlugin.sendMessage(p, "&e/minecreator setpos [pos] &f- set mine position [pos] on current location");

            MineCreatorPlugin.sendMessage(p, "&e/minecreator create [mine] &f- create mine with name [mine]");
            MineCreatorPlugin.sendMessage(p, "&e/minecreator delete [mine] &f- delete mine with name [mine]");
            MineCreatorPlugin.sendMessage(p, "&e/minecreator reset [mine] &f- reset mine with name [mine]");

            MineCreatorPlugin.sendMessage(p, "&e/minecreator set [mine] [material] [percent] &f- set block [material] to [percent] in mine [mine]");
            MineCreatorPlugin.sendMessage(p, "&e/minecreator remove [mine] [material] &f- remove block [material] from mine [mine]");

            MineCreatorPlugin.sendMessage(p, "&e/minecreator list &f- look at mine list");

            MineCreatorPlugin.sendMessage(p, "&e/minecreator stats [mine] &f- set stats location of mine [mine] to current position");
            MineCreatorPlugin.sendMessage(p, "&e/minecreator resetDelay [mine] [reset_time] &f- set reset time of mine [mine] to [reset_time] (in seconds)");

        }

        return false;

    }
}