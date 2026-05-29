package de.louis.xdGens.skin;

import org.bukkit.Material;

public enum HoeSkin {

    FOREST(
            1001,
            "Forest",
            10_000.0,
            "<gradient:#2d6e1a:#7bc947><bold>🌿 Forest Hoe</bold></gradient>",
            "<green>+10% Crop Yield</green>",
            0.10, 0.0, 0.0, false
    ),
    OCEAN(
            1002,
            "Ocean",
            40_000.0,
            "<gradient:#1a5ec7:#40e0ff><bold>🌊 Ocean Hoe</bold></gradient>",
            "<aqua>+20% Crop Yield, +10% XP</aqua>",
            0.20, 0.10, 0.0, false
    ),
    LAVA(
            1003,
            "Lava",
            120_000.0,
            "<gradient:#d42a05:#ffb020><bold>🔥 Lava Hoe</bold></gradient>",
            "<gold>+30% Crop Yield, +20% XP, +10% Tokens</gold>",
            0.30, 0.20, 0.10, false
    ),
    CRYSTAL(
            1004,
            "Crystal",
            350_000.0,
            "<gradient:#8a0fcc:#f070ff><bold>💎 Crystal Hoe</bold></gradient>",
            "<light_purple>+40% Crop Yield, +30% XP, +20% Tokens</light_purple>",
            0.40, 0.30, 0.20, false
    ),
    SHADOW(
            1005,
            "Shadow",
            900_000.0,
            "<gradient:#1a0a2e:#9025ff><bold>☽ Shadow Hoe</bold></gradient>",
            "<dark_purple>+60% Crop Yield, +50% XP, +35% Tokens, +Speed</dark_purple>",
            0.60, 0.50, 0.35, true
    );

    public final int    customModelData;
    public final String displayId;
    public final double price;
    public final String nameGradient;
    public final String bonusDescription;
    public final double cropBonus;
    public final double xpBonus;
    public final double tokenBonus;
    public final boolean speedBonus;

    HoeSkin(int customModelData, String displayId, double price,
            String nameGradient, String bonusDescription,
            double cropBonus, double xpBonus, double tokenBonus, boolean speedBonus) {
        this.customModelData  = customModelData;
        this.displayId        = displayId;
        this.price            = price;
        this.nameGradient     = nameGradient;
        this.bonusDescription = bonusDescription;
        this.cropBonus        = cropBonus;
        this.xpBonus          = xpBonus;
        this.tokenBonus       = tokenBonus;
        this.speedBonus       = speedBonus;
    }

    public static HoeSkin fromId(String id) {
        for (HoeSkin s : values()) {
            if (s.displayId.equalsIgnoreCase(id)) return s;
        }
        return null;
    }

    public Material baseMaterial() {
        return Material.DIAMOND_HOE;
    }
}
