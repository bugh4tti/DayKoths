package com.bughatti.daykoths.gui;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.HexUtil;
import com.bughatti.daykoths.util.TitleUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Map;

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
        TopsMenu topsMenu = new TopsMenu(plugin);

        if (title.equals(topsMenu.title())) {
            e.setCancelled(true);
            return;
        }

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
                Player player = (Player) e.getWhoClicked();

                if (slot == KothMenu.TOGGLE_SLOT) {
                    boolean newState = !koth.isRunning();

                    if (newState && !koth.hasBothPositions()) {
                        player.sendMessage(msg("koth-no-zone"));
                        return;
                    }

                    koth.setRunning(newState);
                    plugin.getKothManager().save();

                    String broadcastKey = newState ? "koth-started" : "koth-stopped";
                    String raw = plugin.getConfig().getString("messages." + broadcastKey, "")
                            .replace("%prefix%", plugin.getConfig().getString("plugin.prefix", ""))
                            .replace("%koth%", koth.getName());
                    org.bukkit.Bukkit.broadcastMessage(HexUtil.colorize(raw));

                    if (newState) {
                        TitleUtil.sendStartTitle(plugin, koth);
                    }

                    new KothMenu(plugin, koth).open(player);
                } else if (slot == KothMenu.REWARD_SLOT) {
                    new RewardMenu(plugin, koth).open(player);
                } else if (slot == KothMenu.UTILITIES_SLOT) {
                    koth.setUtilitiesAllowed(!koth.isUtilitiesAllowed());
                    plugin.getKothManager().save();
                    player.sendMessage(msg("utilities-set").replace("%koth%", koth.getName()).replace("%value%", String.valueOf(koth.isUtilitiesAllowed())));
                    new KothMenu(plugin, koth).open(player);
                } else if (slot == KothMenu.KEEPINVENTORY_SLOT) {
                    koth.setKeepInventory(!koth.isKeepInventory());
                    plugin.getKothManager().save();
                    player.sendMessage(msg("keepinventory-set").replace("%koth%", koth.getName()).replace("%value%", String.valueOf(koth.isKeepInventory())));
                    new KothMenu(plugin, koth).open(player);
                } else if (slot == KothMenu.DELETE_SLOT) {
                    String deletedName = koth.getName();
                    plugin.getKothManager().delete(deletedName);
                    player.closeInventory();
                    player.sendMessage(msg("koth-deleted").replace("%koth%", deletedName));
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

            ArsenalMenu arsenalMenu = new ArsenalMenu(plugin, koth);
            if (title.equals(arsenalMenu.title())) {
                e.setCancelled(true);
                int slot = e.getRawSlot();
                Player player = (Player) e.getWhoClicked();

                if (slot == ArsenalMenu.TOGGLE_SLOT) {
                    koth.setArsenalEnabled(!koth.isArsenalEnabled());
                    plugin.getKothManager().save();
                    new ArsenalMenu(plugin, koth).open(player);
                    return;
                }

                Map<Integer, String> slotMap = ArsenalMenu.buildSlotMap(plugin);
                String id = slotMap.get(slot);
                if (id != null) {
                    boolean current = plugin.getArsenalManager().isEnabledForKoth(koth, id);
                    koth.getArsenalOverrides().put(id, !current);
                    plugin.getKothManager().save();
                    new ArsenalMenu(plugin, koth).open(player);
                }
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
