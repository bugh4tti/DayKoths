package com.bughatti.daykoths.manager;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.util.HexUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public class StreakManager {

    private final DayKoths plugin;
    private UUID currentStreakPlayer;
    private int currentStreakCount;

    public StreakManager(DayKoths plugin) {
        this.plugin = plugin;
        load();
    }

    public void recordWin(UUID winnerId) {
        if (winnerId.equals(currentStreakPlayer)) {
            currentStreakCount++;
        } else {
            currentStreakPlayer = winnerId;
            currentStreakCount = 1;
        }
        save();
        applyRewardIfAny(winnerId, currentStreakCount);
        announceIfNeeded(winnerId, currentStreakCount);
    }

    private void applyRewardIfAny(UUID winnerId, int streak) {
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("streak.rewards." + streak);
        if (sec == null) return;
        OfflinePlayer op = Bukkit.getOfflinePlayer(winnerId);
        String name = op.getName();
        if (name == null) return;
        for (String cmd : sec.getStringList("commands")) {
            String finalCmd = cmd.replace("%player%", name);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
        }
    }

    private void announceIfNeeded(UUID winnerId, int streak) {
        if (!plugin.getConfig().getBoolean("streak.enabled", true)) return;
        int minAnnounce = plugin.getConfig().getInt("streak.min-streak-announce", 2);
        if (streak < minAnnounce) return;

        OfflinePlayer op = Bukkit.getOfflinePlayer(winnerId);
        String name = op.getName() == null ? "---" : op.getName();
        String raw = plugin.getConfig().getString("streak.broadcast", "")
                .replace("%prefix%", plugin.getConfig().getString("plugin.prefix", ""))
                .replace("%player%", name)
                .replace("%streak%", String.valueOf(streak));
        Bukkit.broadcastMessage(HexUtil.colorize(raw));
    }

    public int getCurrentStreakCount() { return currentStreakCount; }
    public UUID getCurrentStreakPlayer() { return currentStreakPlayer; }

    public void load() {
        String uuidStr = plugin.getConfig().getString("streak-state.player", null);
        currentStreakPlayer = uuidStr == null ? null : safeUUID(uuidStr);
        currentStreakCount = plugin.getConfig().getInt("streak-state.count", 0);
    }

    private UUID safeUUID(String s) {
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void save() {
        plugin.getConfig().set("streak-state.player", currentStreakPlayer == null ? null : currentStreakPlayer.toString());
        plugin.getConfig().set("streak-state.count", currentStreakCount);
        plugin.saveConfig();
    }
    }
