package me.santres.menhunt.runnables;

import me.santres.menhunt.MenHunt;
import me.santres.menhunt.PointerCreator;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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

public class LavaPoolPointerUpdater extends BukkitRunnable {

    private final MenHunt plugin;
    private BukkitTask task;
    private LavaPoolFinder lavaPoolFinder;

    public LavaPoolPointerUpdater(MenHunt plugin, LavaPoolFinder lavaPoolFinder) {
        this.plugin = plugin;
        this.lavaPoolFinder = lavaPoolFinder;
    }

    public void startLavaPoolPointerUpdater() {
        lavaPoolFinder.startLavaPoolFinder();
        this.task = this.runTaskTimer(this.plugin, 0L, 1L);
    }

    public void cancelLavaPoolPointerUpdater() {
        this.cancel();
        this.task.cancel();
    }

    @Override
    public void run() {
        Player speedrunner = MenHunt.getSpeedrunner();

        if (speedrunner == null) {
            this.cancel();
            return;
        }

        if (this.lavaPoolFinder.getCurrentLavaPool() == null) return;

        PointerCreator pointerCreator = new PointerCreator(speedrunner.getLocation(), lavaPoolFinder.getCurrentLavaPool(), this.plugin);
        speedrunner.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(pointerCreator.create()));
    }

}
