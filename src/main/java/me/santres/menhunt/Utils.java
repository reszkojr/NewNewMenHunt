package me.santres.menhunt;

import org.bukkit.Bukkit;

public class Utils {
    public static void printBroadcast(String msg) {
        Bukkit.getServer().getConsoleSender().sendMessage(msg);
    }

    public static void printConsole(String msg) {
        Bukkit.broadcastMessage(msg);
    }
}
