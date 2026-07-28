package com.bughatti.daykoths.manager;

import com.bughatti.daykoths.DayKoths;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

public class StatsManager {

    private final DayKoths plugin;
    private final Map<UUID, Integer> wins = new HashMap<>();

    public StatsManager(DayKoths plugin) {
        this.plugin = plugin;
        load();
    }

    public void addWin(UUID playerId) {
        wins.merge(playerId, 1, Integer::sum);
        save();
    }

    public List<Map.Entry<UUID, Integer>> topWins(int limit) {
        return wins.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void load() {
        wins.clear();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("stats");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            try {
                wins.put(UUID.fromString(key), sec.getInt(key));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save() {
        plugin.getConfig().set("stats", null);
        for (Map.Entry<UUID, Integer> e : wins.entrySet()) {
            plugin.getConfig().set("stats." + e.getKey(), e.getValue());
        }
        plugin.saveConfig();
    }
  }
