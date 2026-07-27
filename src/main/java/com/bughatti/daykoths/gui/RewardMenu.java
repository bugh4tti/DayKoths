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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RewardMenu {

    private final DayKoths plugin;
    private final Koth koth;

    public static final int[] REWARD_SLOTS = {
            19, 20, 21, 22, 23, 24, 25
    };

    public static final int[] COMMAND_SLOTS = {
            37, 38, 39, 40, 41, 42, 43
    };

    public RewardMenu(DayKoths plugin, Koth koth) {
        this.plugin = plugin;
        this.koth = koth;
    }

    public String title() {
        String raw = plugin.getConfig().getString("gui.reward-menu-title", "&8Recompensa &7» &6%koth%");
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

        int idx = 0;
        for (ItemStack item : koth.getReward()) {
            if (idx >= REWARD_SLOTS.length) break;
            inv.setItem(REWARD_SLOTS[idx], item);
            idx++;
        }

        int cmdIdx = 0;
        for (String cmd : koth.getCommandRewards()) {
            if (cmdIdx >= COMMAND_SLOTS.length) break;
            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();
            meta.setDisplayName(HexUtil.colorize("&eComando #" + (cmdIdx + 1)));
            meta.setLore(Collections.singletonList(HexUtil.colorize("&7" + cmd)));
            paper.setItemMeta(meta);
            inv.setItem(COMMAND_SLOTS[cmdIdx], paper);
            cmdIdx++;
        }

        player.openInventory(inv);
    }

    public void saveFromInventory(Inventory inv) {
        List<ItemStack> reward = new ArrayList<>();
        for (int slot : REWARD_SLOTS) {
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
