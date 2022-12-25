package me.fhoz.chunkydiscord.util;

import org.popcraft.chunky.Selection;

public record GenerationTaskRecord(String world, long chunks, float percentComplete, long hours, long minutes, long seconds, double rate, long currentX, long currentZ, String shape, double centerX, double centerZ, Selection selection) {
    public String getReadableTime() {
        final StringBuilder builder = new StringBuilder();
        if (hours != 0) {
            builder.append(hours);
            builder.append(hours == 1 ? " hour, " : " hours, ");
            builder.append(minutes);
            builder.append(minutes == 1 ? " minute, " : " minutes, ");
        } else if (minutes != 0) {
            builder.append(minutes);
            builder.append(minutes == 1 ? " minute, " : " minutes, ");
        }
        builder.append(seconds);
        builder.append(seconds == 1 ? " second" : " seconds");
        return builder.toString();
    }
}
