package de.louis.xdGens.gui;

import de.louis.xdGens.main.Main;
import de.louis.xdGens.manager.HoeUpgradeManager;
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

/**
 * Main Hoe Upgrade GUI  —  5 rows (45 slots)
 *
 * Row 0  ──  border (purple panes)
 * Row 1  ──  border | [CROP  col 2] filler filler [XP    col 6] border
 * Row 2  ──  border | [STATS col 2] filler filler [STATS col 6] border
 * Row 3  ──  border | [TOKEN col 2] filler filler [HOE   col 6] border   ← HOE upgrade on right
 * Row 4  ──  border (purple panes)
 *
 * Exact slots used:
 *   Crop   → 11    XP     → 15
 *   Token  → 29    Hoe    → 33
 *   Borders: top row 0-8, bottom row 36-44, left col 0/9/18/27/36, right col 8/17/26/35/44
 */
public class HoeUpgradeGUI {

    public static final String GUI_TITLE = "⚡ Hoe Upgrades";

    // ── slot constants ────────────────────────────────────────────────────
    private static final int SLOT_CROP  = 11;
    private static final int SLOT_XP    = 15;
    private static final int SLOT_TOKEN = 29;
    private static final int SLOT_HOE   = 33;

    private final Main plugin;

    public HoeUpgradeGUI(Main plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(
                null, 45,
                MessageUtil.parse("<gradient:#a18cd1:#fbc2eb><bold>⚡ Hoe Upgrades</bold></gradient>")
        );

        fillBorders(inv);

        inv.setItem(SLOT_CROP,  buildCropItem(player));
        inv.setItem(SLOT_XP,    buildXpItem(player));
        inv.setItem(SLOT_TOKEN, buildTokenItem(player));
        inv.setItem(SLOT_HOE,   buildHoeItem(player));

        // Centre decoration – info pane
        inv.setItem(22, buildInfoPane(player));

        player.openInventory(inv);
    }

    // ── upgrade items ─────────────────────────────────────────────────────

