package net.polar;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.timer.Task;
import net.minestom.server.utils.validate.Check;
import net.polar.database.PolaroidDatabase;
import net.polar.gui.Gui;
import net.polar.gui.GuiClickable;
import net.polar.launch.LaunchArguments;
import net.polar.launch.ProxySettings;
import net.polar.motd.MotdProvider;
import net.polar.world.TickTrackingInstanceContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

/**
 * This is a wrapper for the {@link MinecraftServer} server.
 * Internal methods are not documented.
 * {@link Polaroid#initServer()} for the main method
 */
@SuppressWarnings("unused")
public final class Polaroid {

    private static final Logger LOGGER = LoggerFactory.getLogger(Polaroid.class);
    private static volatile boolean INITIALIZED = false;
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

    private Polaroid(@NotNull LaunchArguments launchArguments) {
        onlineMode = launchArguments.onlineMode();
        debugMode = launchArguments.debug();
        String host = launchArguments.host();
        String[] split = host.split(":");
        address = split[0];
        port = Integer.parseInt(split[1]);
        database = new PolaroidDatabase(launchArguments.mongoUri());
        proxySettings = launchArguments.proxySettings();
        maxPlayers = launchArguments.maxPlayers();
        INITIALIZED = true;
    }

    private void onEnable() {
        Check.stateCondition(!INITIALIZED, "Polaroid has not been initialized yet!");

        MinecraftServer server = MinecraftServer.init();
        defaultInstance = new TickTrackingInstanceContainer(UUID.randomUUID());
        MinecraftServer.getDimensionTypeManager().addDimension(TickTrackingInstanceContainer.FULLBRIGHT_DIMENSION);
        MinecraftServer.setBrandName("Polaroid");

        if (onlineMode) {MojangAuth.init();}
        if (proxySettings.enabled()) {VelocityProxy.enable(proxySettings.secret());}
        registerInternalListeners();
        MinecraftServer.getGlobalEventHandler().addChild(EVENT_NODE);

        server.start(address, port);
        getLogger().info("Polaroid initialized on address " + address + ":" + port);
    }

    private void registerInternalListeners() {
        GlobalEventHandler eh = MinecraftServer.getGlobalEventHandler();
        eh.addListener(PlayerLoginEvent.class, event -> event.setSpawningInstance(defaultInstance));
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
    }

    /**
     * Initializes the server with the given arguments.
     * Automatically builds {@link LaunchArguments}
     */
    public static void initServer() {
        Polaroid polaroid = new Polaroid(LaunchArguments.defaults());
        polaroid.onEnable();
    }

    /**
     * Initializes the server with the given arguments.
     * @param arguments - the arguments to use
     */
    public static void initServer(@NotNull LaunchArguments arguments) {
        Polaroid polaroid = new Polaroid(arguments);
        polaroid.onEnable();
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
            getLogger().info("Attempted sending player " + player.getUsername() + " to server " + proxyServerId);
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
     * @return the SLF4J Logger instance for Polaroid
     */
    public static @NotNull Logger getLogger() {
        return LOGGER;
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
}
