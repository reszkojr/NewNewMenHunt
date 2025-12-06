package me.santres.menhunt.runnables;

import me.santres.menhunt.MenHunt;
import me.santres.menhunt.PointerCreator;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PointerUpdater extends BukkitRunnable {

    private final MenHunt plugin;
    private BukkitTask task;

    public PointerUpdater(MenHunt plugin) {
        this.plugin = plugin;
    }

    public void startPointer() {
        this.task = this.runTaskTimer(this.plugin, 0L, 2L);
    }

    public void cancelPointer() {
        this.cancel();
        this.task.cancel();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Player speedrunner = MenHunt.getSpeedrunner();
            if (speedrunner == null) {
                this.cancel();
                return;
            }

            if (player == speedrunner) continue;
            if (player.getLocation().getWorld() == null || speedrunner.getLocation().getWorld() == null) return;

            PointerCreator pc;

            World hunterWorld = player.getLocation().getWorld();
            World speedrunnerWorld = speedrunner.getLocation().getWorld();

            if (hunterWorld == speedrunnerWorld) {
                pc = new PointerCreator(player.getLocation(), speedrunner.getLocation(), true, this.plugin, 'L');
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(pc.create()));
                continue;
            }

            if (hunterWorld.getEnvironment() == World.Environment.THE_END && speedrunnerWorld.getEnvironment() != World.Environment.THE_END) {
                continue;
            }

            if (hunterWorld.getEnvironment() == World.Environment.NETHER) {
                Location overworldPortalLocation = this.plugin.retrieveLastLocation();

                double netherX = overworldPortalLocation.getX() / 8;
                double netherZ = overworldPortalLocation.getZ() / 8;
                double netherY = overworldPortalLocation.getY();

                Location portalNetherLocation = new Location(Bukkit.getWorld("world_nether"), netherX, netherY, netherZ);

                pc = new PointerCreator(player.getLocation(), portalNetherLocation, this.plugin);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(pc.create()));
                continue;
            }

            pc = new PointerCreator(player.getLocation(), this.plugin.retrieveLastLocation(), this.plugin);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(pc.create()));

        }
    }
}
