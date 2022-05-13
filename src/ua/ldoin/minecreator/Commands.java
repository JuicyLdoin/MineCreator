package ua.ldoin.minecreator;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.ldoin.minecreator.mine.*;
import ua.ldoin.minecreator.mine.types.*;
import ua.ldoin.minecreator.utils.block.*;

public class Commands implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {

        if (!(sender instanceof Player)) {

            MineCreatorPlugin.sendMessage(sender, MineCreatorPlugin.getMessageConfig("for_players", null));
            return false;

        }

        Player player = (Player) sender;

        if (command.equalsIgnoreCase("minecreator") || command.equalsIgnoreCase("mc")) {

            if (!player.hasPermission("minecreator.manage")) {

                MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("no_permissions", null));
                return false;

            }

            if (args.length == 1 && args[0].equals("list")) {

                MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.mine_list", null));

                for (Mine mine : MineManager.mines)
                    player.sendMessage("- " + mine.getName());

                return true;

            }

            if (args.length == 2) {

                if (args[0].equals("delete")) {

                    String name = args[1];

                    if (!MineManager.mineCreated(name)) {

                        MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.not_found", null).replace("%mine%", name));
                        return false;

                    }

                    Mine mine = MineManager.getMineByName(name);

                    MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.deleted", mine));

                    MineManager.deleteMine(mine);
                    MineManager.save();

                    return true;

                }

                if (args[0].equals("reset")) {

                    String name = args[1];

                    if (!MineManager.mineCreated(name)) {

                        MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.not_found", null).replace("%mine%", name));
                        return false;

                    }

                    Mine mine = MineManager.getMineByName(name);
                    mine.callFill();

                    MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.reseted", mine));
                    return true;

                }
            }

            if (args.length == 3) {

                if (args[0].equals("create")) {

                    String name = args[1];

                    Location position1;
                    Location position2;

                    WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

                    Region selection;

                    try {

                        LocalSession session = worldEdit.getSession(player);
                        selection = session.getSelection(session.getSelectionWorld());

                    } catch (Exception e) {

                        e.printStackTrace();
                        return false;

                    }

                    position1 = new Location(player.getWorld(), selection.getMinimumPoint().getBlockX(),
                            selection.getMinimumPoint().getBlockY(),
                            selection.getMinimumPoint().getBlockZ());

                    position2 = new Location(player.getWorld(), selection.getMaximumPoint().getBlockX(),
                            selection.getMaximumPoint().getBlockY(),
                            selection.getMaximumPoint().getBlockZ());

                    if (MineManager.mineCreated(name)) {

                        MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.already_created", null).replace("%mine%", name));
                        return false;

                    }

                    Types type = Types.valueOf(args[2].toUpperCase());

                    Mine mine = null;

                    if (type.equals(Types.CUBOID))
                        mine = new CuboidMine(name, position1, position2, player.getWorld(), -1, -1);
                    else if (type.equals(Types.OVERLAY))
                        mine = new OverlayMine(name, position1, position2, player.getWorld(), -1, -1);

                    if (mine == null) {

                        MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.invalid_mine_type", null).replace("%mine%", name));
                        return false;

                    }

                    MineManager.mines.add(mine);
                    MineManager.save();

                    MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.created", mine));
                    return true;

                }

                if (args[0].equals("remove")) {

                    String name = args[1];

                    if (!MineManager.mineCreated(name)) {

                        MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.not_found", null).replace("%mine%", name));
                        return false;

                    }

                    Mine mine = MineManager.getMineByName(name);

                    SerializableBlock block = new SerializableBlock(args[2]);

                    if (mine instanceof CuboidMine) {

                        CuboidMine cuboidMine = (CuboidMine) mine;

                        if (cuboidMine.getBlocks().containsKey(block)) {

                            cuboidMine.removeBlock(block);
                            MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.remove".replace("%block%", block.toString()), mine));

                        } else
                            MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.not_fit".replace("%block%", block.toString()), mine));

                    }

                    MineManager.save();

                    MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.remove", mine));
                    return true;

                }

                if (args[0].equals("resetDelay")) {

                    if (!args[2].matches("^-?\\d+$")) {

                        MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("not_an_integer", null));
                        return false;

                    }

                    int time = Integer.parseInt(args[2]);

                    String name = args[1];

                    if (!MineManager.mineCreated(name)) {

                        MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.not_found", null).replace("%mine%", name));
                        return false;

                    }

                    Mine mine = MineManager.getMineByName(name);

                    mine.setResetDelay(time);
                    MineManager.save();

                    MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.reset_delay", mine).replace("%time%", String.valueOf(time)));
                    return true;

                }

