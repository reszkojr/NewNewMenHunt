package me.santres.menhunt;

import me.santres.menhunt.runnables.PointerUpdater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MenHunt extends JavaPlugin {

    private FileConfiguration config;
    private File configFile;

    private boolean coloredPointer = true;

    private PointerUpdater pointer;

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("speedrunner").setExecutor(new Command(pointer, this));
        Bukkit.getServer().getPluginManager().registerEvents(new Events(this), this);

        createConfig();

        Utils.printBroadcast(ChatColor.GREEN + "MenHunt initiated.");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        Utils.printBroadcast(ChatColor.RED + "MenHunt disabled.");
    }

    public static void setSpeedrunner(Player player) {
        player.setMetadata("SPEEDRUNNER", new FixedMetadataValue(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MenHunt")), true));
    }

    public static Player getSpeedrunner() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasMetadata("SPEEDRUNNER")) return player;
        }
        return null;
    }

    public boolean isColoredPointer() {
        return coloredPointer;
    }

    public void setColoredPointer(boolean coloredPointer) {
        this.coloredPointer = coloredPointer;
    }

    public static void removeSpeedrunner() {
        if (getSpeedrunner() != null && getSpeedrunner().hasMetadata("SPEEDRUNNER")) {
            getSpeedrunner().removeMetadata("SPEEDRUNNER", Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MenHunt")));
        }
    }

    public void persistLastLocation(Location location) {
        this.getConfig().set("LastSpeedrunnerLocation", location.toString());
        try {
            this.getConfig().save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location retrieveLastLocation() {
        String locationString = (String) this.getConfig().get("LastSpeedrunnerLocation");

        String[] parts = locationString.split(",");
        String worldName = parts[0].split("=")[2].replaceAll("[}]", "");
        double x = Double.parseDouble(parts[1].split("=")[1]);
        double y = Double.parseDouble(parts[2].split("=")[1]);
        double z = Double.parseDouble(parts[3].split("=")[1]);
        float pitch = Float.parseFloat(parts[4].split("=")[1]);
        float yaw = Float.parseFloat(parts[5].split("=")[1].replaceAll("[}]", ""));

        World world = Bukkit.getWorld(worldName);

        return new Location(world, x, y, z, yaw, pitch);
    }

    private void createConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    Utils.printConsole("MenHunt configuration file created.");
                }
            } catch (IOException e) {
                Utils.printConsole("Could not create MenHunt configuration file.");
            }
        }

        config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getServer().getConsoleSender().sendMessage("MenHunt config not found.");
        }
        this.saveDefaultConfig();
    }
}
