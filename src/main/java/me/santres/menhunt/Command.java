package me.santres.menhunt;

import me.santres.menhunt.runnables.LavaPoolFinder;
import me.santres.menhunt.runnables.LavaPoolPointerUpdater;
import me.santres.menhunt.runnables.PointerUpdater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Command implements CommandExecutor, TabCompleter {

    PointerUpdater pointer;
    LavaPoolPointerUpdater lavaPoolPointerUpdater;
    MenHunt plugin;
    LavaPoolFinder lavaPoolFinder;

    public Command(PointerUpdater pointer, MenHunt plugin) {
        this.pointer = pointer;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String lbl, String[] args) {
        if (cmd.getName().equals("speedrunner")) {
            switch (args.length) {
                case 1 -> {
                    if (args[0].equals("get")) {
                        if (MenHunt.getSpeedrunner() == null) {
                            sender.sendMessage(ChatColor.GRAY + "No speedrunner was set yet.");
                            return false;
                        }
                        sender.sendMessage(ChatColor.AQUA + MenHunt.getSpeedrunner().getName() + " is the current speedrunner.");
                        return false;
                    }

                    if (args[0].equals("remove")) {
                        if (MenHunt.getSpeedrunner() == null) {
                            sender.sendMessage(ChatColor.RED + "No speedrunner was set yet!");
                            return false;
                        }
                        this.pointer.cancelPointer();
                        this.lavaPoolPointerUpdater.cancelLavaPoolPointerUpdater();
                        this.lavaPoolFinder.cancelLavaPoolFinder();
                        sender.sendMessage(ChatColor.GREEN + MenHunt.getSpeedrunner().getName() + " is not speedrunner anymore.");
                        MenHunt.removeSpeedrunner();
                    }
                }

                case 2 -> {
                    if (args[0].equals("set")) {
                        Player speedrunnerCandidate = Bukkit.getPlayer(args[1]);
                        MenHunt.getSpeedrunner();

                        if (speedrunnerCandidate == null) {
                            sender.sendMessage(ChatColor.RED + "Player does not exist.");
                            return false;
                        }

                        if (MenHunt.getSpeedrunner() == speedrunnerCandidate) {
                            sender.sendMessage(ChatColor.AQUA + speedrunnerCandidate.getName() + " already is speedrunner!");
                            return false;
                        }

                        MenHunt.removeSpeedrunner();
                        MenHunt.setSpeedrunner(speedrunnerCandidate);

                        this.pointer = new PointerUpdater(this.plugin);
                        this.pointer.startPointer();

                        this.lavaPoolFinder = new LavaPoolFinder(this.plugin, MenHunt.getSpeedrunner());
                        this.lavaPoolPointerUpdater = new LavaPoolPointerUpdater(this.plugin, this.lavaPoolFinder);
                        this.lavaPoolPointerUpdater.startLavaPoolPointerUpdater();

                        sender.sendMessage(ChatColor.GREEN + speedrunnerCandidate.getName() + " is now speedrunner!");
                        return false;
                    }

                    if (args[0].equals("colored")) {
                        switch (args[1]) {
                            case "enable" -> this.plugin.setColoredPointer(true);
                            case "disable" -> this.plugin.setColoredPointer(false);
                            default -> sendUsage(sender);
                        }
                        sender.sendMessage(ChatColor.GREEN + "Pointer color is now " + (this.plugin.isColoredPointer() ? "enabled" : "disabled"));
                        return false;
                    }

                    sendUsage(sender);

                }
                default -> sendUsage(sender);

            }

        }
        return false;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Wrong usage. Usages:");
        sender.sendMessage(ChatColor.RED + "/speedrunner set <player>");
        sender.sendMessage(ChatColor.RED + "/speedrunner remove|get");
        sender.sendMessage(ChatColor.RED + "/speedrunner colored enable|disable,");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String lbl, String[] args) {
        List<String> list = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("speedrunner")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                list.add("set");
                list.add("remove");
                list.add("get");
                list.add("colored");

                if (args[0].equals("set")) {
                    list.clear();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        list.add(p.getName());
                    }
                    return list;
                }
                if (args[0].equals("colored")) {
                    list.clear();
                    list.add("enable");
                    list.add("disable");
                    return list;
                }
                if (args.length > 1) list.clear();
            }
        }
        return list;
    }
}
