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

public class RewardMenu {

    private final DayKoths plugin;
    private final Koth koth;

    public RewardMenu(DayKoths plugin, Koth koth) {
        this.plugin = plugin;
        this.koth = koth;
    }

    public String title() {
        return HexUtil.colorize("&8Recompensa &7» &6" + koth.getName());
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, title());
        ItemStack pane = pane();
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) inv.setItem(i, pane);
        }

        int slot = 10;
        for (ItemStack item : koth.getReward()) {
            if (slot > 15) break;
            inv.setItem(slot, item);
            slot++;
        }

        player.openInventory(inv);
    }

    public void saveFromInventory(Inventory inv) {
        java.util.List<ItemStack> reward = new java.util.ArrayList<>();
        for (int slot = 10; slot <= 15; slot++) {
            ItemStack item = inv.getItem(slot);
            if (item != null && item.getType() != Material.GRAY_STAINED_GLASS_PANE && item.getType() != Material.AIR) {
                reward.add(item);
            }
        }
        koth.setReward(reward);
        plugin.getKothManager().save();
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
