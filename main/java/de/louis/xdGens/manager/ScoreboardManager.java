package de.louis.xdGens.manager;

import de.louis.xdGens.main.Main;
import de.louis.xdGens.util.NumberUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private final Main plugin;
    private final Map<UUID, Scoreboard> boards = new HashMap<>();
    private final MiniMessage mini = MiniMessage.miniMessage();

    public ScoreboardManager(Main plugin) {
        this.plugin = plugin;
    }

    public void setupPlayer(Player player) {
        if (Bukkit.getScoreboardManager() == null) {
            return;
        }

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = board.registerNewObjective(
                "xdgens",
                "dummy",
                mini.deserialize("<gradient:#a18cd1:#fbc2eb><bold>✦ xdGens ✦</bold></gradient>")
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        createLine(board, objective, "blank_top", "§0", 10);

        createLine(board, objective, "money_title", "§1", 9);
        createLine(board, objective, "money_value", "§2", 8);

        createLine(board, objective, "tokens_title", "§3", 7);
        createLine(board, objective, "tokens_value", "§4", 6);

        createLine(board, objective, "level_title", "§5", 5);
        createLine(board, objective, "level_value", "§6", 4);

        createLine(board, objective, "prestige_title", "§7", 3);
        createLine(board, objective, "prestige_value", "§8", 2);

        createLine(board, objective, "blank_bottom", "§9", 1);
        createLine(board, objective, "footer", "§a", 0);

        boards.put(player.getUniqueId(), board);
        player.setScoreboard(board);

        update(player);
    }

    public void update(Player player) {
        Scoreboard board = boards.get(player.getUniqueId());
        if (board == null) {
            return;
        }

        CurrencyManager currency = plugin.getCurrencyManager();
        ProgressionManager progression = plugin.getProgressionManager();

        setPrefix(board, "blank_top", "<dark_gray> </dark_gray>");

        setPrefix(board, "money_title", "<gray>Money</gray>");
        setPrefix(board, "money_value",
                "<gradient:#f6d365:#fda085>" + NumberUtil.format(currency.getMoney(player)) + "$</gradient>");

        setPrefix(board, "tokens_title", "<gray>Tokens</gray>");
        setPrefix(board, "tokens_value",
                "<gradient:#7afcff:#00c2ff>" + NumberUtil.format(currency.getTokens(player)) + "</gradient>");

        setPrefix(board, "level_title", "<gray>Level</gray>");
        setPrefix(board, "level_value",
                "<gradient:#7afcff:#00c2ff>" + progression.getLevel(player)
                        + "/" + progression.getMaxLevelForPlayer(player) + "</gradient>");

        setPrefix(board, "prestige_title", "<gray>Prestige</gray>");
        setPrefix(board, "prestige_value",
                "<gradient:#a18cd1:#fbc2eb>" + progression.getPrestige(player) + "</gradient>");

        setPrefix(board, "blank_bottom", "<dark_gray>  </dark_gray>");
        setPrefix(board, "footer", "<dark_gray>play.xdgens</dark_gray>");
    }

    public void removePlayer(Player player) {
        boards.remove(player.getUniqueId());

        if (Bukkit.getScoreboardManager() != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    private void createLine(Scoreboard board, Objective objective, String teamName, String entry, int score) {
        Team team = board.getTeam(teamName);
        if (team == null) {
            team = board.registerNewTeam(teamName);
        }

        if (!team.hasEntry(entry)) {
            team.addEntry(entry);
        }

        objective.getScore(entry).setScore(score);
    }

    private void setPrefix(Scoreboard board, String teamName, String text) {
        Team team = board.getTeam(teamName);
        if (team == null) {
            return;
        }

        team.prefix(MiniMessage.miniMessage().deserialize(text));
    }
}