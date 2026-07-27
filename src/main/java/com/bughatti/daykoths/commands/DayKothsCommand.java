package com.bughatti.daykoths.commands;

import com.bughatti.daykoths.DayKoths;
import com.bughatti.daykoths.gui.KothMenu;
import com.bughatti.daykoths.gui.MainMenu;
import com.bughatti.daykoths.gui.RewardMenu;
import com.bughatti.daykoths.model.CaptureMode;
import com.bughatti.daykoths.model.Koth;
import com.bughatti.daykoths.util.HexUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class DayKothsCommand implements CommandExecutor, TabCompleter {

    private final DayKoths plugin;

    public DayKothsCommand(DayKoths plugin) {
        this.plugin = plugin;
    }

    private String msg(String key) {
        String raw = plugin.getConfig().getString("messages." + key, "");
        String prefix = plugin.getConfig().getString("plugin.prefix", "");
        raw = raw.replace("%prefix%", prefix);
        return HexUtil.colorize(raw);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                new MainMenu(plugin).open((Player) sender);
            }
            return true;
        }

        String sub = args[0].toLowerCase();

        if (!sender.hasPermission("daykoths.admin") && !sub.equals("help")) {
            sender.sendMessage(msg("no-permission"));
            return true;
        }

        switch (sub) {
            case "help": {
                sender.sendMessage(HexUtil.colorize("&b&lDayKoths &8» &fVersion " + plugin.getDescription().getVersion() + " &7creado por &b" + plugin.getConfig().getString("plugin.author", "Bughatti")));
                return true;
            }
            case "reload": {
                plugin.reloadConfig();
                plugin.getKothManager().load();
                sender.sendMessage(msg("reload-success"));
                return true;
            }
            case "create": {
                if (args.length < 4) {
                    sender.sendMessage(msg("create-usage"));
                    return true;
                }
                String name = args[1];
                String modeArg = args[2].toLowerCase();

                if (!modeArg.equals("puntaje") && !modeArg.equals("tiempo")) {
                    sender.sendMessage(msg("create-mode-invalid"));
                    return true;
                }

                int value;
                try {
                    value = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(msg("create-value-invalid"));
                    return true;
                }

                Koth koth = plugin.getKothManager().create(name);
                koth.setMode(modeArg.equals("tiempo") ? CaptureMode.TIME : CaptureMode.SCORE);
                koth.setRequiredSeconds(value);
                plugin.getKothManager().save();
                sender.sendMessage(msg("koth-created").replace("%koth%", name));
                return true;
            }
            case "start": {
                if (args.length < 2) return true;
                Koth koth = plugin.getKothManager().get(args[1]);
                if (koth == null) { sender.sendMessage(msg("koth-not-found")); return true; }
                if (!koth.hasBothPositions()) { sender.sendMessage(msg("koth-no-zone")); return true; }
                koth.setRunning(true);
                plugin.getKothManager().save();
                plugin.getServer().broadcastMessage(msg("koth-started").replace("%koth%", koth.getName()));
                return true;
            }
            case "stop": {
                if (args.length < 2) return true;
                Koth koth = plugin.getKothManager().get(args[1]);
                if (koth == null) { sender.sendMessage(msg("koth-not-found")); return true; }
                koth.setRunning(false);
                plugin.getKothManager().save();
                plugin.getServer().broadcastMessage(msg("koth-stopped").replace("%koth%", koth.getName()));
                return true;
            }
            case "delete": {
                if (args.length < 2) { sender.sendMessage("Uso: /daykoths delete <koth>"); return true; }
                if (!plugin.getKothManager().exists(args[1])) { sender.sendMessage(msg("koth-not-found")); return true; }
                String deletedName = args[1];
                plugin.getKothManager().delete(deletedName);
                sender.sendMessage(msg("koth-deleted").replace("%koth%", deletedName));
                return true;
            }
            case "wand": {
                if (!(sender instanceof Player)) return true;
                Player p = (Player) sender;
                ItemStack wand = new ItemStack(Material.STICK);
                ItemMeta meta = wand.getItemMeta();
                meta.setDisplayName(HexUtil.colorize("&bDayKoths Wand"));
                meta.setLore(Collections.singletonList(HexUtil.colorize("&7Click izq = zona 1 | Click der = zona 2")));
                wand.setItemMeta(meta);
                p.getInventory().addItem(wand);
                p.sendMessage(msg("wand-given"));
                return true;
            }
            case "setpos": {
                if (args.length < 2 || !(sender instanceof Player)) return true;
                Player p = (Player) sender;
                Koth koth = plugin.getKothManager().get(args[1]);
                if (koth == null) { sender.sendMessage(msg("koth-not-found")); return true; }
                if (!plugin.getSelectionManager().hasBoth(p)) {
                    sender.sendMessage(msg("zone-not-set"));
                    return true;
                }
                koth.setPos1(plugin.getSelectionManager().getPos1(p));
                koth.setPos2(plugin.getSelectionManager().getPos2(p));
                plugin.getKothManager().save();
                sender.sendMessage(msg("zone-saved").replace("%koth%", koth.getName()));
                return true;
            }
            case "schedules": {
                if (args.length < 4) { sender.sendMessage("Uso: /daykoths schedules <koth> <dia|alldays> <hora> <true|false>"); return true; }
                Koth koth = plugin.getKothManager().get(args[1]);
                if (koth == null) { sender.sendMessage(msg("koth-not-found")); return true; }
                String dayKey = args[2].equalsIgnoreCase("alldays") ? "ALLDAYS" : args[2].toUpperCase();
                int hour;
                try { hour = Integer.parseInt(args[3].replace("pm", "").replace("PM", "")); } catch (NumberFormatException e) { sender.sendMessage(msg("schedule-hour-invalid")); return true; }
                boolean active = args.length > 4 && Boolean.parseBoolean(args[4]);
                koth.getSchedules().computeIfAbsent(dayKey, k -> new HashMap<>()).put(hour, active);
                plugin.getKothManager().save();
                sender.sendMessage(msg("schedule-updated").replace("%day%", dayKey).replace("%hour%", String.valueOf(hour)).replace("%value%", String.valueOf(active)));
                return true;
            }
            case "utilities": {
                if (args.length < 3) return true;
                Koth koth = plugin.getKothManager().get(args[1]);
                if (koth == null) { sender.sendMessage(msg("koth-not-found")); return true; }
                boolean value = Boolean.parseBoolean(args[2]);
                koth.setUtilitiesAllowed(value);
                plugin.getKothManager().save();
                sender.sendMessage(msg("utilities-set").replace("%koth%", koth.getName()).replace("%value%", String.valueOf(value)));
                return true;
            }
            case "keepinventory": {
                if (args.length < 3) return true;
                Koth koth = plugin.getKothManager().get(args[1]);
                if (koth == null) { sender.sendMessage(msg("koth-not-found")); return true; }
                boolean value = Boolean.parseBoolean(args[2]);
                koth.setKeepInventory(value);
                plugin.getKothManager().save();
                sender.sendMessage(msg("keepinventory-set").replace("%koth%", koth.getName()).replace("%value%", String.valueOf(value)));
                return true;
            }
            case "duration": {
                if (args.length < 3) { sender.sendMessage("Uso: /daykoths duration <koth> <minutos>"); return true; }
                Koth koth = plugin.getKothManager().get(args[1]);
                if (koth == null) { sender.sendMessage(msg("koth-not-found")); return true; }
                try {
                    int minutes = Integer.parseInt(args[2]);
                    koth.setDurationMinutes(minutes);
                    plugin.getKothManager().save();
                    sender.sendMessage(msg("duration-set").replace("%koth%", koth.getName()).replace("%minutes%", String.valueOf(minutes)));
                } catch (NumberFormatException e) {
                    sender.sendMessage(msg("duration-invalid"));
                }
                return true;
            }
            case "rw": {
                if (args.length >= 2 && args[1].equalsIgnoreCase("create")) {
                    if (args.length < 4) { sender.sendMessage(msg("rw-command-usage")); return true; }
                    Koth koth = plugin.getKothManager().get(args[2]);
                    if (koth == null) { sender.sendMessage(msg("koth-not-found")); return true; }
                    StringBuilder cmdBuilder = new StringBuilder();
                    for (int i = 3; i < args.length; i++) {
                        if (i > 3) cmdBuilder.append(" ");
                        cmdBuilder.append(args[i]);
                    }
                    koth.getCommandRewards().add(cmdBuilder.toString());
                    plugin.getKothManager().save();
                    sender.sendMessage(msg("rw-command-added").replace("%koth%", koth.getName()));
                    return true;
                }
                if (args.length < 2 || !(sender instanceof Player)) return true;
                Koth koth = plugin.getKothManager().get(args[1]);
                if (koth == null) { sender.sendMessage(msg("koth-not-found")); return true; }
                new RewardMenu(plugin, koth).open((Player) sender);
                return true;
            }
            default: {
                if (!(sender instanceof Player)) return true;
                Koth koth = plugin.getKothManager().get(sub);
                if (koth == null) { sender.sendMessage(msg("koth-not-found")); return true; }
                new KothMenu(plugin, koth).open((Player) sender);
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> base = Arrays.asList("help", "reload", "create", "start", "stop", "delete", "wand", "setpos", "schedules", "utilities", "keepinventory", "duration", "rw");
        if (args.length == 1) {
            return base.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("rw")) {
            List<String> opts = new ArrayList<>();
            opts.add("create");
            plugin.getKothManager().getAll().forEach(k -> opts.add(k.getName()));
            return opts.stream().filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 2 && Arrays.asList("start", "stop", "delete", "setpos", "schedules", "utilities", "keepinventory", "duration").contains(args[0].toLowerCase())) {
            return plugin.getKothManager().getAll().stream().map(Koth::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("rw") && args[1].equalsIgnoreCase("create")) {
            return plugin.getKothManager().getAll().stream().map(Koth::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            return Arrays.asList("puntaje", "tiempo");
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("schedules")) {
            return Arrays.asList("alldays", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday");
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("schedules")) {
            return Arrays.asList("00","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23");
        }
        if ((args.length == 3 && (args[0].equalsIgnoreCase("utilities") || args[0].equalsIgnoreCase("keepinventory")))
                || (args.length == 5 && args[0].equalsIgnoreCase("schedules"))) {
            return Arrays.asList("true", "false");
        }
        return Collections.emptyList();
    }
                }
