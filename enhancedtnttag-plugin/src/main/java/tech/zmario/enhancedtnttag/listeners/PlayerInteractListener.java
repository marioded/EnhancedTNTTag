package tech.zmario.enhancedtnttag.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (itemStack == null || itemStack.getType() == Material.AIR ||
                event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;

        NBTItem nbtItem = new NBTItem(itemStack);
        String key = "TNTTag-Command";

        if (!nbtItem.hasKey("key")) return;

        Bukkit.getServer().dispatchCommand(player, nbtItem.getString(key));
    }
}
