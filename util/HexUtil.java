package com.bughatti.daykoths.util;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("&x(&[A-Fa-f0-9]){6}");

    public static String colorize(String message) {
        if (message == null) return "";

        Matcher legacy = LEGACY_HEX_PATTERN.matcher(message);
        StringBuilder sb = new StringBuilder();
        while (legacy.find()) {
            String hex = legacy.group().replace("&x", "").replace("&", "");
            legacy.appendReplacement(sb, ChatColor.of("#" + hex).toString());
        }
        legacy.appendTail(sb);
        message = sb.toString();

        Matcher hex = HEX_PATTERN.matcher(message);
        sb = new StringBuilder();
        while (hex.find()) {
            hex.appendReplacement(sb, ChatColor.of("#" + hex.group(1)).toString());
        }
        hex.appendTail(sb);
        message = sb.toString();

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
