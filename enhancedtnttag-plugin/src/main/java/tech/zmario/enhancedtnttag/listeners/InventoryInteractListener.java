package tech.zmario.enhancedtnttag.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;

@RequiredArgsConstructor
public class InventoryInteractListener implements Listener {

    private final EnhancedTNTTag plugin;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (plugin.getLocalStorage().getBuildPlayers().contains(event.getWhoClicked().getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        if (plugin.getLocalStorage().getBuildPlayers().contains(event.getWhoClicked().getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (plugin.getLocalStorage().getBuildPlayers().contains(event.getWhoClicked().getUniqueId())) return;

        event.setCancelled(true);
    }
}
