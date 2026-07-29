package com.bughatti.daykoths.gui;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.util.HexUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreakMenu {

    private final DayKoths plugin;

    public StreakMenu(DayKoths plugin) {
        this.plugin = plugin;
    }

    public String title() {
        return HexUtil.colorize(plugin.getConfig().getString("gui.streak-menu-title", "&8DayKoths &7» &dRachas"));
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

        ConfigurationSection rewardsSec = plugin.getConfig().getConfigurationSection("streak.rewards");
        List<Integer> thresholds = new ArrayList<>();
        if (rewardsSec != null) {
            for (String key : rewardsSec.getKeys(false)) {
                try {
                    thresholds.add(Integer.parseInt(key));
                } catch (NumberFormatException ignored) {}
            }
        }
        Collections.sort(thresholds);

        int slot = 10;
        for (int threshold : thresholds) {
            if (slot > 43) break;
            List<String> commands = rewardsSec.getStringList(threshold + ".commands");

            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(HexUtil.colorize("&d" + threshold + " victorias seguidas"));
            List<String> lore = new ArrayList<>();
            if (commands.isEmpty()) {
                lore.add(HexUtil.colorize("&7Sin recompensa configurada"));
            } else {
                for (String cmd : commands) lore.add(HexUtil.colorize("&7" + cmd));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
            slot = (slot % 9 == 7) ? slot + 3 : slot + 1;
        }

        if (thresholds.isEmpty()) {
            ItemStack info = new ItemStack(Material.BARRIER);
            ItemMeta meta = info.getItemMeta();
            meta.setDisplayName(HexUtil.colorize("&cNo hay recompensas de racha configuradas"));
            meta.setLore(Collections.singletonList(HexUtil.colorize("&7Agregalas en config.yml bajo streak.rewards")));
            info.setItemMeta(meta);
            inv.setItem(22, info);
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
        }
