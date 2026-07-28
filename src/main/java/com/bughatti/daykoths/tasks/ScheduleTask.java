package com.bughatti.daykoths.tasks;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.HexUtil;
import com.bughatti.daykoths.util.ScheduleUtil;
import com.bughatti.daykoths.util.TitleUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ScheduleTask extends BukkitRunnable {

    private final DayKoths plugin;

    public ScheduleTask(DayKoths plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            tick();
        } catch (Exception ex) {
            plugin.getLogger().warning("Error en ScheduleTask (no se detiene el plugin): " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void tick() {
        String currentKey = ScheduleUtil.currentKey();

        for (Koth koth : plugin.getKothManager().getAll()) {
            if (koth.isRunning()) continue;
            if (!koth.hasBothPositions()) continue;
            if (!ScheduleUtil.isActiveNow(koth)) continue;
            if (currentKey.equals(koth.getLastScheduleTriggerKey())) continue;

            koth.setLastScheduleTriggerKey(currentKey);
            koth.setRunning(true);
            plugin.getKothManager().save();

            String raw = plugin.getConfig().getString("messages.koth-started", "")
                    .replace("%prefix%", plugin.getConfig().getString("plugin.prefix", ""))
                    .replace("%koth%", koth.getName());
            Bukkit.broadcastMessage(HexUtil.colorize(raw));
            TitleUtil.sendStartTitle(plugin, koth);
        }
    }
}
