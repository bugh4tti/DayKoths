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

    public static final int TOGGLE_SLOT = 20;
    public static final int INFO_SLOT = 22;
    public static final int REWARD_SLOT = 24;
    public static final int UTILITIES_SLOT = 29;
    public static final int KEEPINVENTORY_SLOT = 31;
    public static final int DELETE_SLOT = 33;

    public KothMenu(DayKoths plugin, Koth koth) {
        this.plugin = plugin;
        this.koth = koth;
    }

    public String title() {
        String raw = plugin.getConfig().getString("gui.koth-menu-title", "&8Koth &7» &b%koth%");
        return HexUtil.colorize(raw.replace("%koth%", koth.getName()));
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

        inv.setItem(TOGGLE_SLOT, toggleItem());
        inv.setItem(INFO_SLOT, infoItem());
        inv.setItem(REWARD_SLOT, rewardItem());
        inv.setItem(UTILITIES_SLOT, utilitiesItem());
        inv.setItem(KEEPINVENTORY_SLOT, keepInventoryItem());
        inv.setItem(DELETE_SLOT, deleteItem());

        player.openInventory(inv);
    }

    private ItemStack rewardItem() {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(HexUtil.colorize("&6Recompensa (rw)"));
        meta.setLore(Arrays.asList(
                HexUtil.colorize("&7Items: &f" + koth.getReward().size()),
                HexUtil.colorize("&7Comandos: &f" + koth.getCommandRewards().size())
        ));
        item.setItemMeta(meta);
        return item;
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

    private ItemStack toggleItem() {
        ItemStack item = new ItemStack(koth.isRunning() ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(HexUtil.colorize("&aIniciar / Detener Koth"));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack utilitiesItem() {
        ItemStack item = new ItemStack(koth.isUtilitiesAllowed() ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(HexUtil.colorize("&eUtilities: " + (koth.isUtilitiesAllowed() ? "&aActivado" : "&cDesactivado")));
        meta.setLore(Arrays.asList(HexUtil.colorize("&7Click para activar/desactivar lana y telas")));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack keepInventoryItem() {
        ItemStack item = new ItemStack(koth.isKeepInventory() ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(HexUtil.colorize("&ekeepInventory: " + (koth.isKeepInventory() ? "&aActivado" : "&cDesactivado")));
        meta.setLore(Arrays.asList(HexUtil.colorize("&7Click para activar/desactivar")));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack deleteItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(HexUtil.colorize("&cEliminar Koth"));
        meta.setLore(Arrays.asList(HexUtil.colorize("&7Click para borrar este koth")));
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
