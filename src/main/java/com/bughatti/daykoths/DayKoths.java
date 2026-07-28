package com.bughatti.daykoths;

import com.bughatti.daykoths.commands.DayKothsCommand;
import com.bughatti.daykoths.gui.GuiListener;
import com.bughatti.daykoths.listeners.GoldenAppleCooldownListener;
import com.bughatti.daykoths.listeners.KothListener;
import com.bughatti.daykoths.listeners.WandListener;
import com.bughatti.daykoths.manager.ArsenalManager;
import com.bughatti.daykoths.manager.BossBarManager;
import com.bughatti.daykoths.manager.KothManager;
import com.bughatti.daykoths.manager.ScoreboardManager;
import com.bughatti.daykoths.manager.SelectionManager;
import com.bughatti.daykoths.manager.StatsManager;
import com.bughatti.daykoths.placeholder.DayKothsExpansion;
import com.bughatti.daykoths.tasks.ArsenalTask;
import com.bughatti.daykoths.tasks.CaptureTask;
import org.bukkit.plugin.java.JavaPlugin;

public class DayKoths extends JavaPlugin {

    private static DayKoths instance;
    private KothManager kothManager;
    private SelectionManager selectionManager;
    private BossBarManager bossBarManager;
    private ScoreboardManager scoreboardManager;
    private StatsManager statsManager;
    private ArsenalManager arsenalManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        kothManager = new KothManager(this);
        selectionManager = new SelectionManager();
        bossBarManager = new BossBarManager(this);
        scoreboardManager = new ScoreboardManager(this);
        statsManager = new StatsManager(this);
        arsenalManager = new ArsenalManager(this);

        DayKothsCommand command = new DayKothsCommand(this);
        getCommand("daykoths").setExecutor(command);
        getCommand("daykoths").setTabCompleter(command);

        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new KothListener(this), this);
        getServer().getPluginManager().registerEvents(new WandListener(this), this);
        getServer().getPluginManager().registerEvents(new GoldenAppleCooldownListener(this), this);

        new CaptureTask(this).runTaskTimer(this, 20L, 20L);
        new ArsenalTask(this).runTaskTimer(this, 1200L, 1200L);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new DayKothsExpansion(this).register();
            getLogger().info("PlaceholderAPI detectado, variables de DayKoths registradas.");
        }

        getLogger().info("DayKoths habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        if (kothManager != null) kothManager.save();
        if (statsManager != null) statsManager.save();
        if (bossBarManager != null) bossBarManager.removeAll();
        if (scoreboardManager != null) scoreboardManager.clearAll();
    }

    public static DayKoths getInstance() { return instance; }
    public KothManager getKothManager() { return kothManager; }
    public SelectionManager getSelectionManager() { return selectionManager; }
    public BossBarManager getBossBarManager() { return bossBarManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public StatsManager getStatsManager() { return statsManager; }
    public ArsenalManager getArsenalManager() { return arsenalManager; }
}
