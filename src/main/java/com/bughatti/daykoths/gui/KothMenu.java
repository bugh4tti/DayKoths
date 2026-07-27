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

public class KothMenu {

    private final DayKoths plugin;
    private final Koth koth;

    public KothMenu(DayKoths plugin, Koth koth) {
        this.plugin = plugin;
        this.koth = koth;
    }

    public String title() {
        return HexUtil.colorize("&8Koth &7» &b" + koth.getName());
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, title());
        ItemStack pane = pane();
        for (int i = 0; i < 9; i++) inv.setItem(i, pane);
        for (int i = 18; i < 27; i++) inv.setItem(i, pane);
        inv.setItem(9, pane); inv.setItem(17, pane);

        inv.setItem(11, toggleItem(koth.isRunning(), "&aIniciar / Detener Koth"));
        inv.setItem(13, infoItem());
        inv.setItem(15, new ItemStack(Material.CHEST));
        ItemMeta rewardMeta = inv.getItem(15).getItemMeta();
        rewardMeta.setDisplayName(HexUtil.colorize("&6Recompensa (rw)"));
        inv.getItem(15).setItemMeta(rewardMeta);

        player.openInventory(inv);
    }

    private ItemStack infoItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(HexUtil.colorize("&bInformacion"));
        meta.setLore(Arrays.asList(
                HexUtil.colorize("&7Modo: &f" + koth.getMode().name()),
                HexUtil.colorize("&7Duracion: &f" + koth.getDurationMinutes() + "m"),
                HexUtil.colorize("&7keepInventory: &f" + koth.isKeepInventory()),
                HexUtil.colorize("&7Utilities: &f" + koth.isUtilitiesAllowed()),
                HexUtil.colorize("&7Zona marcada: &f" + koth.hasBothPositions())
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack toggleItem(boolean state, String name) {
        ItemStack item = new ItemStack(state ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(HexUtil.colorize(name));
        item.setItemMeta(meta);
        return item;
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
