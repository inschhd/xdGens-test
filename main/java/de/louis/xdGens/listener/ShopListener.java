package de.louis.xdGens.listener;

import de.louis.xdGens.main.Main;
import de.louis.xdGens.manager.BackpackGUI;
import de.louis.xdGens.util.CustomItemUtil;
import de.louis.xdGens.util.MessageUtil;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {
    private final Main plugin;

    public ShopListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !CustomItemUtil.isBackpack(plugin, item)) return;
        event.setCancelled(true);
        event.getPlayer().openInventory(new BackpackGUI(plugin, event.getPlayer()).create());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!title.contains("Backpack")) return;
        event.setCancelled(true);
        if (event.getSlot() == 22) {
            plugin.getBackpackManager().sellContents(player);
            MessageUtil.sendRaw(player, MessageUtil.PREFIX + " <green>Sold backpack contents.</green>");
            player.closeInventory();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (title.contains("Backpack")) plugin.getBackpackManager().savePlayer(player);
    }
}