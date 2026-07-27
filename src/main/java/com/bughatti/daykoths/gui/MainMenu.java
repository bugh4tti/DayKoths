package com.bughatti.daykoths.gui;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.HexUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MainMenu {

    private final DayKoths plugin;
    public static final String TITLE = HexUtil.colorize("&8DayKoths &7» &fMenu Principal");

    public MainMenu(DayKoths plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        ItemStack pane = pane();
        for (int i = 0; i < 9; i++) inv.setItem(i, pane);
        for (int i = 45; i < 54; i++) inv.setItem(i, pane);
        for (int row = 1; row < 5; row++) {
            inv.setItem(row * 9, pane);
            inv.setItem(row * 9 + 8, pane);
        }

        int slot = 10;
        for (Koth koth : plugin.getKothManager().getAll()) {
            if (slot > 43) break;
            ItemStack item = new ItemStack(koth.isRunning() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(HexUtil.colorize("&b" + koth.getName()));
            meta.setLore(Arrays.asList(
                    HexUtil.colorize("&7Estado: " + (koth.isRunning() ? "&aActivo" : "&cInactivo")),
                    HexUtil.colorize("&7Modo: &f" + koth.getMode().name()),
                    HexUtil.colorize("&7Click para abrir")
            ));
            item.setItemMeta(meta);
            inv.setItem(slot, item);
            slot = (slot % 8 == 7) ? slot + 3 : slot + 1;
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
