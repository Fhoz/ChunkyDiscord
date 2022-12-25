package me.fhoz.chunkydiscord.listeners;

import me.fhoz.chunkydiscord.ChunkyDiscord;
import me.fhoz.chunkydiscord.util.ChunkyUtil;
import org.popcraft.chunky.GenerationTask;
import org.popcraft.chunky.api.event.task.GenerationProgressEvent;

public class GenerationProgressListener {
    public GenerationProgressListener(ChunkyDiscord plugin) {
        plugin.getChunkyAPI().onGenerationProgress(this::onGenerationProgress);
    }

    public void onGenerationProgress(GenerationProgressEvent event) {
        if (!ChunkyUtil.isTaskStored(event.world())) {
            final GenerationTask task = ChunkyUtil.getGenerationTask(event.world());
            if (task == null) {
                ChunkyDiscord.debug("Generation task for world " + event.world() + " is null!");
                return;
            }
            ChunkyUtil.addTask(task);
        }
        if (event.complete()) {
            ChunkyUtil.handleTaskCompleted(event.world());
        }
    }
}
