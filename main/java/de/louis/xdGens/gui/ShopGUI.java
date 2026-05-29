package de.louis.xdGens.gui;

import de.louis.xdGens.main.Main;
import de.louis.xdGens.skin.HoeSkin;
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

    public static final String TITLE           = "xdGens Shop";
    public static final String TITLE_SKINS     = "xdGens Shop - Skins";
    public static final double BACKPACK_PRICE  = 50000.0;
    public static final int    SLOT_BACKPACK   = 13;
    public static final int    SLOT_SKINS_BTN  = 22;
    public static final int    SLOT_BACK       = 22;

    // Skin slots: 10, 12, 14, 16, row 2 → slots 10–16 odd
    public static final int[] SKIN_SLOTS = { 10, 12, 14, 16, 22 };

    private final Main   plugin;
    private final Player player;

    public ShopGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    // ─── Main shop page ──────────────────────────────────────────────────────

    public Inventory create() {
        Inventory inv = Bukkit.createInventory(null, 27,
                MessageUtil.parse("<gradient:#f6d365:#fda085><bold>xdGens Shop</bold></gradient>"));
        fill(inv);
        inv.setItem(SLOT_BACKPACK,  buildBackpackItem());
        inv.setItem(SLOT_SKINS_BTN, buildSkinsButton());
        return inv;
    }

    // ─── Skin shop page ──────────────────────────────────────────────────────

    public Inventory createSkinPage() {
        Inventory inv = Bukkit.createInventory(null, 36,
                MessageUtil.parse("<gradient:#a18cd1:#fbc2eb><bold>xdGens Shop - Skins</bold></gradient>"));
        fill(inv);
        HoeSkin[] skins = HoeSkin.values();
        for (int i = 0; i < skins.length && i < 5; i++) {
            inv.setItem(SKIN_SLOTS[i], buildSkinItem(skins[i]));
        }
        inv.setItem(SLOT_BACK, buildBackButton());
        return inv;
    }

    // ─── Item builders ───────────────────────────────────────────────────────

    private ItemStack buildBackpackItem() {
        boolean has = plugin.getBackpackManager().playerHasItem(player);
        ItemStack item = new ItemStack(Material.BUNDLE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gradient:#84fab0:#8fd3f4><bold>Crop Backpack</bold></gradient>"));
        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>Stores harvested crops automatically.</gray>"));
        lore.add(MessageUtil.parse("<gray>Upgradeable to level 25.</gray>"));
        lore.add(MessageUtil.parse(" "));
        if (has) {
            lore.add(MessageUtil.parse("<red>Already owned!</red>"));
        } else {
            lore.add(MessageUtil.parse("<gray>Price: <green>$" + NumberUtil.format(BACKPACK_PRICE) + "</green>"));
            lore.add(MessageUtil.parse("<dark_gray>Click to buy!</dark_gray>"));
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildSkinsButton() {
        ItemStack item = new ItemStack(Material.DIAMOND_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gradient:#a18cd1:#fbc2eb><bold>Hoe Skins</bold></gradient>"));
        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>5 kosmetische Skins mit Boni.</gray>"));
        lore.add(MessageUtil.parse("<dark_gray>Click to browse!</dark_gray>"));
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack buildSkinItem(HoeSkin skin) {
        boolean owned  = plugin.getSkinManager().owns(player, skin);
        boolean active = skin == plugin.getSkinManager().getActiveSkin(player);

        ItemStack item = new ItemStack(skin.baseMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(skin.customModelData);
        meta.displayName(MessageUtil.parse(skin.nameGradient));

        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse(" "));
        lore.add(MessageUtil.parse(skin.bonusDescription));
        lore.add(MessageUtil.parse(" "));

        if (active) {
            lore.add(MessageUtil.parse("<green><bold>✔ Equipped</bold></green>"));
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else if (owned) {
            lore.add(MessageUtil.parse("<aqua>Owned — Click to equip</aqua>"));
        } else {
            lore.add(MessageUtil.parse("<gray>Price: <green>$" + NumberUtil.format(skin.price) + "</green>"));
            lore.add(MessageUtil.parse("<dark_gray>Click to buy!</dark_gray>"));
        }

        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gray>← Back</gray>"));
        item.setItemMeta(meta);
        return item;
    }

    private void fill(Inventory inv) {
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = border.getItemMeta();
        meta.displayName(MessageUtil.parse("<gray> </gray>"));
        border.setItemMeta(meta);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, border.clone());
    }
}
