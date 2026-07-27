package com.bughatti.daykoths;

import com.bughatti.daykoths.commands.DayKothsCommand;
import com.bughatti.daykoths.gui.GuiListener;
import com.bughatti.daykoths.listeners.KothListener;
import com.bughatti.daykoths.listeners.WandListener;
import com.bughatti.daykoths.manager.KothManager;
import com.bughatti.daykoths.manager.SelectionManager;
import com.bughatti.daykoths.tasks.CaptureTask;
import org.bukkit.plugin.java.JavaPlugin;

public class DayKoths extends JavaPlugin {

    private static DayKoths instance;
    private KothManager kothManager;
    private SelectionManager selectionManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        kothManager = new KothManager(this);
        selectionManager = new SelectionManager();

        DayKothsCommand command = new DayKothsCommand(this);
        getCommand("daykoths").setExecutor(command);
        getCommand("daykoths").setTabCompleter(command);

        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new KothListener(this), this);
        getServer().getPluginManager().registerEvents(new WandListener(this), this);

        new CaptureTask(this).runTaskTimer(this, 20L, 20L);

        getLogger().info("DayKoths habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        if (kothManager != null) kothManager.save();
    }

    public static DayKoths getInstance() { return instance; }
    public KothManager getKothManager() { return kothManager; }
    public SelectionManager getSelectionManager() { return selectionManager; }
}
