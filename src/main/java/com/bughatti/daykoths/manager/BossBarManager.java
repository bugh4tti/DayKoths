package com.bughatti.daykoths.manager;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.HexUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BossBarManager {

    private final DayKoths plugin;
    private final Map<String, BossBar> bars = new HashMap<>();
    private final boolean papiEnabled;

    public BossBarManager(DayKoths plugin) {
        this.plugin = plugin;
        this.papiEnabled = plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public void update(Koth koth, String timeLeft) {
        if (!plugin.getConfig().getBoolean("bossbar.enabled", false)) {
            remove(koth);
            return;
        }

        BossBar bar = bars.get(koth.getName().toLowerCase());
        if (bar == null) {
            BarColor color;
            try { color = BarColor.valueOf(plugin.getConfig().getString("bossbar.color", "BLUE")); } catch (Exception e) { color = BarColor.BLUE; }
            BarStyle style;
            try { style = BarStyle.valueOf(plugin.getConfig().getString("bossbar.style", "SOLID")); } catch (Exception e) { style = BarStyle.SOLID; }
            bar = Bukkit.createBossBar(" ", color, style);
            bars.put(koth.getName().toLowerCase(), bar);
        }

        String rawTitle = plugin.getConfig().getString("bossbar.title", "&b%koth%")
                .replace("%koth%", koth.getName())
                .replace("%time_left%", timeLeft);

        double progress = 1.0;
        if (koth.getDurationMinutes() > 0) {
            long elapsedSec = (System.currentTimeMillis() - koth.getStartedAt()) / 1000L;
            long totalSec = koth.getDurationMinutes() * 60L;
            progress = Math.max(0.0, Math.min(1.0, 1.0 - ((double) elapsedSec / totalSec)));
        }
        bar.setProgress(progress);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!bar.getPlayers().contains(p)) bar.addPlayer(p);
        }

        if (papiEnabled && !bar.getPlayers().isEmpty()) {
            Player anyPlayer = bar.getPlayers().get(0);
            String parsed = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(anyPlayer, rawTitle);
            bar.setTitle(HexUtil.colorize(parsed));
        } else {
            bar.setTitle(HexUtil.colorize(rawTitle));
        }
    }

    public void remove(Koth koth) {
        BossBar bar = bars.remove(koth.getName().toLowerCase());
        if (bar != null) bar.removeAll();
    }

    public void removeAll() {
        for (BossBar bar : bars.values()) bar.removeAll();
        bars.clear();
    }
}
