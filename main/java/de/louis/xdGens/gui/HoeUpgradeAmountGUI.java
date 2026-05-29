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
 * Amount sub-menu — 4 rows (36 slots)
 *
 * Row 0  ──  border (type-coloured pane)
 * Row 1  ──  border | [+1] [+10] [+25] [+50] [MAX] | border | [BACK] | border
 * Row 2  ──  border | [stat display – 5 wide]       | border | [info] | border
 * Row 3  ──  border (type-coloured pane)
 *
 * Slots:
 *   +1  → 10   +10 → 11   +25 → 12   +50 → 13   MAX → 14
 *   BACK → 16
 *   Stat block → 19-23   Info → 25
 */
public class HoeUpgradeAmountGUI {

    public static final String TITLE_PREFIX = "⚡ Upgrade – ";

    public static final int SLOT_PLUS1  = 10;
    public static final int SLOT_PLUS10 = 11;
    public static final int SLOT_PLUS25 = 12;
    public static final int SLOT_PLUS50 = 13;
    public static final int SLOT_MAX    = 14;
    public static final int SLOT_BACK   = 16;

    private final Main     plugin;
    private final String   type;
    private final String   label;
    private final String   gradient;
    private final Material icon;
    private final Material borderPane;

    public HoeUpgradeAmountGUI(Main plugin, String type) {
        this.plugin = plugin;
        this.type   = type;
        switch (type) {
            case "crop"  -> {
                label      = "🌾 Crop Harvest";
                gradient   = "<gradient:#f6d365:#fda085>";
                icon       = Material.WHEAT;
                borderPane = Material.ORANGE_STAINED_GLASS_PANE;
            }
            case "xp"    -> {
                label      = "✨ XP Boost";
                gradient   = "<gradient:#7afcff:#00c2ff>";
                icon       = Material.EXPERIENCE_BOTTLE;
                borderPane = Material.CYAN_STAINED_GLASS_PANE;
            }
            default      -> {
                label      = "💰 Token Boost";
                gradient   = "<gradient:#ffd86f:#fc6262>";
                icon       = Material.GOLD_INGOT;
                borderPane = Material.YELLOW_STAINED_GLASS_PANE;
            }
        }
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(
                null, 36,
                MessageUtil.parse(gradient + "<bold>" + TITLE_PREFIX + label + "</bold></gradient>")
        );

        fillBorders(inv);

        inv.setItem(SLOT_PLUS1,  buildAmountButton(player,  1));
        inv.setItem(SLOT_PLUS10, buildAmountButton(player, 10));
        inv.setItem(SLOT_PLUS25, buildAmountButton(player, 25));
        inv.setItem(SLOT_PLUS50, buildAmountButton(player, 50));
        inv.setItem(SLOT_MAX,    buildMaxButton(player));
        inv.setItem(SLOT_BACK,   buildBackButton());

        // Row 2: stat display across slots 19-23 + info at 25
        inv.setItem(19, buildStatItem(player));
        inv.setItem(25, buildBalanceItem(player));

        player.openInventory(inv);
    }

    // ── amount buttons ────────────────────────────────────────────────────

