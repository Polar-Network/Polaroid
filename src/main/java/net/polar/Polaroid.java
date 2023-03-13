package net.polar;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSkinInitEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.timer.Task;
import net.minestom.server.utils.validate.Check;
import net.polar.database.PolaroidDatabase;
import net.polar.database.rank.RankProvider;
import net.polar.database.suffix.SuffixProvider;
import net.polar.entity.player.PolaroidPlayer;
import net.polar.gui.Gui;
import net.polar.gui.GuiClickable;
import net.polar.launch.LaunchArguments;
import net.polar.launch.ProxySettings;
import net.polar.motd.MotdProvider;
import net.polar.utils.chat.ChatColor;
import net.polar.world.TickTrackingInstanceContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

/**
 * This is a wrapper for the {@link MinecraftServer} server.
 * {@link Polaroid#initServer()} should be used in your application to start the Polaroid server.
 */
@SuppressWarnings("unused")
public final class Polaroid {

    private static final ComponentLogger LOGGER = ComponentLogger.logger(Polaroid.class);
    private static final EventNode<Event> EVENT_NODE = EventNode.all("Polaroid");
    private static final Path LOCAL_PATH = Path.of(".");
    private static boolean onlineMode;
    private static boolean debugMode;
    private static String address;
    private static int port;
    private static int maxPlayers;
    private static PolaroidDatabase database;
    private static ProxySettings proxySettings;
    private static TickTrackingInstanceContainer defaultInstance;
    private static MotdProvider motdProvider;
    private static boolean enableDefaultSkinOverwrite = false;
    private static PlayerSkin defaultSkin;
    private static RankProvider rankProvider;
    private static SuffixProvider suffixProvider;

    private Polaroid(@NotNull LaunchArguments launchArguments) {

        onlineMode = launchArguments.onlineMode();
        debugMode = launchArguments.debug();
        String host = launchArguments.host();
        String[] split = host.split(":");
        address = split[0];
        port = Integer.parseInt(split[1]);
        database = new PolaroidDatabase(launchArguments.mongoUri(), launchArguments.redisUrl());
        proxySettings = launchArguments.proxySettings();
        maxPlayers = launchArguments.maxPlayers();

        MinecraftServer server = MinecraftServer.init();
        defaultInstance = new TickTrackingInstanceContainer(UUID.randomUUID());
        MinecraftServer.getDimensionTypeManager().addDimension(TickTrackingInstanceContainer.FULLBRIGHT_DIMENSION);
        MinecraftServer.setBrandName("Polaroid");
        MinecraftServer.getConnectionManager().setPlayerProvider(PolaroidPlayer::new);

        if (onlineMode) {MojangAuth.init();}
        if (proxySettings.enabled()) {VelocityProxy.enable(proxySettings.secret());}

        registerInternalListeners();
        rankProvider = RankProvider.defaultProvider();
        rankProvider.refreshRanks();
        suffixProvider = SuffixProvider.defaultProvider();
        MinecraftServer.getGlobalEventHandler().addChild(EVENT_NODE);

        defaultSkin = PlayerSkin.fromUuid("fdc0fc16-45c1-4432-ae1a-f9e201dd7aeb");
        MiniMessenger.create(database.getClient().getDatabase("minimessage"), "tags").thenAccept(mm -> ChatColor.replaceHandler(mm));
        server.start(address, port);
        getLogger().info(ChatColor.color("<green>Polaroid initialized on address " + address + ":" + port));
    }

    private void registerInternalListeners() {
        GlobalEventHandler eh = MinecraftServer.getGlobalEventHandler();
        eh.addListener(PlayerLoginEvent.class, event -> {
            if (!(event.getPlayer() instanceof PolaroidPlayer)) {
                event.getPlayer().kick(Component.text("Invalid player type! If you're an admin please check your code for your PlayerProvider to use PolaroidPlayer", NamedTextColor.RED));
                return;
            }
            event.setSpawningInstance(defaultInstance);
        });
        eh.addListener(ServerListPingEvent.class, event -> {

            ResponseData data;
            if (motdProvider != null) data = motdProvider.provide();
            else data = MotdProvider.DEFAULT_MOTD;

            event.setResponseData(data);
        });
        eh.addListener(InventoryPreClickEvent.class, event -> {
            if (!(event.getInventory() instanceof Gui gui)) return;
            event.setCancelled(true);

            GuiClickable clickable = gui.getClickableAt(event.getSlot());
            if (clickable != null) clickable.onClick(event.getClickType(), event.getPlayer());
        });
        eh.addListener(PlayerSkinInitEvent.class, event -> {
            if (enableDefaultSkinOverwrite && event.getSkin() == null) {
                event.setSkin(defaultSkin);
            }
        });
        eh.addListener(PlayerChatEvent.class, event -> {
            PolaroidPlayer player = (PolaroidPlayer) event.getPlayer();
            event.setChatFormat(chatEvent -> ChatColor.color(
                    PolaroidPlayer.getReplacer().replace(PolaroidPlayer.getChatFormat(), player)
                            .replace("%message%", chatEvent.getMessage()
            )));
        });
    }

    /**
     * Initializes the server with the given arguments.
     * Automatically builds {@link LaunchArguments}
     */
    public static void initServer() {
        initServer(LaunchArguments.defaults());
    }

    /**
     * Initializes the server with the given arguments.
     * @param arguments - the arguments to use
     */
    public static void initServer(@NotNull LaunchArguments arguments) {
        Polaroid polaroid = new Polaroid(arguments);
    }

