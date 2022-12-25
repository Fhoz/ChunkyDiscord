package me.fhoz.chunkydiscord.listeners;

import me.fhoz.chunkydiscord.ChunkyDiscord;
import me.fhoz.chunkydiscord.util.ChunkyUtil;
import org.popcraft.chunky.GenerationTask;
import org.popcraft.chunky.event.task.GenerationTaskFinishEvent;

import java.util.function.Consumer;

public class GenerationTaskFinishListener implements Consumer<GenerationTaskFinishEvent> {
    public GenerationTaskFinishListener(ChunkyDiscord plugin) {
        plugin.getChunky().getEventBus().subscribe(GenerationTaskFinishEvent.class, this);
    }

    @Override
    public void accept(final GenerationTaskFinishEvent event) {
        final GenerationTask task = event.generationTask();
        if (task.isCancelled() && !task.getProgress().isComplete()) {
            ChunkyUtil.handleTaskCancelled(task.getSelection().world().getName());
        }
    }
}
