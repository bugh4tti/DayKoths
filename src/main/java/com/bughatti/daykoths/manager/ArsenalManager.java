package com.bughatti.daykoths.manager;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.HexUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ArsenalManager {

    private final DayKoths plugin;
    private final Random random = new Random();

    public ArsenalManager(DayKoths plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabledForKoth(Koth koth, String eventId) {
        Boolean override = koth.getArsenalOverrides().get(eventId);
        return override == null || override;
    }

    public void triggerRandomEvent(Koth koth) {
        ConfigurationSection eventsSec = plugin.getConfig().getConfigurationSection("arsenal.events");
        if (eventsSec == null) return;

        List<String> ids = new ArrayList<>(eventsSec.getKeys(false));
        ids.removeIf(id -> !isEnabledForKoth(koth, id));
        if (ids.isEmpty()) return;

        String chosen = ids.get(random.nextInt(ids.size()));
        applyEvent(koth, chosen, eventsSec.getConfigurationSection(chosen));
    }

    private void applyEvent(Koth koth, String id, ConfigurationSection sec) {
        if (sec == null) return;

        String displayName = HexUtil.colorize(sec.getString("name", id));
        String announceTitle = HexUtil.colorize(plugin.getConfig().getString("arsenal.announce-title", ""));
        int fadeIn = plugin.getConfig().getInt("titles.fade-in", 10);
        int stay = plugin.getConfig().getInt("titles.stay", 40);
        int fadeOut = plugin.getConfig().getInt("titles.fade-out", 10);

        String special = sec.getString("special", "");
        long intervalMs = plugin.getConfig().getLong("arsenal.event-interval-minutes", 6) * 60000L;

        if (special.equalsIgnoreCase("GOLDEN_APPLE_COOLDOWN")) {
            koth.setActiveArsenalEvent(id);
            koth.setActiveArsenalExpiry(System.currentTimeMillis() + intervalMs);
        } else {
            koth.setActiveArsenalEvent(null);
            koth.setActiveArsenalExpiry(0);
            String potionName = sec.getString("potion-effect", null);
            if (potionName != null) {
                PotionEffectType type = PotionEffectType.getByName(potionName);
                if (type != null) {
                    int amplifier = sec.getInt("amplifier", 0);
                    int durationSeconds = sec.getInt("duration-seconds", 30);
                    for (UUID uid : koth.getPlayersInside()) {
                        Player p = Bukkit.getPlayer(uid);
                        if (p != null) p.addPotionEffect(new PotionEffect(type, durationSeconds * 20, amplifier, true, true));
                    }
                }
            }
        }

        for (UUID uid : koth.getPlayersInside()) {
            Player p = Bukkit.getPlayer(uid);
            if (p != null) p.sendTitle(announceTitle, displayName, fadeIn, stay, fadeOut);
        }

        String prefix = plugin.getConfig().getString("plugin.prefix", "");
        Bukkit.broadcastMessage(HexUtil.colorize(prefix + "&d¡Nuevo evento en &f" + koth.getName() + "&d! &f" + displayName));
    }

    public boolean isSpecialActive(Koth koth, String specialId) {
        if (koth.getActiveArsenalEvent() == null) return false;
        if (System.currentTimeMillis() > koth.getActiveArsenalExpiry()) return false;
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("arsenal.events." + koth.getActiveArsenalEvent());
        return sec != null && specialId.equalsIgnoreCase(sec.getString("special", ""));
    }

    public int getGoldenAppleCooldownSeconds(Koth koth) {
        if (koth.getActiveArsenalEvent() == null) return 0;
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("arsenal.events." + koth.getActiveArsenalEvent());
        return sec == null ? 0 : sec.getInt("cooldown-seconds", 10);
    }
  }
