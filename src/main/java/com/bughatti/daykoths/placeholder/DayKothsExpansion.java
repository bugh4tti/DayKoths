package com.bughatti.daykoths.placeholder;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.ScheduleUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DayKothsExpansion extends PlaceholderExpansion {

    private final DayKoths plugin;

    public DayKothsExpansion(DayKoths plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() { return "daykoths"; }

    @Override
    public String getAuthor() { return "Bughatti"; }

    @Override
    public String getVersion() { return plugin.getDescription().getVersion(); }

    @Override
    public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        for (Koth koth : plugin.getKothManager().getAll()) {
            String prefix = koth.getName() + "_";
            if (!params.startsWith(prefix)) continue;
            String rest = params.substring(prefix.length());

            if (rest.equals("next_start")) {
                return ScheduleUtil.getNextStart(koth);
            }

            if (rest.startsWith("top_player_")) {
                String[] parts = rest.split("_");
                if (parts.length >= 4) {
                    int index;
                    try { index = Integer.parseInt(parts[2]); } catch (NumberFormatException e) { return ""; }
                    String field = parts[3];

                    List<Map.Entry<UUID, Integer>> top = koth.getScoreProgress().entrySet().stream()
                            .sorted((a, b) -> b.getValue() - a.getValue())
                            .collect(Collectors.toList());

                    if (top.size() < index) {
                        return field.equals("name") ? "---" : "0";
                    }
                    Map.Entry<UUID, Integer> entry = top.get(index - 1);
                    if (field.equals("name")) {
                        OfflinePlayer op = Bukkit.getOfflinePlayer(entry.getKey());
                        return op.getName() == null ? "---" : op.getName();
                    } else if (field.equals("amount")) {
                        return String.valueOf(entry.getValue());
                    }
                }
            }
        }
        return "";
    }
                }
