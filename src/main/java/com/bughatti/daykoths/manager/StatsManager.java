package com.bughatti.daykoths.manager;

import com.bughatti.daykoths.DayKoths;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

public class StatsManager {

    private final DayKoths plugin;
    private final Map<UUID, Integer> wins = new HashMap<>();
    private final Map<String, Map<UUID, Integer>> perKothWins = new HashMap<>();

    public StatsManager(DayKoths plugin) {
        this.plugin = plugin;
        load();
    }

    public void addWin(UUID playerId, String kothName) {
        wins.merge(playerId, 1, Integer::sum);
        perKothWins.computeIfAbsent(kothName.toLowerCase(), k -> new HashMap<>()).merge(playerId, 1, Integer::sum);
        save();
    }

    public List<Map.Entry<UUID, Integer>> topWins(int limit) {
        return wins.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Map.Entry<UUID, Integer> topWinnerForKoth(String kothName) {
        Map<UUID, Integer> map = perKothWins.get(kothName.toLowerCase());
        if (map == null || map.isEmpty()) return null;
        return map.entrySet().stream()
                .max((a, b) -> a.getValue() - b.getValue())
                .orElse(null);
    }

    public void load() {
        wins.clear();
        perKothWins.clear();

        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("stats");
        if (sec == null) return;

        for (String key : sec.getKeys(false)) {
            if (key.equals("per-koth")) continue;
            try {
                wins.put(UUID.fromString(key), sec.getInt(key));
            } catch (IllegalArgumentException ignored) {}
        }

        ConfigurationSection perKothSec = sec.getConfigurationSection("per-koth");
        if (perKothSec != null) {
            for (String kothKey : perKothSec.getKeys(false)) {
                ConfigurationSection kSec = perKothSec.getConfigurationSection(kothKey);
                Map<UUID, Integer> map = new HashMap<>();
                if (kSec != null) {
                    for (String uuidKey : kSec.getKeys(false)) {
                        try {
                            map.put(UUID.fromString(uuidKey), kSec.getInt(uuidKey));
                        } catch (IllegalArgumentException ignored) {}
                    }
                }
                perKothWins.put(kothKey.toLowerCase(), map);
            }
        }
    }

    public void save() {
        plugin.getConfig().set("stats", null);
        for (Map.Entry<UUID, Integer> e : wins.entrySet()) {
            plugin.getConfig().set("stats." + e.getKey(), e.getValue());
        }
        for (Map.Entry<String, Map<UUID, Integer>> kEntry : perKothWins.entrySet()) {
            for (Map.Entry<UUID, Integer> e : kEntry.getValue().entrySet()) {
                plugin.getConfig().set("stats.per-koth." + kEntry.getKey() + "." + e.getKey(), e.getValue());
            }
        }
        plugin.saveConfig();
    }
            }