    private ItemStack buildAmountButton(Player player, int amount) {
        int  current      = getCurrentLevel(player);
        int  maxLevel     = getMaxLevel();
        int  actualAmount = Math.min(amount, maxLevel - current);
        int  canBuy       = howManyCanAfford(player, current, maxLevel);
        boolean possible  = actualAmount > 0;
        boolean afford    = canBuy >= amount && possible;
        long totalCost    = totalCostFor(current, amount, maxLevel);

        // Choose a coloured pane-like visual: green lantern = can afford, red = can't, gray = maxed
        Material mat = !possible ? Material.GRAY_STAINED_GLASS
                : afford        ? Material.LIME_STAINED_GLASS
                :                 Material.RED_STAINED_GLASS;

        ItemStack item = new ItemStack(mat);
        ItemMeta  meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(
                gradient + "<bold>+" + amount + "</bold></gradient>"
                + (possible ? "" : " <dark_gray>(maxed)")));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        if (!possible) {
            lore.add(MessageUtil.parse("<red>Already at or near max level."));
        } else {
            lore.add(MessageUtil.parse("<gray>  " + current + " <dark_gray>→ <white>" + (current + actualAmount)));
            if (amount != actualAmount)
                lore.add(MessageUtil.parse("<dark_gray>  (caps at max — only " + actualAmount + " levels)"));
            lore.add(Component.empty());
            lore.add(MessageUtil.parse("<gray>Cost   <dark_gray>│ "
                    + (afford ? "<green>" : "<red>")
                    + NumberUtil.format(totalCost) + " Tokens"
                    + (afford ? "</green>" : "</red>")));
            lore.add(Component.empty());
            lore.add(afford
                    ? MessageUtil.parse("<green>▶ Click to buy")
                    : MessageUtil.parse("<red>✘ Cannot afford"));
        }
        meta.lore(lore);
        if (afford) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildMaxButton(Player player) {
        int  current    = getCurrentLevel(player);
        int  maxLevel   = getMaxLevel();
        int  levelsLeft = maxLevel - current;
        int  canBuy     = howManyCanAfford(player, current, maxLevel);
        boolean afford  = canBuy >= levelsLeft && levelsLeft > 0;
        long total      = totalCostFor(current, levelsLeft, maxLevel);

        Material mat = levelsLeft <= 0 ? Material.GRAY_STAINED_GLASS
                : afford              ? Material.LIME_STAINED_GLASS
                :                       Material.RED_STAINED_GLASS;

        ItemStack item = new ItemStack(mat);
        ItemMeta  meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(gradient + "<bold>✦ MAX ✦</bold></gradient>"));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        if (levelsLeft <= 0) {
            lore.add(MessageUtil.parse("<gold><bold>Already maxed out!</bold></gold>"));
        } else {
            lore.add(MessageUtil.parse("<gray>  " + current + " <dark_gray>→ <white>" + maxLevel
                    + " <dark_gray>(+" + levelsLeft + " levels)"));
            lore.add(Component.empty());
            lore.add(MessageUtil.parse("<gray>Cost   <dark_gray>│ "
                    + (afford ? "<green>" : "<red>")
                    + NumberUtil.format(total) + " Tokens"
                    + (afford ? "</green>" : "</red>")));
            if (!afford)
                lore.add(MessageUtil.parse("<yellow>  You can afford " + canBuy + " level" + (canBuy == 1 ? "" : "s") + "."));
            lore.add(Component.empty());
            lore.add(afford
                    ? MessageUtil.parse("<green>▶ Click to max out!")
                    : MessageUtil.parse("<red>✘ Not enough Tokens"));
        }
        meta.lore(lore);
        if (levelsLeft > 0) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta  meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gray><bold>← Back</bold></gray>"));
        meta.lore(List.of(
                Component.empty(),
                MessageUtil.parse("<dark_gray>Return to all upgrades.")
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    // ── info items ────────────────────────────────────────────────────────

    /** Current level + bonus displayed as a focused item. */
    private ItemStack buildStatItem(Player player) {
        int current = getCurrentLevel(player);
        int maxLevel = getMaxLevel();

        ItemStack item = new ItemStack(icon);
        ItemMeta  meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(gradient + "<bold>" + label + "</bold></gradient>"));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(MessageUtil.parse("<gray>Level  <dark_gray>│ <white>" + NumberUtil.format(current)
                + " <dark_gray>/ " + NumberUtil.format(maxLevel)));
        lore.add(MessageUtil.parse("<gray>       <dark_gray>  " + levelBar(current, maxLevel, 12)));
        lore.add(Component.empty());

        // Bonus line per type
        switch (type) {
            case "crop" -> lore.add(MessageUtil.parse(
                    "<gray>Bonus  <dark_gray>│ <gradient:#f6d365:#fda085>+" + current + " crops/break</gradient>"));
            case "xp" -> lore.add(MessageUtil.parse(
                    "<gray>Bonus  <dark_gray>│ <gradient:#7afcff:#00c2ff>+"
                    + NumberUtil.format(plugin.getHoeUpgradeManager().getXpPercentBonus(player)) + "% XP</gradient>"));
            default -> lore.add(MessageUtil.parse(
                    "<gray>Bonus  <dark_gray>│ <gradient:#ffd86f:#fc6262>+"
                    + NumberUtil.format(plugin.getHoeUpgradeManager().getTokenPercentBonus(player)) + "% Tokens</gradient>"));
        }

        meta.lore(lore);
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    /** Player balance overview. */
    private ItemStack buildBalanceItem(Player player) {
        long   tokens = plugin.getCurrencyManager().getTokens(player);
        double money  = plugin.getCurrencyManager().getMoney(player);

        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta  meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gradient:#a18cd1:#fbc2eb><bold>Wallet</bold></gradient>"));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(MessageUtil.parse("<gray>Tokens  <dark_gray>│ <gold>" + NumberUtil.format(tokens) + "</gold>"));
        lore.add(MessageUtil.parse("<gray>Money   <dark_gray>│ <green>$" + NumberUtil.format(money) + "</green>"));
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    // ── layout helpers ────────────────────────────────────────────────────

    private void fillBorders(Inventory inv) {
        ItemStack border = pane(borderPane);
        ItemStack dark   = pane(Material.BLACK_STAINED_GLASS_PANE);

        // Row 0 & row 3 (top/bottom)
        for (int i = 0;  i < 9;  i++) inv.setItem(i, border.clone());
        for (int i = 27; i < 36; i++) inv.setItem(i, border.clone());

        // Side columns
        int[] sides = {9, 17, 18, 26};
        for (int s : sides) inv.setItem(s, border.clone());

        // Separator between button area (10-14) and back (16)
        inv.setItem(15, border.clone());

        // Inner filler for row 2 non-button slots
        for (int i = 18; i <= 26; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, dark.clone());
        }
        // Slots 20-24 inner filler
        for (int i = 20; i <= 24; i++) inv.setItem(i, dark.clone());
    }

    // ── reused logic ──────────────────────────────────────────────────────

    public String getType() { return type; }

    private int howManyCanAfford(Player player, int current, int max) {
        long budget = plugin.getCurrencyManager().getTokens(player);
        int count = 0;
        HoeUpgradeManager mgr = plugin.getHoeUpgradeManager();
        for (int lvl = current + 1; lvl <= max; lvl++) {
            long cost = getCost(mgr, lvl);
            if (budget < cost) break;
            budget -= cost;
            count++;
        }
        return count;
    }

    private long totalCostFor(int current, int amount, int max) {
        HoeUpgradeManager mgr = plugin.getHoeUpgradeManager();
        long total = 0;
        for (int i = 1; i <= amount && (current + i) <= max; i++) total += getCost(mgr, current + i);
        return total;
    }

    private int getCurrentLevel(Player player) {
        return switch (type) {
            case "crop" -> plugin.getHoeUpgradeManager().getCropLevel(player);
            case "xp"   -> plugin.getHoeUpgradeManager().getXpLevel(player);
            default     -> plugin.getHoeUpgradeManager().getTokenLevel(player);
        };
    }

    private int getMaxLevel() {
        return switch (type) {
            case "crop" -> HoeUpgradeManager.MAX_CROP_LEVEL;
            case "xp"   -> HoeUpgradeManager.MAX_XP_LEVEL;
            default     -> HoeUpgradeManager.MAX_TOKEN_LEVEL;
        };
    }

    private long getCost(HoeUpgradeManager mgr, int lvl) {
        return switch (type) {
            case "crop" -> mgr.getCropCost(lvl);
            case "xp"   -> mgr.getXpCost(lvl);
            default     -> mgr.getTokenCost(lvl);
        };
    }

    private String levelBar(int current, int max, int segments) {
        int filled = max > 0 ? (int) Math.round((double) current / max * segments) : 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments; i++)
            sb.append(i < filled ? "<green>█" : "<dark_gray>░");
        return sb.toString();
    }

    private ItemStack pane(Material mat) {
        ItemStack p = new ItemStack(mat);
        ItemMeta  m = p.getItemMeta();
        m.displayName(Component.empty());
        p.setItemMeta(m);
        return p;
    }
}
