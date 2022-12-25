package me.fhoz.chunkydiscord.util;

import me.fhoz.chunkydiscord.ChunkyDiscord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.popcraft.chunky.util.Formatting;
import org.popcraft.chunky.util.TranslationKey;
import org.popcraft.chunky.util.Translator;

import java.awt.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DiscordUtil {
    private static final Map<String, Message> messageMap = new HashMap<>();
    private static final Set<String> pendingTaskStartedMessages = new HashSet<>();
    private static final Color PROGRESS_EMBED_COLOR = new Color(255, 189, 35);
    private static final Color TASK_STARTED_EMBED_COLOR = new Color(0, 153, 255);
    private static final Color TASK_FINISHED_EMBED_COLOR = new Color(86, 211, 0);
    private static final Color TASK_CANCELLED_EMBED_COLOR = new Color(200, 120, 0);

    /**
     * Retrieves the {@link JDA} instance used by the ChunkyDiscord plugin.
     *
     * @return the {@link JDA} instance used by the ChunkyDiscord plugin
     */
    public static JDA getJDA() {
        return ChunkyDiscord.getPlugin().getJda();
    }

    public static void sendProgressMessage(GenerationTaskRecord record) {
        if (pendingTaskStartedMessages.contains(record.world())) {
            return;
        }
        if (messageMap.containsKey(record.world())) {
            final Message cachedMessage = messageMap.get(record.world());
            final Consumer<Message> success = (message) -> ChunkyDiscord.debug("Updated Progress Message embed for message " + message.getJumpUrl());
            final Consumer<Throwable> callback = (error) -> ChunkyDiscord.warning("Failed to update progress message " + cachedMessage.getJumpUrl());
            cachedMessage.editMessageEmbeds(new ProgressEmbed(record).build()).timeout(ChunkyUtil.getUpdateTaskInterval() * 50, TimeUnit.MILLISECONDS).queue(success, callback);
        } else {
            createTaskStartedMessage(record).queue(
                    message -> {
                        ChunkyDiscord.debug("Sent TaskStartedMessage for world " + record.world() + " and message " + message.getJumpUrl());
                        getUpdateChannel().sendMessageEmbeds(new ProgressEmbed(record).build()).queue(
                                progressMessage -> {
                                    messageMap.put(record.world(), progressMessage);
                                    pendingTaskStartedMessages.remove(record.world());
                                    ChunkyDiscord.debug("Created new update message for task " + record.world() + " and message " + progressMessage.getJumpUrl());
                                },
                                error -> {
                                    pendingTaskStartedMessages.remove(record.world());
                                    message.delete().queue();
                                    ChunkyDiscord.debug("Failed to create update message for task " + record.world() + ", deleting TaskStartedMessage.");
                                });
                        },
                    error -> {
                        pendingTaskStartedMessages.remove(record.world());
                        ChunkyDiscord.warning("Failed to create TaskStartedMessage for world " + record.world());
                    }
            );
        }
    }

    public static MessageCreateAction createTaskStartedMessage(GenerationTaskRecord record) {
        pendingTaskStartedMessages.add(record.world());
        return getUpdateChannel().sendMessageEmbeds(new TaskStartedEmbed(record).build());
    }

    public static void sendTaskFinishedMessage(GenerationTaskRecord record) {
        if (messageMap.containsKey(record.world())) {
            Message cachedMessage = messageMap.get(record.world());
            cachedMessage.editMessageEmbeds(new TaskFinishedEmbed(record).build()).queue(
                    message -> ChunkyDiscord.debug("Sent TaskFinishedMessage for world " + record.world() + " by editing the cached Message " + message.getJumpUrl()),
                    error -> ChunkyDiscord.warning("Failed to send TaskFinishedMessage for world " + record.world() + " by editing the cached Message")
            );
            messageMap.remove(record.world());
        } else {
            getUpdateChannel().sendMessageEmbeds(new TaskFinishedEmbed(record).build()).queue(
                    (message) -> ChunkyDiscord.debug("Sent TaskFinishedMessage for world " + record.world() + " and message " + message.getJumpUrl()),
                    (error) -> ChunkyDiscord.warning("Failed to send TaskFinishedMessage for world " + record.world())
            );
        }
    }

    public static void sendTaskCancelledMessage(String world) {
        messageMap.remove(world);
        getUpdateChannel().sendMessageEmbeds(new TaskCancelledEmbed(world).build()).queue(message -> ChunkyDiscord.debug("Sent TaskCancelledMessage for world " + world));
    }

    public static TextChannel getUpdateChannel() {
        final String channelId = ChunkyDiscord.getPlugin().getConfigUtil().getChannel();
        final TextChannel channel = getJDA().getTextChannelById(channelId);
        Check.notNull(channel, "Update channel is null");
        return getJDA().getTextChannelById(channelId);
    }

    public static void clearMessageCache() {
        messageMap.clear();
        pendingTaskStartedMessages.clear();
    }

    /**
     * A class that represents an embed message containing progress information for a generation task.
     */
    private static class ProgressEmbed {
        private final EmbedBuilder builder;

        /**
         * Constructs a new {@code ProgressEmbed} from the given {@link GenerationTaskRecord}.
         *
         * @param record the generation task record to create the progress embed from
         */
        public ProgressEmbed (GenerationTaskRecord record) {
            this.builder = new EmbedBuilder();
            builder.setColor(DiscordUtil.PROGRESS_EMBED_COLOR);
            builder.setTitle("Task running for " + record.world());
            builder.setDescription(String.format("""
                    **Processed:** %s chunks (%s%%)
                    **ETA:** %s
                    **Rate:** %s cps
                    **Current:** %s, %s""",
                    record.chunks(), String.format("%.2f", record.percentComplete()), record.getReadableTime(), String.format("%.1f", record.rate()), record.currentX(), record.currentZ()));
        }

        /**
         * Builds the {@link MessageEmbed} representation of the progress embed.
         *
         * @return the {@link MessageEmbed} representation of the progress embed
         */
        public MessageEmbed build() {
            return this.builder.build();
        }
    }

    /**
     * A class that represents an embed message indicating that a generation task has started.
     */
    private static class TaskStartedEmbed {
        private final EmbedBuilder builder;

        /**
         * Constructs a new {@code TaskStartedEmbed} from the given {@link GenerationTaskRecord}.
         *
         * @param record the generation task record to create the task started embed from
         */
        public TaskStartedEmbed(GenerationTaskRecord record) {
            this.builder = new EmbedBuilder();
            builder.setColor(DiscordUtil.TASK_STARTED_EMBED_COLOR);
            builder.setTitle("Task started in " + record.world());
            builder.setDescription(Translator.translateKey(TranslationKey.FORMAT_START, false, record.world(), Translator.translate("shape_" + record.shape()), Formatting.number(record.centerX()), Formatting.number(record.centerZ()), Formatting.radius(record.selection())));
        }

        /**
         * Builds the {@link MessageEmbed} representation of the task started embed.
         *
         * @return the {@link MessageEmbed} representation of the task started embed
         */
        public MessageEmbed build() {
            return this.builder.build();
        }
    }

    /**
     * A class that represents an embed message indicating that a generation task has finished.
     */
    private static class TaskFinishedEmbed {
        private final EmbedBuilder builder;

        /**
         * Constructs a new {@code TaskFinishedEmbed} from the given {@link GenerationTaskRecord}.
         *
         * @param record the generation task record to create the task finished embed from
         */
        public TaskFinishedEmbed (GenerationTaskRecord record) {
            this.builder = new EmbedBuilder();
            builder.setColor(DiscordUtil.TASK_FINISHED_EMBED_COLOR);
            builder.setTitle("Task finished for " + record.world());
            builder.setDescription(String.format("""
                    **Processed:** %s chunks (%s%%)
                    **Total time:** %s""",
                    record.chunks(), String.format("%.2f", record.percentComplete()), record.getReadableTime()));
        }

        /**
         * Builds the {@link MessageEmbed} representation of the task finished embed.
         *
         * @return the {@link MessageEmbed} representation of the task finished embed
         */
        public MessageEmbed build() {
            return this.builder.build();
        }
    }

    /**
     * A class that represents an embed message indicating that a generation task has been cancelled.
     */
    private static class TaskCancelledEmbed {
        private final EmbedBuilder builder;

        /**
         * Constructs a new {@code TaskCancelledEmbed} for the given world.
         *
         * @param world the name of the world for which the task was cancelled
         */
        public TaskCancelledEmbed (String world) {
            this.builder = new EmbedBuilder();
            builder.setColor(DiscordUtil.TASK_CANCELLED_EMBED_COLOR);
            builder.setTitle("Task cancelled for " + world);
        }

        /**
         * Builds the {@link MessageEmbed} representation of the task cancelled embed.
         *
         * @return the {@link MessageEmbed} representation of the task cancelled embed
         */
        public MessageEmbed build() {
            return this.builder.build();
        }
    }
}
