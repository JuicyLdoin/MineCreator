package ua.ldoin.minecreator;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import ua.ldoin.minecreator.mine.Mine;
import ua.ldoin.minecreator.mine.MineManager;

public class Listeners implements Listener {

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {

        if (MineCreatorPlugin.plugin.getConfig().getBoolean("mine.blocks_drop_to_inventory"))
            for (Mine mine : MineManager.mines)
                if (mine.isAllowToBreakBlock(event.getBlock())) {

                    Player player = event.getPlayer();

                    if (player.getInventory().firstEmpty() == -1) {

                        event.setCancelled(true);
                        return;

                    }

                    if (player.getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH))
                        player.getInventory().addItem(new ItemStack(event.getBlock().getType(), 1, event.getBlock().getData()));
                    else
                        event.getBlock().getDrops(player.getItemInHand()).forEach(items -> player.getInventory().addItem(new ItemStack[]{new ItemStack(items)}));

                }
    }
}