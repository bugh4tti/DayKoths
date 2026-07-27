package com.bughatti.daykoths.manager;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.HexUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ScoreboardManager {

    private final DayKoths plugin;
    private final boolean papiEnabled;

    public ScoreboardManager(DayKoths plugin) {
        this.plugin = plugin;
        this.papiEnabled = plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    private String applyPapi(Player player, String text) {
        if (!papiEnabled) return text;
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
    }

    public void update(Koth koth, String timeLeft) {
        if (!plugin.getConfig().getBoolean("scoreboard.enabled", false)) {
            clearAll();
            return;
        }

        List<String> rawLines = plugin.getConfig().getStringList("scoreboard.lines");

        List<Map.Entry<UUID, Integer>> top = koth.getScoreProgress().entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .collect(Collectors.toList());

        for (Player player : Bukkit.getOnlinePlayers()) {
            String title = applyPapi(player, plugin.getConfig().getString("scoreboard.title", "&b&lDAYKOTHS"))
                    .replace("%koth%", koth.getName())
                    .replace("%time_left%", timeLeft);

            Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective obj = board.registerNewObjective("daykoths", "dummy", HexUtil.colorize(title));
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);

            int score = rawLines.size();
            for (String line : rawLines) {
                String parsed = line.replace("%koth%", koth.getName()).replace("%time_left%", timeLeft);
                for (int i = 1; i <= 3; i++) {
                    String name = "---";
                    String amount = "0";
                    if (top.size() >= i) {
                        UUID id = top.get(i - 1).getKey();
                        String realName = Bukkit.getOfflinePlayer(id).getName();
                        name = realName == null ? "---" : realName;
                        amount = String.valueOf(top.get(i - 1).getValue());
                    }
                    parsed = parsed.replace("%top_player_" + i + "_name%", name);
                    parsed = parsed.replace("%top_player_" + i + "_amount%", amount);
                }
                parsed = applyPapi(player, parsed);
                String colored = HexUtil.colorize(parsed);
                if (colored.length() > 40) colored = colored.substring(0, 40);

                Team team = board.registerNewTeam("line" + score);
                String entry = entryFor(score);
                team.addEntry(entry);
                team.setPrefix(colored);
                obj.getScore(entry).setScore(score);
                score--;
            }

            player.setScoreboard(board);
        }
    }

    private String entryFor(int score) {
        return org.bukkit.ChatColor.values()[Math.min(score, 15) % 16].toString();
    }

    public void clearAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }
                        }
