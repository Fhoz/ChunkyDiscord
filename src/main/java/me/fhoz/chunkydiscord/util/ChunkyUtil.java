package me.fhoz.chunkydiscord.util;

import me.fhoz.chunkydiscord.ChunkyDiscord;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.popcraft.chunky.Chunky;
import org.popcraft.chunky.GenerationTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ChunkyUtil {
    private static final Map<String, GenerationTask> taskMap = new HashMap<>();
    private static Chunky chunky;
    private static BukkitTask updateTask;
    private static long updateTaskInterval;

    public static void init(@Nonnull Chunky chunky) {
        ChunkyUtil.chunky = chunky;
    }

    public static void stopUpdateTask() {
        updateTask.cancel();
    }

    /**
     * @return The Chunky instance, this should never be null because
     * the plugin should disable itself if it was.
     */
    @Nonnull
    public static Chunky getChunky() {
        return chunky;
    }

    /**
     * Retrieves the generation task for the specified world.
     *
     * @param world the name of the world to retrieve the generation task for
     * @return the generation task for the specified world, or {@code null} if no generation task exists for the world
     */
    @Nullable
    public static GenerationTask getGenerationTask(String world) {
        return chunky.getGenerationTasks().get(world);
    }

    /**
     * Handles updates for all tasks in the task map. For each task, a {@link GenerationTaskRecord} is created and sent as a
     * message to Discord through the {@link DiscordUtil#sendProgressMessage(GenerationTaskRecord)} method.
     *
     * @see GenerationTaskRecord
     * @see DiscordUtil#sendProgressMessage(GenerationTaskRecord)
     */
    public static void handleTaskUpdates() {
        for (GenerationTask task : taskMap.values()) {
            final GenerationTaskRecord taskRecord = createGenerationTaskRecord(task);
            DiscordUtil.sendProgressMessage(taskRecord);
        }
    }

    public static void handleTaskCancelled(@Nonnull String world) {
        taskMap.remove(world);
        DiscordUtil.sendTaskCancelledMessage(world);
        updateBukkitTask();
    }

    public static void handleTaskCompleted(@Nonnull String world) {
        final GenerationTask task = taskMap.get(world);
        taskMap.remove(world);
        final GenerationTaskRecord taskRecord = createGenerationTaskRecord(task);
        DiscordUtil.sendTaskFinishedMessage(taskRecord);
        updateBukkitTask();
    }

    public static boolean isTaskStored(@Nonnull String world) {
        return taskMap.containsKey(world);
    }

    public static void addTask(@Nonnull GenerationTask task) {
        final String world = task.getSelection().world().getName();
        if (taskMap.containsKey(world)) {
            ChunkyDiscord.error("World already exists in taskMap", new Throwable());
            return;
        }
        taskMap.put(world, task);
        updateBukkitTask();
    }

    public static void updateBukkitTask() {
        if (updateTask != null) {
            updateTask.cancel();
            ChunkyDiscord.getPlugin().getJda().cancelRequests();
            ChunkyDiscord.debug("Cancelled previous update task and JDA requests");
        } else {
            ChunkyDiscord.debug("Creating new BukkitTask");
        }
        if (!taskMap.isEmpty()) {
            final long intervalPerTask = ChunkyDiscord.getPlugin().getConfigUtil().getUpdateInterval();
            updateTaskInterval = intervalPerTask * (taskMap.size() - 1) + intervalPerTask;
            updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(ChunkyDiscord.getPlugin(), ChunkyUtil::handleTaskUpdates, 0, updateTaskInterval);
            ChunkyDiscord.info("Updated bukkit task, now running every " + updateTaskInterval + " ticks");
        }
    }

    public static long getUpdateTaskInterval() {
        return updateTaskInterval;
    }

    private static GenerationTaskRecord createGenerationTaskRecord(GenerationTask task) {
        return new GenerationTaskRecord(task.getSelection().world().getName(),
                task.getCount(),
                task.getProgress().getPercentComplete(),
                task.getProgress().getHours(),
                task.getProgress().getMinutes(),
                task.getProgress().getSeconds(),
                task.getProgress().getRate(),
                task.getProgress().getChunkX(),
                task.getProgress().getChunkZ(),
                task.getShape().name(),
                task.getSelection().centerX(),
                task.getSelection().centerZ(),
                task.getSelection());
    }
}
