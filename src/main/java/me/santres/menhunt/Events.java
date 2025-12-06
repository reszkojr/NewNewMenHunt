package me.santres.menhunt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {

    MenHunt plugin;

    public Events(MenHunt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player speedrunner = MenHunt.getSpeedrunner();
        if (speedrunner == null) return;

        if (event.getPlayer() == speedrunner) {
            MenHunt.removeSpeedrunner();
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player speedrunner = MenHunt.getSpeedrunner();
        if (speedrunner == null) return;

        if (event.getEntity().getPlayer() == speedrunner) return;
        for (ItemStack item : event.getDrops()) {
            if (item.getType() == Material.COMPASS) {
                event.getDrops().remove(item);
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerPortalEnter(PlayerPortalEvent event) {
        Player speedrunner = MenHunt.getSpeedrunner();
        if (speedrunner == null) return;
        if (event.getPlayer() != speedrunner) return;

        Location lastSpeedrunnerLocation = event.getPlayer().getLocation();

        if (speedrunner.getLocation().getWorld().getEnvironment() == World.Environment.NETHER) {
            lastSpeedrunnerLocation.setX(lastSpeedrunnerLocation.getX() * 8);
            lastSpeedrunnerLocation.setZ(lastSpeedrunnerLocation.getZ() * 8);
            lastSpeedrunnerLocation.setWorld(Bukkit.getWorld("world"));
        }

        this.plugin.persistLastLocation(lastSpeedrunnerLocation);
    }
}

