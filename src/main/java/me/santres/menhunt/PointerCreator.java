package me.santres.menhunt;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class PointerCreator {

    private Location origin;
    private Location target;
    private boolean fixed;
    private char character = 'X';

    private MenHunt plugin;

    public PointerCreator(Location origin, Location target, MenHunt plugin) {
        this.origin = origin;
        this.target = target;
        this.plugin = plugin;
    }

    public PointerCreator(Location origin, Location target, boolean fixed, MenHunt plugin) {
        this.origin = origin;
        this.target = target;
        this.plugin = plugin;
        this.fixed = fixed;
    }

    public PointerCreator(Location origin, Location target, boolean fixed, MenHunt plugin, Character character) {
        this.origin = origin;
        this.target = target;
        this.plugin = plugin;
        this.fixed = fixed;
        this.character = character;
    }


    private double getAngle() {
        Vector originVec = origin.toVector();
        Vector targetVec = target.toVector();

        Vector trajectory = targetVec.clone().subtract(originVec).normalize();

        double yaw = Math.toDegrees(Math.atan(-trajectory.getX() / trajectory.getZ()));

        if (trajectory.getZ() < 0) {
            yaw += 180;
        }

        double angle = yaw - origin.getYaw();

        if (angle > 180) {
            angle -= 360;
        }
        return angle;
    }

    private ChatColor colorDistance(int distance) {
        ChatColor color;

        if (distance > 40000) {
            color = ChatColor.AQUA;
        } else if (distance > 10000) {
            color = ChatColor.DARK_AQUA;
        } else if (distance > 2500) {
            color = ChatColor.GOLD;
        } else if (distance > 400) {
            color = ChatColor.RED;
        } else {
            color = ChatColor.DARK_RED;
        }

        return color;
    }

    public String create() {
        double angle = getAngle();

        int MAX_CHARS = 81;
        int drawnChars = 0;

        final StringBuilder stringBuilder = new StringBuilder(MAX_CHARS);
        stringBuilder.append(ChatColor.BOLD);

        if (angle > 90) {
            while (stringBuilder.length() < MAX_CHARS - 1) stringBuilder.append(' ');
            stringBuilder.append('>');
            drawnChars = MAX_CHARS + 1;
        } else if (angle < -90) {
            stringBuilder.append('<');
            while (stringBuilder.length() < MAX_CHARS - 1) stringBuilder.append(' ');
            drawnChars = MAX_CHARS + 1;
        } else {
            for (double percent = (angle + 90.0) / 180.0; percent >= drawnChars / (double) MAX_CHARS; drawnChars++) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(this.character);
            drawnChars++;
        }
        while (drawnChars <= MAX_CHARS) {
            stringBuilder.append(' ');
            drawnChars++;
        }
        String text = stringBuilder.toString();

        int distance = (int) origin.distanceSquared(target);

        if (this.isFixed()) {
            return ChatColor.GOLD + text;
        }
        return colorDistance(distance) + text;
    }

    private boolean isFixed() {
        return this.fixed;
    }
}
