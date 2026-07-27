package com.bughatti.daykoths.gui;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.HexUtil;
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

    private String msg(String key) {
        String raw = plugin.getConfig().getString("messages." + key, "");
        String prefix = plugin.getConfig().getString("plugin.prefix", "");
        return HexUtil.colorize(raw.replace("%prefix%", prefix));
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        MainMenu mainMenu = new MainMenu(plugin);

        if (title.equals(mainMenu.title())) {
            e.setCancelled(true);
            int slot = e.getRawSlot();

            if (slot == MainMenu.CREATE_SLOT) {
                ((Player) e.getWhoClicked()).sendMessage(msg("gui-create-hint"));
                return;
            }

            ItemStack item = e.getCurrentItem();
            if (item == null || item.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
            if (item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) return;
            String kothName = org.bukkit.ChatColor.stripColor(item.getItemMeta().getDisplayName());
            Koth koth = plugin.getKothManager().get(kothName);
            if (koth != null) new KothMenu(plugin, koth).open((Player) e.getWhoClicked());
            return;
        }

        for (Koth koth : plugin.getKothManager().getAll()) {
            KothMenu kothMenu = new KothMenu(plugin, koth);
            if (title.equals(kothMenu.title())) {
                e.setCancelled(true);
                int slot = e.getRawSlot();
                if (slot == KothMenu.TOGGLE_SLOT) {
                    koth.setRunning(!koth.isRunning());
                    plugin.getKothManager().save();
                    new KothMenu(plugin, koth).open((Player) e.getWhoClicked());
                } else if (slot == KothMenu.REWARD_SLOT) {
                    new RewardMenu(plugin, koth).open((Player) e.getWhoClicked());
                }
                return;
            }
            RewardMenu rewardMenu = new RewardMenu(plugin, koth);
            if (title.equals(rewardMenu.title())) {
                int slot = e.getRawSlot();
                boolean isRewardSlot = false;
                for (int s : RewardMenu.REWARD_SLOTS) {
                    if (s == slot) { isRewardSlot = true; break; }
                }
                if (!isRewardSlot) e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        String title = e.getView().getTitle();
        for (Koth koth : plugin.getKothManager().getAll()) {
            RewardMenu rewardMenu = new RewardMenu(plugin, koth);
            if (title.equals(rewardMenu.title())) {
                rewardMenu.saveFromInventory(e.getInventory());
                return;
            }
        }
    }
                }
