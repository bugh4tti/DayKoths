package com.bughatti.daykoths.listeners;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.HexUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GoldenAppleCooldownListener implements Listener {

    private final DayKoths plugin;
    private final Map<UUID, Long> lastEatenAt = new HashMap<>();

    public GoldenAppleCooldownListener(DayKoths plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        Material type = e.getItem().getType();
        if (type != Material.GOLDEN_APPLE && type != Material.ENCHANTED_GOLDEN_APPLE) return;

        Player player = e.getPlayer();

        for (Koth koth : plugin.getKothManager().getAll()) {
            if (!koth.isRunning() || !koth.hasBothPositions()) continue;
            if (!koth.isInside(player.getLocation())) continue;
            if (!plugin.getArsenalManager().isSpecialActive(koth, "GOLDEN_APPLE_COOLDOWN")) continue;

            int cooldown = plugin.getArsenalManager().getGoldenAppleCooldownSeconds(koth);
            long now = System.currentTimeMillis();
            long last = lastEatenAt.getOrDefault(player.getUniqueId(), 0L);
            long remainingMs = (last + (cooldown * 1000L)) - now;

            if (remainingMs > 0) {
                e.setCancelled(true);
                String raw = plugin.getConfig().getString("messages.golden-apple-cooldown", "")
                        .replace("%prefix%", plugin.getConfig().getString("plugin.prefix", ""))
                        .replace("%seconds%", String.valueOf((remainingMs / 1000) + 1));
                player.sendMessage(HexUtil.colorize(raw));
            } else {
                lastEatenAt.put(player.getUniqueId(), now);
            }
            return;
        }
    }
                                 }
