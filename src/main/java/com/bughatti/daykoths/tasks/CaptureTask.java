package com.bughatti.daykoths.tasks;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.CaptureMode;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.HexUtil;
import com.bughatti.daykoths.util.TimeUtil;
import com.bughatti.daykoths.util.TitleUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CaptureTask extends BukkitRunnable {

    private final DayKoths plugin;
    private int tickCounter = 0;

    public CaptureTask(DayKoths plugin) {
        this.plugin = plugin;
    }

    private String timeLeft(Koth koth) {
        if (koth.getDurationMinutes() <= 0) return "--:--";
        long elapsedSec = (System.currentTimeMillis() - koth.getStartedAt()) / 1000L;
        long totalSec = koth.getDurationMinutes() * 60L;
        return TimeUtil.formatSeconds(totalSec - elapsedSec);
    }

    @Override
    public void run() {
        try {
            tick();
        } catch (Exception ex) {
            plugin.getLogger().warning("Error en CaptureTask (no se detiene el plugin): " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void tick() {
        tickCounter++;
        int interval = Math.max(1, plugin.getConfig().getInt("messages.capturing-interval-seconds", 10));
        String prefix = plugin.getConfig().getString("plugin.prefix", "");
        String capturingMsgRaw = plugin.getConfig().getString("messages.capturing", "");

        String enterTitle = HexUtil.colorize(plugin.getConfig().getString("titles.enter-title", ""));
        String enterSubtitle = HexUtil.colorize(plugin.getConfig().getString("titles.enter-subtitle", ""));
        String leaveTitle = HexUtil.colorize(plugin.getConfig().getString("titles.leave-title", ""));
        String leaveSubtitle = HexUtil.colorize(plugin.getConfig().getString("titles.leave-subtitle", ""));
        int fadeIn = plugin.getConfig().getInt("titles.fade-in", 10);
        int stay = plugin.getConfig().getInt("titles.stay", 40);
        int fadeOut = plugin.getConfig().getInt("titles.fade-out", 10);

        boolean anyRunning = false;

        for (Koth koth : plugin.getKothManager().getAll()) {
            if (!koth.isRunning() || !koth.hasBothPositions()) continue;
            anyRunning = true;

            String timeLeftStr = timeLeft(koth);
            plugin.getBossBarManager().update(koth, timeLeftStr);
            plugin.getScoreboardManager().update(koth, timeLeftStr);

            long elapsedMin = (System.currentTimeMillis() - koth.getStartedAt()) / 60000L;
            if (koth.getDurationMinutes() > 0 && elapsedMin >= koth.getDurationMinutes()) {
                finishByDuration(koth);
                continue;
            }

            Set<UUID> currentlyInside = new HashSet<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                boolean inside = koth.isInside(player.getLocation());

                if (inside) {
                    currentlyInside.add(player.getUniqueId());

                    if (!koth.getPlayersInside().contains(player.getUniqueId())) {
                        player.sendTitle(enterTitle, enterSubtitle, fadeIn, stay, fadeOut);
                    }

                    if (tickCounter % interval == 0) {
                        String finalMsg = HexUtil.colorize(capturingMsgRaw
                                .replace("%prefix%", prefix)
                                .replace("%player%", player.getName())
                                .replace("%koth%", koth.getName())
                                .replace("%time_left%", timeLeftStr));
                        Bukkit.broadcastMessage(finalMsg);
                    }

                    if (koth.getMode() == CaptureMode.SCORE) {
                        koth.getScoreProgress().merge(player.getUniqueId(), 1, Integer::sum);
                    } else {
                        if (koth.getCurrentCapturer() == null || koth.getCurrentCapturer().equals(player.getUniqueId())) {
                            koth.setCurrentCapturer(player.getUniqueId());
                            koth.setCurrentCaptureSeconds(koth.getCurrentCaptureSeconds() + 1);
                            if (koth.getCurrentCaptureSeconds() >= koth.getRequiredSeconds()) {
                                finishByCapture(koth, player);
                            }
                        }
                    }
                } else if (koth.getPlayersInside().contains(player.getUniqueId())) {
                    player.sendTitle(leaveTitle, leaveSubtitle, fadeIn, stay, fadeOut);
                    if (koth.getMode() == CaptureMode.TIME && player.getUniqueId().equals(koth.getCurrentCapturer())) {
                        koth.setCurrentCapturer(null);
                        koth.setCurrentCaptureSeconds(0);
                    }
                }
            }

            koth.getPlayersInside().clear();
            koth.getPlayersInside().addAll(currentlyInside);
        }

        if (!anyRunning) {
            plugin.getScoreboardManager().clearAll();
        }
        plugin.getTabIntegrationManager().onKothStateChanged(anyRunning);
    }

    private void finishByCapture(Koth koth, Player winner) {
        koth.setRunning(false);
        plugin.getBossBarManager().remove(koth);
        String msg = HexUtil.colorize(plugin.getConfig().getString("messages.koth-won", "")
                .replace("%prefix%", plugin.getConfig().getString("plugin.prefix", ""))
                .replace("%player%", winner.getName())
                .replace("%koth%", koth.getName()));
        Bukkit.broadcastMessage(msg);
        TitleUtil.sendWinTitle(plugin, winner, koth);
        plugin.getStatsManager().addWin(winner.getUniqueId(), koth.getName());
        plugin.getStreakManager().recordWin(winner.getUniqueId());
        giveReward(koth, winner);
        plugin.getKothManager().save();
    }

    private void finishByDuration(Koth koth) {
        koth.setRunning(false);
        plugin.getBossBarManager().remove(koth);
        if (koth.getMode() == CaptureMode.SCORE && !koth.getScoreProgress().isEmpty()) {
            List<Map.Entry<UUID, Integer>> sorted = koth.getScoreProgress().entrySet().stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .collect(Collectors.toList());

            UUID winnerId = sorted.get(0).getKey();
            Player winner = Bukkit.getPlayer(winnerId);
            if (winner != null) {
                String msg = HexUtil.colorize(plugin.getConfig().getString("messages.koth-won", "")
                        .replace("%prefix%", plugin.getConfig().getString("plugin.prefix", ""))
                        .replace("%player%", winner.getName())
                        .replace("%koth%", koth.getName()));
                Bukkit.broadcastMessage(msg);
                TitleUtil.sendWinTitle(plugin, winner, koth);
                plugin.getStatsManager().addWin(winner.getUniqueId(), koth.getName());
                plugin.getStreakManager().recordWin(winner.getUniqueId());
                giveReward(koth, winner);

                if (sorted.size() >= 2) {
                    announceTop3(sorted);
                }
            }
        } else {
            Bukkit.broadcastMessage(HexUtil.colorize(plugin.getConfig().getString("messages.koth-stopped", "")
                    .replace("%prefix%", plugin.getConfig().getString("plugin.prefix", ""))
                    .replace("%koth%", koth.getName())));
        }
        plugin.getKothManager().save();
    }

    private void announceTop3(List<Map.Entry<UUID, Integer>> sorted) {
        Map.Entry<UUID, Integer> second = sorted.get(1);
        String secondName = nameOf(second.getKey());
        String thirdName = sorted.size() >= 3 ? nameOf(sorted.get(2).getKey()) : "---";
        String thirdAmount = sorted.size() >= 3 ? String.valueOf(sorted.get(2).getValue()) : "0";

        String raw = plugin.getConfig().getString("messages.koth-top3", "")
                .replace("%prefix%", plugin.getConfig().getString("plugin.prefix", ""))
                .replace("%second_player%", secondName)
                .replace("%second_amount%", String.valueOf(second.getValue()))
                .replace("%third_player%", thirdName)
                .replace("%third_amount%", thirdAmount);
        Bukkit.broadcastMessage(HexUtil.colorize(raw));
    }

    private String nameOf(UUID id) {
        Player p = Bukkit.getPlayer(id);
        if (p != null) return p.getName();
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        return op.getName() == null ? "---" : op.getName();
    }

    private void giveReward(Koth koth, Player winner) {
        for (org.bukkit.inventory.ItemStack item : koth.getReward()) {
            winner.getInventory().addItem(item.clone());
        }
        for (String cmd : koth.getCommandRewards()) {
            String finalCmd = cmd.replace("%player%", winner.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
        }
    }
            }
