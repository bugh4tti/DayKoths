package com.bughatti.daykoths.gui;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.util.HexUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TopsMenu {

    private final DayKoths plugin;
    private static final int[] SLOTS = {20, 21, 22, 23, 24};

    public TopsMenu(DayKoths plugin) {
        this.plugin = plugin;
    }

    public String title() {
        return HexUtil.colorize(plugin.getConfig().getString("gui.tops-menu-title", "&8DayKoths &7» &fTop Jugadores"));
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

        List<Map.Entry<UUID, Integer>> top = plugin.getStatsManager().topWins(5);

        for (int i = 0; i < 5; i++) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();

            if (i < top.size()) {
                UUID id = top.get(i).getKey();
                OfflinePlayer op = Bukkit.getOfflinePlayer(id);
                String name = op.getName() == null ? "---" : op.getName();
                meta.setOwningPlayer(op);
                meta.setDisplayName(HexUtil.colorize("&e#" + (i + 1) + " &f" + name));
                meta.setLore(Collections.singletonList(HexUtil.colorize("&7Koths ganados: &f" + top.get(i).getValue())));
            } else {
                meta.setDisplayName(HexUtil.colorize("&7#" + (i + 1) + " &8---"));
                meta.setLore(Collections.singletonList(HexUtil.colorize("&7Koths ganados: &f0")));
            }

            skull.setItemMeta(meta);
            inv.setItem(SLOTS[i], skull);
        }

        player.openInventory(inv);
    }

    private ItemStack pane() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
}
