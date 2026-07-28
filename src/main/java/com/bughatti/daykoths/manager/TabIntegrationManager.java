package com.bughatti.daykoths.manager;

import com.bughatti.daykoths.DayKoths;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabIntegrationManager {

    private final DayKoths plugin;
    private boolean tabDisabled = false;

    public TabIntegrationManager(DayKoths plugin) {
        this.plugin = plugin;
    }

    private boolean shouldManage() {
        return plugin.getConfig().getBoolean("scoreboard.enabled", false)
                && plugin.getConfig().getBoolean("scoreboard.auto-disable-tab", true)
                && Bukkit.getPluginManager().getPlugin("TAB") != null;
    }

    public void onKothStateChanged(boolean anyKothRunning) {
        if (!shouldManage()) return;

        if (anyKothRunning && !tabDisabled) {
            tabDisabled = true;
            for (Player p : Bukkit.getOnlinePlayers()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab scoreboard off " + p.getName() + " -s");
            }
        } else if (!anyKothRunning && tabDisabled) {
            tabDisabled = false;
            for (Player p : Bukkit.getOnlinePlayers()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab scoreboard on " + p.getName() + " -s");
            }
        }
    }

    public void onPlayerJoin(Player player) {
        if (!shouldManage()) return;
        if (tabDisabled) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab scoreboard off " + player.getName() + " -s");
        }
    }
                 }
