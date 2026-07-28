package com.bughatti.daykoths.gui;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.HexUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ArsenalMenu {

    private final DayKoths plugin;
    private final Koth koth;

    public static final int TOGGLE_SLOT = 4;

    public ArsenalMenu(DayKoths plugin, Koth koth) {
        this.plugin = plugin;
        this.koth = koth;
    }

    public String title() {
        String raw = plugin.getConfig().getString("gui.arsenal-menu-title", "&8Arsenal &7» &b%koth%");
        return HexUtil.colorize(raw.replace("%koth%", koth.getName()));
    }

    public static List<Integer> contentSlots() {
        List<Integer> slots = new ArrayList<>();
        int slot = 10;
        while (slot <= 43) {
            slots.add(slot);
            slot = (slot % 9 == 7) ? slot + 3 : slot + 1;
        }
        return slots;
    }

    public static Map<Integer, String> buildSlotMap(DayKoths plugin) {
        Map<Integer, String> map = new LinkedHashMap<>();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("arsenal.events");
        if (sec == null) return map;
        List<String> ids = new ArrayList<>(sec.getKeys(false));
        List<Integer> slots = contentSlots();
        for (int i = 0; i < ids.size() && i < slots.size(); i++) {
            map.put(slots.get(i), ids.get(i));
        }
        return map;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, title());
        ItemStack pane = pane();
        for (int i = 0; i < 9; i++) inv.setItem(i, pane);
        for (int i = 45; i < 54; i++) inv.setItem(i, pane);
        for (int row = 1; row < 5; row++) {
            inv.setItem(row * 9, pane);
            inv.setItem(row * 9 + 8, pane);
        }

        ItemStack masterToggle = new ItemStack(koth.isArsenalEnabled() ? Material.LIME_DYE : Material.RED_DYE);
        ItemMeta masterMeta = masterToggle.getItemMeta();
        masterMeta.setDisplayName(HexUtil.colorize("&dArsenal: " + (koth.isArsenalEnabled() ? "&aActivado" : "&cDesactivado")));
        masterMeta.setLore(Collections.singletonList(HexUtil.colorize("&7Click para activar/desactivar los eventos")));
        masterToggle.setItemMeta(masterMeta);
        inv.setItem(TOGGLE_SLOT, masterToggle);

        ConfigurationSection eventsSec = plugin.getConfig().getConfigurationSection("arsenal.events");
        Map<Integer, String> slotMap = buildSlotMap(plugin);

        for (Map.Entry<Integer, String> entry : slotMap.entrySet()) {
            int slot = entry.getKey();
            String id = entry.getValue();
            ConfigurationSection eSec = eventsSec == null ? null : eventsSec.getConfigurationSection(id);

            boolean enabled = plugin.getArsenalManager().isEnabledForKoth(koth, id);
            String type = eSec != null ? eSec.getString("type", "GOOD") : "GOOD";
            String name = eSec != null ? eSec.getString("name", id) : id;

            ItemStack item = new ItemStack(enabled ? Material.LIME_WOOL : Material.RED_WOOL);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(HexUtil.colorize(name));
            meta.setLore(Arrays.asList(
                    HexUtil.colorize("&7Tipo: " + ("GOOD".equalsIgnoreCase(type) ? "&aBueno" : "&cMalo")),
                    HexUtil.colorize("&7Estado: " + (enabled ? "&aActivado" : "&cDesactivado")),
                    HexUtil.colorize("&7Click para activar/desactivar")
            ));
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }

        player.openInventory(inv);
    }

    private ItemStack pane() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    public Koth getKoth() { return koth; }
             }
