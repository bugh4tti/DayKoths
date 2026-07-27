package com.bughatti.daykoths.listeners;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.util.HexUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WandListener implements Listener {

    private final DayKoths plugin;

    public WandListener(DayKoths plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) return;
        if (!ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("DayKoths Wand")) return;
        if (e.getClickedBlock() == null) return;

        e.setCancelled(true);
        Player player = e.getPlayer();

        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            plugin.getSelectionManager().setPos1(player, e.getClickedBlock().getLocation());
            player.sendMessage(HexUtil.colorize("&aPosicion 1 marcada. Usa /daykoths setpos <koth> para guardarla."));
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            plugin.getSelectionManager().setPos2(player, e.getClickedBlock().getLocation());
            player.sendMessage(HexUtil.colorize("&aPosicion 2 marcada. Usa /daykoths setpos <koth> para guardarla."));
        }
    }
}
