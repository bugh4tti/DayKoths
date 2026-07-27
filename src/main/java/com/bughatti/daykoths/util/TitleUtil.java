package com.bughatti.daykoths.util;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleUtil {

    public static void sendStartTitle(DayKoths plugin, Koth koth) {
        String title = HexUtil.colorize(plugin.getConfig().getString("titles.start-title", ""));
        String subtitle = HexUtil.colorize(plugin.getConfig().getString("titles.start-subtitle", "").replace("%koth%", koth.getName()));
        int fadeIn = plugin.getConfig().getInt("titles.fade-in", 10);
        int stay = plugin.getConfig().getInt("titles.stay", 40);
        int fadeOut = plugin.getConfig().getInt("titles.fade-out", 10);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void sendWinTitle(DayKoths plugin, Player winner, Koth koth) {
        String title = HexUtil.colorize(plugin.getConfig().getString("titles.win-title", ""));
        String subtitle = HexUtil.colorize(plugin.getConfig().getString("titles.win-subtitle", "")
                .replace("%player%", winner.getName())
                .replace("%koth%", koth.getName()));
        int fadeIn = plugin.getConfig().getInt("titles.fade-in", 10);
        int stay = plugin.getConfig().getInt("titles.stay", 40);
        int fadeOut = plugin.getConfig().getInt("titles.fade-out", 10);
        winner.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
          }
