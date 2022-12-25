package me.fhoz.chunkydiscord;

import me.fhoz.chunkydiscord.command.CDCommand;
import me.fhoz.chunkydiscord.listeners.GenerationProgressListener;
import me.fhoz.chunkydiscord.listeners.GenerationTaskFinishListener;
import me.fhoz.chunkydiscord.util.ChunkyUtil;
import me.fhoz.chunkydiscord.util.ConfigUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.requests.GatewayIntent;
import okhttp3.OkHttpClient;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.chunky.Chunky;
import org.popcraft.chunky.api.ChunkyAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.logging.Logger;

public final class ChunkyDiscord extends JavaPlugin {
    private static ChunkyDiscord instance;
    private ChunkyAPI chunkyAPI;
    private Chunky chunky;
    private ConfigUtil config;
    private JDA jda;
    private final CDCommand command = new CDCommand(this);

    private final EnumSet<GatewayIntent> intents = EnumSet.of(
            GatewayIntent.MESSAGE_CONTENT
    );

    public ChunkyDiscord() {
        super();
    }

    @Override
    public void onEnable() {
        setInstance(this);
        onPluginStart();
    }

    private void onPluginStart() {
        this.saveDefaultConfig();

        try {
            command.register();
        } catch (Exception | LinkageError x) {
            error("An Exception occurred while registering the /chunkydiscord command", x);
        }

        config = new ConfigUtil(getPlugin());
        chunkyAPI = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);

        ChunkyUtil.init(getChunky());

        jda = JDABuilder.createDefault(config.getToken())
                .enableIntents(intents)
                .build();
        jda.getSelfUser().getManager().setAvatar(getIcon()).setName(config.getName()).queue();

        registerListeners();
        startMetrics();
        printStartupMessage();
    }

    @Override
    public void onDisable() {
        // Properly shut down JDA
        jda.shutdown();
        OkHttpClient client = jda.getHttpClient();
        client.connectionPool().evictAll();
        client.dispatcher().executorService().shutdown();
    }

    private static void setInstance(@Nullable ChunkyDiscord pluginInstance) {
        instance = pluginInstance;
    }

    public static @Nonnull Logger logger() {
        validateInstance();
        return instance.getLogger();
    }

    private static void validateInstance() {
        if (instance == null) {
            throw new IllegalStateException("Cannot invoke static method, ChunkyDiscord instance is null.");
        }
    }

    public ChunkyAPI getChunkyAPI() {
        return chunkyAPI;
    }

    private void registerListeners() {
        new GenerationProgressListener(this);
        new GenerationTaskFinishListener(this);
    }

    public static ChunkyDiscord getPlugin() {
        return getPlugin(ChunkyDiscord.class);
    }

    // Logging methods
    public static void info(String message) {
        getPlugin().getLogger().info(message);
    }

    public static void warning(String message) {
        getPlugin().getLogger().warning(message);
    }

    public static void error(String message) {
        getPlugin().getLogger().severe(message);
    }

    public static void error(String message, Throwable throwable) {
        error(message);
        error(throwable);
    }

    public static void error(Throwable throwable) {
        logThrowable(throwable, ChunkyDiscord::error);
    }

    public static void logThrowable(Throwable throwable, Consumer<String> logger) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));

        for (String line : stringWriter.toString().split("\n")) {
            logger.accept(line);
        }
    }

    public static void debug(String message) {
        if (ChunkyDiscord.getPlugin().getConfigUtil().isDebugEnabled()) {
            getPlugin().getLogger().severe("[DEBUG] " + message);
        }
    }

    public JDA getJda() {
        return jda;
    }

    public ConfigUtil getConfigUtil() {
        return config;
    }

    public Chunky getChunky() {
        if (this.chunky == null) {
            getNewChunky();
        }
        return chunky;
    }

    /**
     * Get the instance of the {@link Chunky} class
     */
    private void getNewChunky() {
        final Plugin chunkyPlugin = Bukkit.getPluginManager().getPlugin("Chunky");
        if (chunkyPlugin == null) {
            ChunkyDiscord.error("Chunky is not enabled, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(getPlugin());
            return;
        }
        try {
            chunky = (Chunky) chunkyPlugin.getClass().getDeclaredMethod("getChunky").invoke(chunkyPlugin);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            ChunkyDiscord.error("Failed getting Chunky", e);
        }
    }

    @Nullable
    private Icon getIcon() {
        // Go through all the files in the plugin's data folder and return the file if it is an image, otherwise return avatar.jpg from the plugin's jar
        File[] files = getDataFolder().listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().matches(".*\\.(png|jpg|jpeg)")) {
                    try {
                        return Icon.from(Files.readAllBytes(file.toPath()));
                    } catch (IOException e) {
                        ChunkyDiscord.error("Failed to load icon from file", e);
                    }
                }
            }
        }
        try {
            return Icon.from(getResource("avatar.jpg"));
        } catch (IOException e) {
            ChunkyDiscord.error("Failed to load icon from jar", e);
        }
        return null;
    }

    private void startMetrics() {
        final int pluginId = 17164;
        final Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("chunky_version", () -> getChunky().getVersion().toString()));
    }
    private void printStartupMessage() {
        String[] message = {
                "§8§m----------------------------------------",
                "§6§lChunkyDiscord §7v" + getDescription().getVersion(),
                "§7Made by §6§l" + getDescription().getAuthors().get(0),
                "§7Discord bot: §6§l" + getJda().getSelfUser().getAsTag(),
                "§7Bug reports: §6§l" + getDescription().getWebsite() + "/issues",
                "§8§m----------------------------------------"
        };
        for (String line : message) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}
