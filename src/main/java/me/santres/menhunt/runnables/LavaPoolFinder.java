package me.santres.menhunt.runnables;

import me.santres.menhunt.MenHunt;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class LavaPoolFinder extends BukkitRunnable {

    private final Player originPlayer;
    private final MenHunt plugin;
    private Location currentLavaPool;
    private BukkitTask task;


    public LavaPoolFinder(MenHunt plugin, Player originPlayer) {
        this.originPlayer = originPlayer;
        this.plugin = plugin;
    }

    public void startLavaPoolFinder() {
        this.task = this.runTaskTimer(this.plugin, 0L, 20L * 5);
    }

    public void cancelLavaPoolFinder() {
        this.cancel();
        this.task.cancel();
    }

    @Override
    public void run() {
        if (originPlayer.getWorld().getEnvironment() != World.Environment.NORMAL) {
            return;
        }

        int radius = 100;

        Location customSpeedrunnerLocation = new Location(originPlayer.getLocation().getWorld(), originPlayer.getLocation().getX(), originPlayer.getLocation().getY() - 1, originPlayer.getLocation().getZ());

        originPlayer.sendMessage("Atualizando posição da lava pool mais próxima...");
        Location foundLavaPool = searchLavaPoolBFS(customSpeedrunnerLocation, radius);
        if (foundLavaPool != null) {
            setCurrentLavaPool(foundLavaPool);
        }
    }

    private Location searchLavaPoolBFS(Location originPlayer, Integer radius) {
        Queue<Location> toProcess = new LinkedList<>();
        HashMap<Location, Boolean> processed = new HashMap<>();

        toProcess.add(originPlayer);

        while (!toProcess.isEmpty()) {
            Location location = toProcess.poll();
            if (processed.get(location) != null || !isInsideArea(originPlayer, location, radius)) {
                continue;
            }
            processed.put(location, true);

            Block block = location.getBlock();

            if (block.getType() == Material.AIR) {
                continue;
            }

            for (int dX = -1; dX <= 1; dX++) {
                for (int dY = -1; dY <= 1; dY++) {
                    for (int dZ = -1; dZ <= 1; dZ++) {
                        if (Math.abs(dX) + Math.abs(dY) + Math.abs(dZ) != 2) continue;
                        Location neighbour = new Location(location.getWorld(), location.getX() + dX, location.getY() + dY, location.getZ() + dZ);
                        toProcess.add(neighbour);
                    }
                }
            }

            if (isLavaSource(block)) {
                if (checkNeighborsForLava(block)) {
                    return location;
                }
            }
        }
        return null;
    }


    private boolean checkNeighborsForLava(Block lava) {
        for (int dX = -1; dX <= 1; dX++) {
            for (int dY = -1; dY <= 1; dY++) {
                for (int dZ = -1; dZ <= 1; dZ++) {
                    if (Math.abs(dX) + Math.abs(dY) + Math.abs(dZ) != 1) continue;
                    Location locationToCheck = new Location(lava.getWorld(), lava.getX() + dX, lava.getY() + dY, lava.getZ() + dZ);
                    Block blockToCheck = locationToCheck.getBlock();
                    if (isLavaSource(blockToCheck)) return true;
                }
            }
        }
        return false;
    }


    private boolean isLavaSource(Block lava) {
        BlockData lavaData = lava.getBlockData();
        if (lava.getType() != Material.LAVA) return false;
        return ((Levelled) lavaData).getLevel() == 0;
    }


    private boolean isInsideArea(Location center, Location check, int radius) {
        if (check.getX() > center.getX() + radius || check.getX() < center.getX() - radius) return false;
        if (check.getY() > center.getY() + 5 || check.getY() < center.getY() - 5) return false;
        return !(check.getZ() > center.getZ() + radius) && !(check.getZ() < center.getZ() - radius);
    }

    public Location getCurrentLavaPool() {
        return currentLavaPool;
    }

    public void setCurrentLavaPool(Location currentLavaPool) {
        this.currentLavaPool = currentLavaPool;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }
}
