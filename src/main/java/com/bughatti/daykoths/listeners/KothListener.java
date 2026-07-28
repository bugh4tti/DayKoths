package com.bughatti.daykoths.listeners;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.HexUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class KothListener implements Listener {

    private final DayKoths plugin;

    public KothListener(DayKoths plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getTabIntegrationManager().onPlayerJoin(e.getPlayer());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Material type = e.getBlock().getType();
        boolean isWoolOrCloth = type.name().contains("WOOL") || type == Material.COBWEB;
        if (!isWoolOrCloth) return;

        for (Koth koth : plugin.getKothManager().getAll()) {
            if (koth.isInside(e.getBlock().getLocation()) && !koth.isUtilitiesAllowed()) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(HexUtil.colorize(plugin.getConfig().getString("messages.utilities-blocked")));
                return;
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        for (Koth koth : plugin.getKothManager().getAll()) {
            if (koth.isInside(player.getLocation())) {
                e.setKeepInventory(koth.isKeepInventory());
                if (koth.isKeepInventory()) {
                    e.getDrops().clear();
                }
                return;
            }
        }
    }
}
