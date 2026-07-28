package com.bughatti.daykoths.tasks;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import org.bukkit.scheduler.BukkitRunnable;

public class ArsenalTask extends BukkitRunnable {

    private final DayKoths plugin;

    public ArsenalTask(DayKoths plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            tick();
        } catch (Exception ex) {
            plugin.getLogger().warning("Error en ArsenalTask (no se detiene el plugin): " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void tick() {
        long intervalMinutes = Math.max(1, plugin.getConfig().getLong("arsenal.event-interval-minutes", 6));

        for (Koth koth : plugin.getKothManager().getAll()) {
            if (!koth.isRunning() || !koth.isArsenalEnabled() || !koth.hasBothPositions()) continue;
            if (koth.getPlayersInside().isEmpty()) continue;

            long elapsedMinutes = (System.currentTimeMillis() - koth.getStartedAt()) / 60000L;
            if (elapsedMinutes <= 0) continue;
            if (elapsedMinutes % intervalMinutes != 0) continue;
            if (koth.getLastArsenalTriggerMinute() == elapsedMinutes) continue;

            koth.setLastArsenalTriggerMinute(elapsedMinutes);
            plugin.getArsenalManager().triggerRandomEvent(koth);
        }
    }
}
