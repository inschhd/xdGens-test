package de.louis.xdGens.command;

import de.louis.xdGens.main.Main;
import de.louis.xdGens.util.MessageUtil;
import de.louis.xdGens.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class XdAdminCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public XdAdminCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("xdgens.admin")) {
            MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>You do not have permission.</red>");
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "info" -> handleInfo(sender, args);
            case "reset" -> handleReset(sender, args);
            case "money", "tokens", "xp", "level", "prestige" -> handleModify(sender, sub, args);
            default -> sendUsage(sender);
        }

        return true;
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Usage: /xdadmin info <player></red>");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Player not found.</red>");
            return;
        }

        double money = plugin.getCurrencyManager().getMoney(target);
        long tokens = plugin.getCurrencyManager().getTokens(target);
        int level = plugin.getProgressionManager().getLevel(target);
        int prestige = plugin.getProgressionManager().getPrestige(target);
        double xp = plugin.getProgressionManager().getXp(target);
        int requiredXp = plugin.getProgressionManager().getRequiredXp(target);

        MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <gray>Info for <yellow>" + target.getName() + "</yellow>:</gray>");
        MessageUtil.sendRaw(sender, "<gray>Money:</gray> <green>$" + NumberUtil.format(money) + "</green>");
        MessageUtil.sendRaw(sender, "<gray>Tokens:</gray> <gold>" + NumberUtil.format(tokens) + "</gold>");
        MessageUtil.sendRaw(sender, "<gray>Level:</gray> <aqua>" + level + "</aqua>");
        MessageUtil.sendRaw(sender, "<gray>XP:</gray> <aqua>" + NumberUtil.format(xp) + " / " + NumberUtil.format(requiredXp) + "</aqua>");
        MessageUtil.sendRaw(sender, "<gray>Prestige:</gray> <gradient:#f6d365:#fda085>" + prestige + "</gradient>");
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Usage: /xdadmin reset <player></red>");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Player not found.</red>");
            return;
        }

        plugin.getCurrencyManager().setMoney(target, 0.0);
        plugin.getCurrencyManager().setTokens(target, 0L);

        setProgressValue(target, "level", 1);
        setProgressValue(target, "prestige", 0);
        setProgressValue(target, "xp", 0.0);

        plugin.getCurrencyManager().savePlayer(target);
        plugin.getProgressionManager().savePlayer(target);
        plugin.getProgressionManager().updateDisplays(target);

        MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <green>Reset data for <yellow>" + target.getName() + "</yellow>.</green>");
        MessageUtil.sendRaw(target, MessageUtil.PREFIX + " <red>Your progression data has been reset by an admin.</red>");
    }

    private void handleModify(CommandSender sender, String type, String[] args) {
        if (args.length < 4) {
            MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Usage: /xdadmin " + type + " <set|add|remove> <player> <amount></red>");
            return;
        }

        String action = args[1].toLowerCase();
        Player target = Bukkit.getPlayerExact(args[2]);

        if (target == null) {
            MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Player not found.</red>");
            return;
        }

        double amount;
        try {
            amount = parseAmount(args[3]);
        } catch (NumberFormatException e) {
            MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Invalid amount. Examples: 1000, 5k, 2.5m, 10t</red>");
            return;
        }

        if (amount < 0) {
            MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Amount must be positive.</red>");
            return;
        }

        switch (type) {
            case "money" -> handleMoney(sender, target, action, amount);
            case "tokens" -> handleTokens(sender, target, action, Math.round(amount));
            case "xp" -> handleXp(sender, target, action, amount);
            case "level" -> handleLevel(sender, target, action, (int) Math.round(amount));
            case "prestige" -> handlePrestige(sender, target, action, (int) Math.round(amount));
        }
    }

    private void handleMoney(CommandSender sender, Player target, String action, double amount) {
        switch (action) {
            case "set" -> plugin.getCurrencyManager().setMoney(target, amount);
            case "add" -> plugin.getCurrencyManager().addMoney(target, amount);
            case "remove" -> {
                if (!plugin.getCurrencyManager().removeMoney(target, amount)) {
                    MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Player does not have enough money.</red>");
                    return;
                }
            }
            default -> {
                MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Action must be set, add or remove.</red>");
                return;
            }
        }

        plugin.getCurrencyManager().savePlayer(target);

        MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <green>Updated money for <yellow>" + target.getName()
                + "</yellow> to <green>$" + NumberUtil.format(plugin.getCurrencyManager().getMoney(target)) + "</green>.</green>");
    }

    private void handleTokens(CommandSender sender, Player target, String action, long amount) {
        switch (action) {
            case "set" -> plugin.getCurrencyManager().setTokens(target, amount);
            case "add" -> plugin.getCurrencyManager().addTokens(target, amount);
            case "remove" -> {
                if (!plugin.getCurrencyManager().removeTokens(target, amount)) {
                    MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Player does not have enough tokens.</red>");
                    return;
                }
            }
            default -> {
                MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Action must be set, add or remove.</red>");
                return;
            }
        }

        plugin.getCurrencyManager().savePlayer(target);

        MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <green>Updated tokens for <yellow>" + target.getName()
                + "</yellow> to <gold>" + NumberUtil.format(plugin.getCurrencyManager().getTokens(target)) + "</gold>.</green>");
    }

    private void handleXp(CommandSender sender, Player target, String action, double amount) {
        double current = plugin.getProgressionManager().getXp(target);
        double updated;

        switch (action) {
            case "set" -> updated = amount;
            case "add" -> updated = current + amount;
            case "remove" -> updated = Math.max(0.0, current - amount);
            default -> {
                MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Action must be set, add or remove.</red>");
                return;
            }
        }

        setProgressValue(target, "xp", updated);
        plugin.getProgressionManager().savePlayer(target);
        plugin.getProgressionManager().updateDisplays(target);

        MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <green>Updated XP for <yellow>" + target.getName()
                + "</yellow> to <aqua>" + NumberUtil.format(updated) + "</aqua>.</green>");
    }

    private void handleLevel(CommandSender sender, Player target, String action, int amount) {
        int current = plugin.getProgressionManager().getLevel(target);
        int updated;

        switch (action) {
            case "set" -> updated = Math.max(1, amount);
            case "add" -> updated = current + amount;
            case "remove" -> updated = Math.max(1, current - amount);
            default -> {
                MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Action must be set, add or remove.</red>");
                return;
            }
        }

        updated = Math.min(plugin.getProgressionManager().getMaxLevel(), updated);

        setProgressValue(target, "level", updated);

        if (updated >= plugin.getProgressionManager().getMaxLevel()) {
            setProgressValue(target, "xp", (double) plugin.getProgressionManager().getRequiredXp(target));
        } else {
            double currentXp = plugin.getProgressionManager().getXp(target);
            int required = plugin.getProgressionManager().getRequiredXp(target);
            if (currentXp > required) {
                setProgressValue(target, "xp", (double) required);
            }
        }

        plugin.getProgressionManager().savePlayer(target);
        plugin.getProgressionManager().updateDisplays(target);

        MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <green>Updated level for <yellow>" + target.getName()
                + "</yellow> to <aqua>" + updated + "</aqua>.</green>");
    }

    private void handlePrestige(CommandSender sender, Player target, String action, int amount) {
        int current = plugin.getProgressionManager().getPrestige(target);
        int updated;

        switch (action) {
            case "set" -> updated = Math.max(0, amount);
            case "add" -> updated = current + amount;
            case "remove" -> updated = Math.max(0, current - amount);
            default -> {
                MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <red>Action must be set, add or remove.</red>");
                return;
            }
        }

        setProgressValue(target, "prestige", updated);
        plugin.getProgressionManager().savePlayer(target);
        plugin.getProgressionManager().updateDisplays(target);

        MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <green>Updated prestige for <yellow>" + target.getName()
                + "</yellow> to <gradient:#f6d365:#fda085>" + updated + "</gradient>.</green>");
    }

    private double parseAmount(String input) throws NumberFormatException {
        if (input == null || input.isBlank()) {
            throw new NumberFormatException("Empty amount");
        }

        String normalized = input.trim().toLowerCase().replace(",", ".");

        double multiplier = 1.0;
        char last = normalized.charAt(normalized.length() - 1);

        if (Character.isLetter(last)) {
            switch (last) {
                case 'k' -> multiplier = 1_000D;
                case 'm' -> multiplier = 1_000_000D;
                case 'b' -> multiplier = 1_000_000_000D;
                case 't' -> multiplier = 1_000_000_000_000D;
                default -> throw new NumberFormatException("Unknown suffix: " + last);
            }

            normalized = normalized.substring(0, normalized.length() - 1).trim();
            if (normalized.isEmpty()) {
                throw new NumberFormatException("Missing numeric part");
            }
        }

        return Double.parseDouble(normalized) * multiplier;
    }

    private void setProgressValue(Player player, String fieldName, Object value) {
        try {
            Field dataField = plugin.getProgressionManager().getClass().getDeclaredField("data");
            dataField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<UUID, Object> dataMap = (Map<UUID, Object>) dataField.get(plugin.getProgressionManager());

            Object progress = dataMap.get(player.getUniqueId());
            if (progress == null) {
                for (Class<?> innerClass : plugin.getProgressionManager().getClass().getDeclaredClasses()) {
                    if (innerClass.getSimpleName().equals("PlayerProgress")) {
                        var constructor = innerClass.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        progress = constructor.newInstance();
                        dataMap.put(player.getUniqueId(), progress);
                        break;
                    }
                }
            }

            if (progress == null) {
                throw new IllegalStateException("Could not create PlayerProgress instance");
            }

            Field valueField = progress.getClass().getDeclaredField(fieldName);
            valueField.setAccessible(true);
            valueField.set(progress, value);
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtil.sendRaw(player, MessageUtil.PREFIX + " <red>Internal admin command error.</red>");
        }
    }

    private void sendUsage(CommandSender sender) {
        MessageUtil.sendRaw(sender, MessageUtil.PREFIX + " <gray>Admin commands:</gray>");
        MessageUtil.sendRaw(sender, "<yellow>/xdadmin info <player></yellow>");
        MessageUtil.sendRaw(sender, "<yellow>/xdadmin reset <player></yellow>");
        MessageUtil.sendRaw(sender, "<yellow>/xdadmin money <set|add|remove> <player> <amount></yellow>");
        MessageUtil.sendRaw(sender, "<yellow>/xdadmin tokens <set|add|remove> <player> <amount></yellow>");
        MessageUtil.sendRaw(sender, "<yellow>/xdadmin xp <set|add|remove> <player> <amount></yellow>");
        MessageUtil.sendRaw(sender, "<yellow>/xdadmin level <set|add|remove> <player> <amount></yellow>");
        MessageUtil.sendRaw(sender, "<yellow>/xdadmin prestige <set|add|remove> <player> <amount></yellow>");
        MessageUtil.sendRaw(sender, "<gray>Examples: <yellow>5k</yellow>, <yellow>2.5m</yellow>, <yellow>10t</yellow></gray>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("xdgens.admin")) {
            return List.of();
        }

        if (args.length == 1) {
            return filter(List.of("info", "reset", "money", "tokens", "xp", "level", "prestige"), args[0]);
        }

        if (args.length == 2 && !args[0].equalsIgnoreCase("info") && !args[0].equalsIgnoreCase("reset")) {
            return filter(List.of("set", "add", "remove"), args[1]);
        }

        if ((args.length == 2 && (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("reset")))
                || (args.length == 3 && !args[0].equalsIgnoreCase("info") && !args[0].equalsIgnoreCase("reset"))) {
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                names.add(player.getName());
            }
            return filter(names, args[args.length - 1]);
        }

        if (args.length == 4) {
            return filter(List.of("1k", "10k", "100k", "1m", "10m", "1b", "10t"), args[3]);
        }

        return List.of();
    }

    private List<String> filter(List<String> input, String current) {
        String lower = current.toLowerCase();
        return input.stream()
                .filter(s -> s.toLowerCase().startsWith(lower))
                .toList();
    }
}