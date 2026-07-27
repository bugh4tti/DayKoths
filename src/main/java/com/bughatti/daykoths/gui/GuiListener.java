package com.bughatti.daykoths.gui;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class GuiListener implements Listener {

    private final DayKoths plugin;

    public GuiListener(DayKoths plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();

        if (title.equals(MainMenu.TITLE)) {
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if (item == null || item.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
            if (item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) return;
            String kothName = org.bukkit.ChatColor.stripColor(item.getItemMeta().getDisplayName());
            Koth koth = plugin.getKothManager().get(kothName);
            if (koth != null) new KothMenu(plugin, koth).open((Player) e.getWhoClicked());
            return;
        }

        for (Koth koth : plugin.getKothManager().getAll()) {
            if (title.equals(new KothMenu(plugin, koth).title())) {
                e.setCancelled(true);
                int slot = e.getRawSlot();
                if (slot == 11) {
                    koth.setRunning(!koth.isRunning());
                    plugin.getKothManager().save();
                    new KothMenu(plugin, koth).open((Player) e.getWhoClicked());
                } else if (slot == 15) {
                    new RewardMenu(plugin, koth).open((Player) e.getWhoClicked());
                }
                return;
            }
            if (title.equals(new RewardMenu(plugin, koth).title())) {
                int slot = e.getRawSlot();
                boolean isRewardSlot = slot >= 10 && slot <= 15;
                if (!isRewardSlot) e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        String title = e.getView().getTitle();
        for (Koth koth : plugin.getKothManager().getAll()) {
            if (title.equals(new RewardMenu(plugin, koth).title())) {
                new RewardMenu(plugin, koth).saveFromInventory(e.getInventory());
                return;
            }
        }
    }
}
