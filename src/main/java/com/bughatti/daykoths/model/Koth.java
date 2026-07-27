package com.bughatti.daykoths.model;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Koth {

    private final String name;
    private Location pos1;
    private Location pos2;
    private CaptureMode mode = CaptureMode.SCORE;
    private int requiredSeconds = 60;
    private int durationMinutes = 30;
    private boolean running = false;
    private boolean keepInventory = false;
    private boolean utilitiesAllowed = true;
    private List<ItemStack> reward = new ArrayList<>();
    private String rewardCommand = null;

    private final Map<String, Map<Integer, Boolean>> schedules = new HashMap<>();

    private final transient Set<UUID> playersInside = new HashSet<>();
    private final transient Map<UUID, Integer> scoreProgress = new HashMap<>();
    private transient UUID currentCapturer = null;
    private transient int currentCaptureSeconds = 0;
    private transient long startedAt = 0;

    public Koth(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public Location getPos1() { return pos1; }
    public void setPos1(Location pos1) { this.pos1 = pos1; }
    public Location getPos2() { return pos2; }
    public void setPos2(Location pos2) { this.pos2 = pos2; }
    public CaptureMode getMode() { return mode; }
    public void setMode(CaptureMode mode) { this.mode = mode; }
    public int getRequiredSeconds() { return requiredSeconds; }
    public void setRequiredSeconds(int requiredSeconds) { this.requiredSeconds = requiredSeconds; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public boolean isRunning() { return running; }
    public void setRunning(boolean running) {
        this.running = running;
        if (running) {
            this.startedAt = System.currentTimeMillis();
            this.playersInside.clear();
            this.scoreProgress.clear();
            this.currentCapturer = null;
            this.currentCaptureSeconds = 0;
        }
    }
    public boolean isKeepInventory() { return keepInventory; }
    public void setKeepInventory(boolean keepInventory) { this.keepInventory = keepInventory; }
    public boolean isUtilitiesAllowed() { return utilitiesAllowed; }
    public void setUtilitiesAllowed(boolean utilitiesAllowed) { this.utilitiesAllowed = utilitiesAllowed; }
    public List<ItemStack> getReward() { return reward; }
    public void setReward(List<ItemStack> reward) { this.reward = reward; }
    public String getRewardCommand() { return rewardCommand; }
    public void setRewardCommand(String rewardCommand) { this.rewardCommand = rewardCommand; }
    public Map<String, Map<Integer, Boolean>> getSchedules() { return schedules; }

    public boolean hasBothPositions() { return pos1 != null && pos2 != null; }

    public boolean isInside(Location loc) {
        if (!hasBothPositions() || loc.getWorld() == null) return false;
        if (!loc.getWorld().equals(pos1.getWorld())) return false;

        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX
                && loc.getY() >= minY && loc.getY() <= maxY
                && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    public Set<UUID> getPlayersInside() { return playersInside; }
    public Map<UUID, Integer> getScoreProgress() { return scoreProgress; }
    public UUID getCurrentCapturer() { return currentCapturer; }
    public void setCurrentCapturer(UUID currentCapturer) { this.currentCapturer = currentCapturer; }
    public int getCurrentCaptureSeconds() { return currentCaptureSeconds; }
    public void setCurrentCaptureSeconds(int currentCaptureSeconds) { this.currentCaptureSeconds = currentCaptureSeconds; }
    public long getStartedAt() { return startedAt; }
    }