    private ItemStack buildCropItem(Player player) {
        HoeUpgradeManager m = plugin.getHoeUpgradeManager();
        int lvl   = m.getCropLevel(player);
        int maxLv = HoeUpgradeManager.MAX_CROP_LEVEL;
        boolean maxed = lvl >= maxLv;

        ItemStack item = new ItemStack(Material.WHEAT);
        ItemMeta  meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gradient:#f6d365:#fda085><bold>🌾 Crop Harvest</bold></gradient>"));

        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<dark_gray>─────────────────────"));
        lore.add(MessageUtil.parse("<gray>Bonus: <gradient:#f6d365:#fda085>+" + lvl + " crops</gradient> <dark_gray>per break"));
        lore.add(MessageUtil.parse("<gray>Level: " + levelBar(lvl, maxLv)));
        lore.add(MessageUtil.parse("<gray>       <white>" + lvl + "</white><dark_gray>/" + maxLv));
        lore.add(MessageUtil.parse("<dark_gray>─────────────────────"));
        if (maxed) {
            lore.add(MessageUtil.parse("<gradient:#f6d365:#fda085><bold>✦ MAXED OUT ✦</bold></gradient>"));
        } else {
            long tokens = plugin.getCurrencyManager().getTokens(player);
            long cost   = m.getCropCost(lvl + 1);
            lore.add(MessageUtil.parse("<gray>Next cost: " + costTag(tokens, cost) + " Tokens"));
            lore.add(MessageUtil.parse(""));
            lore.add(tokens >= cost
                    ? MessageUtil.parse("<green>▶ Click to choose amount")
                    : MessageUtil.parse("<red>✘ Not enough Tokens"));
        }

        applyGlow(meta, lvl > 0);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildXpItem(Player player) {
        HoeUpgradeManager m = plugin.getHoeUpgradeManager();
        int lvl   = m.getXpLevel(player);
        int maxLv = HoeUpgradeManager.MAX_XP_LEVEL;
        boolean maxed = lvl >= maxLv;

        ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta  meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gradient:#7afcff:#00c2ff><bold>✨ XP Boost</bold></gradient>"));

        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<dark_gray>─────────────────────"));
        lore.add(MessageUtil.parse("<gray>Bonus: <gradient:#7afcff:#00c2ff>+" + NumberUtil.format(m.getXpPercentBonus(player)) + "% XP</gradient>"));
        lore.add(MessageUtil.parse("<gray>Level: <aqua>" + NumberUtil.format(lvl) + "</aqua><dark_gray>/" + NumberUtil.format(maxLv)));
        lore.add(MessageUtil.parse("<dark_gray>─────────────────────"));
        if (maxed) {
            lore.add(MessageUtil.parse("<gradient:#7afcff:#00c2ff><bold>✦ MAXED OUT ✦</bold></gradient>"));
        } else {
            long tokens = plugin.getCurrencyManager().getTokens(player);
            long cost   = m.getXpCost(lvl + 1);
            lore.add(MessageUtil.parse("<gray>Next cost: " + costTag(tokens, cost) + " Tokens"));
            lore.add(MessageUtil.parse(""));
            lore.add(tokens >= cost
                    ? MessageUtil.parse("<green>▶ Click to choose amount")
                    : MessageUtil.parse("<red>✘ Not enough Tokens"));
        }

        applyGlow(meta, lvl > 0);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildTokenItem(Player player) {
        HoeUpgradeManager m = plugin.getHoeUpgradeManager();
        int lvl   = m.getTokenLevel(player);
        int maxLv = HoeUpgradeManager.MAX_TOKEN_LEVEL;
        boolean maxed = lvl >= maxLv;

        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta  meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gradient:#ffd86f:#fc6262><bold>💰 Token Boost</bold></gradient>"));

        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<dark_gray>─────────────────────"));
        lore.add(MessageUtil.parse("<gray>Bonus: <gradient:#ffd86f:#fc6262>+" + NumberUtil.format(m.getTokenPercentBonus(player)) + "% Tokens</gradient>"));
        lore.add(MessageUtil.parse("<gray>Level: <gold>" + NumberUtil.format(lvl) + "</gold><dark_gray>/" + NumberUtil.format(maxLv)));
        lore.add(MessageUtil.parse("<dark_gray>─────────────────────"));
        if (maxed) {
            lore.add(MessageUtil.parse("<gradient:#ffd86f:#fc6262><bold>✦ MAXED OUT ✦</bold></gradient>"));
        } else {
            long tokens = plugin.getCurrencyManager().getTokens(player);
            long cost   = m.getTokenCost(lvl + 1);
            lore.add(MessageUtil.parse("<gray>Next cost: " + costTag(tokens, cost) + " Tokens"));
            lore.add(MessageUtil.parse(""));
            lore.add(tokens >= cost
                    ? MessageUtil.parse("<green>▶ Click to choose amount")
                    : MessageUtil.parse("<red>✘ Not enough Tokens"));
        }

        applyGlow(meta, lvl > 0);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildHoeItem(Player player) {
        HoeUpgradeManager m = plugin.getHoeUpgradeManager();
        int lvl   = m.getHoeLevel(player);
        int maxLv = HoeUpgradeManager.MAX_HOE_LEVEL;
        boolean maxed = lvl >= maxLv;

        ItemStack item = new ItemStack(m.getHoeMaterial(player));
        ItemMeta  meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gradient:#c0c0c0:#ffffff><bold>⚒ Hoe Material</bold></gradient>"));

        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<dark_gray>─────────────────────"));
        lore.add(MessageUtil.parse("<gray>Material: <white>" + m.getHoeMaterialName(player) + "</white>"));
        lore.add(MessageUtil.parse("<gray>Stage: <white>" + m.getHoeStageInMaterial(player) + "</white><dark_gray>/3"));
        lore.add(MessageUtil.parse("<gray>Speed: <aqua>+" + String.format("%.0f", (m.getWalkSpeed(player) - 0.2f) / (0.7f - 0.2f) * 100) + "% walk speed</aqua>"));
        lore.add(MessageUtil.parse("<gray>XP bonus: <green>+" + String.format("%.0f", m.getHoeXpPercentBonus(player)) + "%</green>"));
        lore.add(MessageUtil.parse("<gray>Level: " + levelBar(lvl, maxLv)));
        lore.add(MessageUtil.parse("<gray>       <white>" + lvl + "</white><dark_gray>/" + maxLv));
        lore.add(MessageUtil.parse("<dark_gray>─────────────────────"));
        if (maxed) {
            lore.add(MessageUtil.parse("<gradient:#c0c0c0:#ffffff><bold>✦ MAXED OUT ✦</bold></gradient>"));
        } else {
            double money = plugin.getCurrencyManager().getMoney(player);
            long cost    = m.getHoeCost(lvl + 1);
            lore.add(MessageUtil.parse("<gray>Next cost: " + costTagMoney(money, cost) + " $"));
            lore.add(MessageUtil.parse(""));
            lore.add(money >= cost
                    ? MessageUtil.parse("<green>▶ Click to upgrade")
                    : MessageUtil.parse("<red>✘ Not enough money"));
        }

        applyGlow(meta, lvl > 1);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    // ── centre info pane ──────────────────────────────────────────────────

    private ItemStack buildInfoPane(Player player) {
        long tokens = plugin.getCurrencyManager().getTokens(player);
        double money = plugin.getCurrencyManager().getMoney(player);

        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta  meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gradient:#a18cd1:#fbc2eb><bold>Your Stats</bold></gradient>"));

        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<dark_gray>─────────────────────"));
        lore.add(MessageUtil.parse("<gray>Tokens: <gold>" + NumberUtil.format(tokens) + "</gold>"));
        lore.add(MessageUtil.parse("<gray>Money:  <green>$" + NumberUtil.format(money) + "</green>"));
        lore.add(MessageUtil.parse("<dark_gray>─────────────────────"));
        lore.add(MessageUtil.parse("<gray>Crop Lv: <gradient:#f6d365:#fda085>" + plugin.getHoeUpgradeManager().getCropLevel(player) + "</gradient>"));
        lore.add(MessageUtil.parse("<gray>XP   Lv: <gradient:#7afcff:#00c2ff>" + plugin.getHoeUpgradeManager().getXpLevel(player) + "</gradient>"));
        lore.add(MessageUtil.parse("<gray>Token Lv: <gradient:#ffd86f:#fc6262>" + plugin.getHoeUpgradeManager().getTokenLevel(player) + "</gradient>"));
        lore.add(MessageUtil.parse("<gray>Hoe   Lv: <gradient:#c0c0c0:#ffffff>" + plugin.getHoeUpgradeManager().getHoeLevel(player) + "</gradient>"));
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    // ── layout helpers ────────────────────────────────────────────────────

    /**
     * Fills the outer border with purple panes (top/bottom rows + left/right columns).
     * Inner area (slots 10-16, 19-25, 28-34) stays empty/free.
     */
    private void fillBorders(Inventory inv) {
        ItemStack pane = pane(Material.PURPLE_STAINED_GLASS_PANE);
        for (int i = 0;  i < 9;  i++) inv.setItem(i, pane.clone());       // top row
        for (int i = 36; i < 45; i++) inv.setItem(i, pane.clone());       // bottom row
        int[] leftRight = {9, 17, 18, 26, 27, 35};
        for (int s : leftRight) inv.setItem(s, pane.clone());

        // Inner filler (dark grey) for remaining non-button slots
        ItemStack dark = pane(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 10; i <= 34; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, dark.clone());
        }
    }

    // ── small helpers ─────────────────────────────────────────────────────

    private String levelBar(int current, int max) {
        int segments = 10;
        int filled   = max > 0 ? (int) Math.round((double) current / max * segments) : 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments; i++)
            sb.append(i < filled ? "<green>█" : "<dark_gray>░");
        return sb.toString();
    }

    /** Coloured cost tag (green = can afford, red = cannot). */
    private String costTag(long balance, long cost) {
        return (balance >= cost ? "<green>" : "<red>") + NumberUtil.format(cost) + (balance >= cost ? "</green>" : "</red>");
    }
    private String costTagMoney(double balance, long cost) {
        return (balance >= cost ? "<green>" : "<red>") + NumberUtil.format(cost) + (balance >= cost ? "</green>" : "</red>");
    }

    private void applyGlow(ItemMeta meta, boolean glow) {
        if (!glow) return;
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    private ItemStack pane(Material mat) {
        ItemStack p = new ItemStack(mat);
        ItemMeta  m = p.getItemMeta();
        m.displayName(Component.empty());
        p.setItemMeta(m);
        return p;
    }
}