                if (args[0].equals("surface")) {

                    String name = args[1];

                    if (!MineManager.mineCreated(name)) {

                        MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.not_found", null).replace("%mine%", name));
                        return false;

                    }

                    Mine mine = MineManager.getMineByName(name);

                    if (!(mine instanceof CuboidMine)) {

                        MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.not_cuboid", null).replace("%mine%", name));
                        return false;

                    }

                    ((CuboidMine) mine).setSurface(new SerializableBlock(args[2]));
                    MineManager.save();

                    MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.surface", mine).replace("%block%", args[2]));
                    return true;

                }
            }

            if (args.length == 4 && args[0].equals("set")) {

                String name = args[1];

                if (!MineManager.mineCreated(name)) {

                    MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.not_found", null).replace("%mine%", name));
                    return false;

                }

                Mine mine = MineManager.getMineByName(name);

                double chance = Double.parseDouble(args[3]);

                if (mine instanceof CuboidMine) {

                    SerializableBlock block = new SerializableBlock(args[2]);
                    CuboidMine cuboidMine = (CuboidMine) mine;

                    cuboidMine.setBlock(block, chance);

                } else if (mine instanceof OverlayMine) {

                    OverlayBlock block = new OverlayBlock(args[2]);
                    OverlayMine overlayMine = (OverlayMine) mine;

                    overlayMine.setBlock(block, chance);

                }

                MineManager.save();

                MineCreatorPlugin.sendMessage(player, MineCreatorPlugin.getMessageConfig("mines.set", mine)
                        .replace("%block%", args[2]).replace("%percentage%", String.valueOf(chance)));
                return true;

            }

            MineCreatorPlugin.sendMessage(player, "&e&lMineCreator Help");
            MineCreatorPlugin.sendMessage(player, "&e/minecreator &f- look all commands of &eMineCreator");

            MineCreatorPlugin.sendMessage(player, "&e/minecreator create [mine] [type] &f- create mine with name [mine]");
            MineCreatorPlugin.sendMessage(player, "&e/minecreator delete [mine] &f- delete mine with name [mine]");
            MineCreatorPlugin.sendMessage(player, "&e/minecreator reset [mine] &f- reset mine with name [mine]");

            MineCreatorPlugin.sendMessage(player, "&e/minecreator set [mine] [block] [percent] &f- set block [block] to [percent] in mine [mine]");
            MineCreatorPlugin.sendMessage(player, "&fCuboidMine block - &e1:0 &f(&cEXAMPLE&f)");
            MineCreatorPlugin.sendMessage(player, "&fOverlayMine block - &e1:0;2:0 &f(&cEXAMPLE&f) &lwhere 1:0 is ground");

            MineCreatorPlugin.sendMessage(player, "&e/minecreator remove [mine] [material] &f- remove block [material] from mine [mine]");

            MineCreatorPlugin.sendMessage(player, "&e/minecreator surface [mine] [surface] &f- set surface to [surface] on mine [mine] &l(Only on Cuboid mine!)");

            MineCreatorPlugin.sendMessage(player, "&e/minecreator list &f- look at mine list");

            MineCreatorPlugin.sendMessage(player, "&e/minecreator stats [mine] &f- set stats location of mine [mine] to current position");
            MineCreatorPlugin.sendMessage(player, "&e/minecreator resetDelay [mine] [reset_time] &f- set reset time of mine [mine] to [reset_time] (in seconds)");

        }

        return false;

    }
}