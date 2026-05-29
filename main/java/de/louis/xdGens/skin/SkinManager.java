package de.louis.xdGens.skin;

import de.louis.xdGens.main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SkinManager {

    private final Main plugin;
    private final Map<UUID, Set<HoeSkin>>  ownedSkins  = new HashMap<>();
    private final Map<UUID, HoeSkin>       activeSkins = new HashMap<>();

    private File              file;
    private FileConfiguration config;

    public SkinManager(Main plugin) {
        this.plugin = plugin;
        setup();
        loadAll();
    }

    // ─── public API ──────────────────────────────────────────────────────────

    public boolean owns(Player player, HoeSkin skin) {
        return ownedSkins.getOrDefault(player.getUniqueId(), Set.of()).contains(skin);
    }

    public HoeSkin getActiveSkin(Player player) {
        return activeSkins.get(player.getUniqueId());
    }

    public boolean buySkin(Player player, HoeSkin skin) {
        if (owns(player, skin)) return false;
        if (!plugin.getCurrencyManager().removeMoney(player, skin.price)) return false;
        ownedSkins.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(skin);
        activeSkins.put(player.getUniqueId(), skin);
        plugin.getCurrencyManager().savePlayer(player);
        saveAll();
        return true;
    }

    public boolean equipSkin(Player player, HoeSkin skin) {
        if (!owns(player, skin)) return false;
        activeSkins.put(player.getUniqueId(), skin);
        saveAll();
        return true;
    }

    // ─── persistence ─────────────────────────────────────────────────────────

    private void setup() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        file = new File(plugin.getDataFolder(), "skins.yml");
        if (!file.exists()) {
            try { file.createNewFile(); }
            catch (IOException e) { plugin.getLogger().severe("Could not create skins.yml: " + e.getMessage()); }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    private void loadAll() {
        if (!config.isConfigurationSection("players")) return;
        for (String key : config.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                List<String> owned = config.getStringList("players." + key + ".owned");
                Set<HoeSkin> skins = new HashSet<>();
                for (String id : owned) {
                    HoeSkin s = HoeSkin.fromId(id);
                    if (s != null) skins.add(s);
                }
                ownedSkins.put(uuid, skins);
                String activeId = config.getString("players." + key + ".active");
                if (activeId != null) {
                    HoeSkin active = HoeSkin.fromId(activeId);
                    if (active != null) activeSkins.put(uuid, active);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void saveAll() {
        for (UUID uuid : ownedSkins.keySet()) {
            String path = "players." + uuid;
            List<String> ids = new ArrayList<>();
            for (HoeSkin s : ownedSkins.getOrDefault(uuid, Set.of())) ids.add(s.displayId);
            config.set(path + ".owned", ids);
            HoeSkin active = activeSkins.get(uuid);
            config.set(path + ".active", active != null ? active.displayId : null);
        }
        try { config.save(file); }
        catch (IOException e) { plugin.getLogger().severe("Could not save skins.yml: " + e.getMessage()); }
    }
}
