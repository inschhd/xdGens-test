package de.louis.xdGens.gui;

import de.louis.xdGens.main.Main;
import de.louis.xdGens.util.MessageUtil;
import de.louis.xdGens.util.NumberUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    public static final String TITLE = "🎒 Backpack";

    private final Main plugin;
    private final Player player;

    public ShopGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public Inventory create() {
        Inventory inv = Bukkit.createInventory(
                null,
                27,
                MessageUtil.parse("<gradient:#84fab0:#8fd3f4><bold>🎒 Backpack</bold></gradient>")
        );

        fill(inv);
        inv.setItem(13, buildInfo());
        inv.setItem(22, buildSell());

        return inv;
    }

    private ItemStack buildInfo() {
        ItemStack item = new ItemStack(Material.BUNDLE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gradient:#84fab0:#8fd3f4><bold>Backpack Contents</bold></gradient>"));

        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>Level: <aqua>" + plugin.getBackpackManager().getLevel(player) + "</aqua>"));
        lore.add(MessageUtil.parse("<gray>Stored Wheat: <yellow>" + plugin.getBackpackManager().getStoredWheat(player) + "</yellow>"));
        lore.add(MessageUtil.parse("<gray>Stored Money: <green>$" + NumberUtil.format(plugin.getBackpackManager().getStoredMoney(player)) + "</green>"));
        lore.add(MessageUtil.parse("<dark_gray>Auto-stores harvest until full.</dark_gray>"));

        meta.lore(lore);
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildSell() {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gradient:#f6d365:#fda085><bold>Sell Contents</bold></gradient>"));

        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>Sell backpack wheat for money.</gray>"));

        meta.lore(lore);
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private void fill(Inventory inv) {
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = border.getItemMeta();
        meta.displayName(MessageUtil.parse("<gray> </gray>"));
        border.setItemMeta(meta);

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, border.clone());
        }
    }
}