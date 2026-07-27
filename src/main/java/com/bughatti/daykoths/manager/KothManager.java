package com.bughatti.daykoths.manager;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.CaptureMode;
import com.bughatti.daykoths.model.Koth;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KothManager {

    private final DayKoths plugin;
    private final Map<String, Koth> koths = new LinkedHashMap<>();

    public KothManager(DayKoths plugin) {
        this.plugin = plugin;
        load();
    }

    public Koth create(String name) {
        Koth koth = new Koth(name);
        koths.put(name.toLowerCase(), koth);
        save();
        return koth;
    }

    public Koth get(String name) {
        return koths.get(name.toLowerCase());
    }

    public boolean exists(String name) {
        return koths.containsKey(name.toLowerCase());
    }

    public Collection<Koth> getAll() {
        return koths.values();
    }

    public void load() {
        koths.clear();
        ConfigurationSection root = plugin.getConfig().getConfigurationSection("koths");
        if (root == null) return;

        for (String name : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(name);
            if (sec == null) continue;

            Koth koth = new Koth(name);
            koth.setMode(CaptureMode.valueOf(sec.getString("mode", "SCORE")));
            koth.setRequiredSeconds(sec.getInt("required-seconds", 60));
            koth.setDurationMinutes(sec.getInt("duration-minutes", 30));
            koth.setKeepInventory(sec.getBoolean("keep-inventory", false));
            koth.setUtilitiesAllowed(sec.getBoolean("utilities-allowed", true));

            if (sec.contains("pos1")) koth.setPos1(deserializeLocation(sec.getConfigurationSection("pos1")));
            if (sec.contains("pos2")) koth.setPos2(deserializeLocation(sec.getConfigurationSection("pos2")));

            List<?> rawReward = sec.getList("reward");
            List<ItemStack> reward = new ArrayList<>();
            if (rawReward != null) {
                for (Object o : rawReward) if (o instanceof ItemStack) reward.add((ItemStack) o);
            }
            koth.setReward(reward);

            ConfigurationSection schedSec = sec.getConfigurationSection("schedules");
            if (schedSec != null) {
                for (String day : schedSec.getKeys(false)) {
                    ConfigurationSection daySec = schedSec.getConfigurationSection(day);
                    Map<Integer, Boolean> hours = new HashMap<>();
                    if (daySec != null) {
                        for (String hourKey : daySec.getKeys(false)) {
                            hours.put(Integer.parseInt(hourKey), daySec.getBoolean(hourKey));
                        }
                    }
                    koth.getSchedules().put(day.toUpperCase(), hours);
                }
            }

            koths.put(name.toLowerCase(), koth);
        }
    }

    public void save() {
        plugin.getConfig().set("koths", null);
        for (Koth koth : koths.values()) {
            String path = "koths." + koth.getName();
            plugin.getConfig().set(path + ".mode", koth.getMode().name());
            plugin.getConfig().set(path + ".required-seconds", koth.getRequiredSeconds());
            plugin.getConfig().set(path + ".duration-minutes", koth.getDurationMinutes());
            plugin.getConfig().set(path + ".keep-inventory", koth.isKeepInventory());
            plugin.getConfig().set(path + ".utilities-allowed", koth.isUtilitiesAllowed());
            plugin.getConfig().set(path + ".reward", koth.getReward());

            if (koth.getPos1() != null) serializeLocation(path + ".pos1", koth.getPos1());
            if (koth.getPos2() != null) serializeLocation(path + ".pos2", koth.getPos2());

            for (Map.Entry<String, Map<Integer, Boolean>> dayEntry : koth.getSchedules().entrySet()) {
                for (Map.Entry<Integer, Boolean> hourEntry : dayEntry.getValue().entrySet()) {
                    plugin.getConfig().set(path + ".schedules." + dayEntry.getKey() + "." + hourEntry.getKey(), hourEntry.getValue());
                }
            }
        }
        plugin.saveConfig();
    }

    private void serializeLocation(String path, Location loc) {
        plugin.getConfig().set(path + ".world", loc.getWorld().getName());
        plugin.getConfig().set(path + ".x", loc.getX());
        plugin.getConfig().set(path + ".y", loc.getY());
        plugin.getConfig().set(path + ".z", loc.getZ());
    }

    private Location deserializeLocation(ConfigurationSection sec) {
        if (sec == null) return null;
        World world = plugin.getServer().getWorld(sec.getString("world"));
        if (world == null) return null;
        return new Location(world, sec.getDouble("x"), sec.getDouble("y"), sec.getDouble("z"));
    }
          }
