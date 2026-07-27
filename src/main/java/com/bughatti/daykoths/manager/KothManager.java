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
            koth