    /**
     * Adds a shutdown task to the Minestom shutdown task list
     * @param runnable - the task to be executed
     */
    public static void addShutdownTask(@NotNull Runnable runnable) {
        MinecraftServer.getSchedulerManager().buildShutdownTask(runnable);
    }

    /**
     * Registers a Minestom event listener to the Polaroid event node
     * @param listeners - the listeners to be registered
     */
    public static void registerListeners(@NotNull EventListener<?>... listeners) {
        Arrays.stream(listeners).forEach(EVENT_NODE::addListener);
    }

    /**
     * Registers an array of commands to the {@link MinecraftServer#getCommandManager()}
     * @param commands - the array of commands to register
     */
    public static void registerCommands(@NotNull Command... commands) {
        Arrays.stream(commands).forEach(MinecraftServer.getCommandManager()::register);
    }

    /**
     * Sends a connected player to a different server. Only works if the server is in proxy mode.
     * Please keep in mind this only works if the proxy software has Bungee plugin channel support.
     * @param player - the player to send
     * @param proxyServerId - the id of the server to send the player to
     * @throws IllegalStateException if the server is not in proxy mode
     */
    public static void sendPlayerToServer(@NotNull Player player, @NotNull String proxyServerId) {
        if (!proxySettings.enabled()) {
            throw new IllegalStateException("Cannot send player to server if proxy mode is not enabled!");
        }
        if (debugMode) {
            getLogger().info(ChatColor.color("<blue>Attempted sending player " + player.getUsername() + " to server " + proxyServerId));
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(proxyServerId);
        player.sendPluginMessage("BungeeCord", out.toByteArray());
    }

    public static @NotNull Task.Builder buildTask(@NotNull Runnable runnable) {
        return MinecraftServer.getSchedulerManager().buildTask(runnable);
    }

    /**
     * @return true if the server is in online mode, false otherwise
     */
    public static boolean isOnlineMode() {
        return onlineMode;
    }

    /**
     * @return true if the server is in debug mode, false otherwise
     */
    public static boolean isDebugMode() {
        return debugMode;
    }

    /**
     * @return the Address String the server will start on
     */
    public static String getAddress() {
        return address;
    }

    /**
     * @return the port the server will start on
     */
    public static int getPort() {
        return port;
    }

    /**
     * @return the specified {@link ProxySettings} for the server
     */
    public static @NotNull ProxySettings getProxySettings() {
        return proxySettings;
    }

    /**
     * @return the SLF4J Logger instance for Polaroid. This uses {@link ComponentLogger} for components.
     */
    public static @NotNull ComponentLogger getLogger() {
        return LOGGER;
    }

    /**
     * Should Polaroid automatically overwrite the default skin for players?
     * @param enabled - true if Polaroid should overwrite the default skin, false otherwise
     */
    public static void toggleDefaultSkinOverwrite(boolean enabled) {
        enableDefaultSkinOverwrite = enabled;
    }

    /**
     * @return true if Polaroid is set to overwrite the default skin, false otherwise
     */
    public static boolean isDefaultSkinOverwriteEnabled() {
        return enableDefaultSkinOverwrite;
    }

    /**
     * @return the {@link PolaroidDatabase} instance for the server
     */
    public static @NotNull PolaroidDatabase getDatabase() {
        return database;
    }


    /**
     * @return the default {@link TickTrackingInstanceContainer} for the server.
     */
    public static @NotNull TickTrackingInstanceContainer getDefaultInstance() {
        return defaultInstance;
    }

    /**
     * Sets the default {@link TickTrackingInstanceContainer} for the server.
     * @param defaultInstance - the new default instance
     */
    public static void setDefaultInstance(@NotNull TickTrackingInstanceContainer defaultInstance) {
        Check.notNull(defaultInstance, "Default instance cannot be null!");
        Polaroid.defaultInstance = defaultInstance;
    }

    /**
     * @return the {@link MotdProvider} for the server
     */
    public static @Nullable MotdProvider getMotdProvider() {
        return motdProvider;
    }

    /**
     * Sets the {@link MotdProvider} for the server
     * @param provider - the new MotdProvider
     */
    public static void setMotdProvider(@Nullable MotdProvider provider) {
        motdProvider = provider;
    }

    /**
     * @return the {@link Path} to the local Polaroid directory (where the server is running from)
     */
    public static @NotNull Path getLocalPath() {
        return LOCAL_PATH;
    }

    /**
     * @return the amount of players the server can hold. This number is cosmetic frankly
     */
    public static int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Set the default skin to overwrite to
     * @param skin - the new default skin
     */
    public static void setDefaultSkin(@NotNull PlayerSkin skin) {
        defaultSkin = skin;
    }

    /**
     * @return the default skin to overwrite to
     */
    public static @NotNull PlayerSkin getDefaultSkin() {
        return defaultSkin;
    }


    /**
     * @return the {@link RankProvider} for the servers players
     */
    public static @NotNull RankProvider getRankProvider() {
        return rankProvider;
    }

    /**
     * Sets the {@link RankProvider} for the servers players
     * @param rankProvider - the new {@link RankProvider}
     */
    public static void setRankProvider(@NotNull RankProvider rankProvider) {
        Polaroid.rankProvider = rankProvider;
    }

    /**
     * @return the {@link SuffixProvider} for the servers players
     */
    public static @NotNull SuffixProvider getSuffixProvider() {
        return suffixProvider;
    }

    /**
     * Sets the {@link SuffixProvider} for the servers players
     * @param suffixProvider - the new {@link SuffixProvider}
     */
    public static void setSuffixProvider(@NotNull SuffixProvider suffixProvider) {
        Polaroid.suffixProvider = suffixProvider;
    }
}
