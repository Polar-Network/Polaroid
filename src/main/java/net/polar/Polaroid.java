package net.polar;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
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
import net.minestom.server.utils.validate.Check;
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

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public class Polaroid {

    private static final Logger LOGGER = LoggerFactory.getLogger(Polaroid.class);
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    private static boolean onlineMode;
    private static boolean debugMode;
    private static String address;
    private static int port;
    private static ProxySettings proxySettings;
    private static final EventNode<Event> EVENT_NODE = EventNode.all("Polaroid");
    private static TickTrackingInstanceContainer defaultInstance = new TickTrackingInstanceContainer(UUID.randomUUID());
    private static MotdProvider motdProvider;

    private Polaroid(@NotNull LaunchArguments launchArguments) {
        onlineMode = launchArguments.onlineMode();
        debugMode = launchArguments.debugMode();
        address = launchArguments.address();
        port = launchArguments.port();
        proxySettings = launchArguments.proxySettings();
        INITIALIZED.set(true);
    }

    public void onEnable() {
        Check.stateCondition(!INITIALIZED.get(), "Polaroid has not been initialized yet!");

        MinecraftServer server = MinecraftServer.init();
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
        eh.addListener(PlayerLoginEvent.class, event -> {
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
    }

    public static void initServer(String... args) {
        LaunchArguments launchArguments = LaunchArguments.parse(args);
        Polaroid polaroid = new Polaroid(launchArguments);
        polaroid.onEnable();
    }

    public static void addShutdownTask(@NotNull Runnable runnable) {MinecraftServer.getSchedulerManager().buildShutdownTask(runnable);}
    public static void registerListeners(@NotNull EventListener<?>... listeners) {Arrays.stream(listeners).forEach(EVENT_NODE::addListener);}
    public static void registerCommands(@NotNull Command... commands) {Arrays.stream(commands).forEach(MinecraftServer.getCommandManager()::register);}
    public static boolean isOnlineMode() {return onlineMode;}
    public static boolean isDebugMode() {return debugMode;}
    public static String getAddress() {return address;}
    public static int getPort() {return port;}
    public static @NotNull ProxySettings getProxySettings() {return proxySettings;}
    public static @NotNull Logger getLogger() {return LOGGER;}
    public static @NotNull TickTrackingInstanceContainer getDefaultInstance() {return defaultInstance;}
    public static void setDefaultInstance(@NotNull TickTrackingInstanceContainer defaultInstance) {
        Check.notNull(defaultInstance, "Default instance cannot be null!");
        Polaroid.defaultInstance = defaultInstance;
    }
    public static @Nullable MotdProvider getMotdProvider() {return motdProvider;}
    public static void setMotdProvider(@Nullable MotdProvider motdProvider) {Polaroid.motdProvider = motdProvider;}

}
