package net.polar.profiler;

import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.GcInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.utils.time.TimeUnit;
import net.polar.utils.ChatColor;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class ServerProfiler {

    private ServerProfiler() {}

    private static final AtomicReference<TickMonitor> lastTick = new AtomicReference<>();
    private static final long byteToMBConstant = 1024 * 1024;


    /**
     * Initializes the server profiler.
     * This method has no safety checks, so it is recommended to only call this method once.
     */
    public static void init() {
        MinecraftServer.getGlobalEventHandler().addListener(ServerTickMonitorEvent.class, event -> {
            lastTick.set(event.getTickMonitor());
        });
    }

    /**
     * Gets server profiler data from the server, this includes recent GC calls, memory used/free/total, MS per tick, TPS and more.
     * @return The server profiler data.
     */
    public static Component getServerProfilerData() {
        StringBuilder sb = new StringBuilder();
        sb.append("<gray>Server Profiler:\n")
                .append("  TPS: ").append("<").append(getTPSHex()).append(">").append(getTPS()).append("<gray> | Last Tick MS: ").append("<").append(getTPSHex()).append(">").append(getTickMS()).append("<gray>\n")
                .append("  Online: <gold>").append(MinecraftServer.getConnectionManager().getOnlinePlayers().size()).append("<gold>\n")
                .append("  Uptime: <gold>").append(getUptimeReadable()).append("<gray>\n")
                .append("  Memory Usage: <gold>").append(getUsedMemory()).append(" MB <gray>(").append(getUsedRamPercentage()).append("%)<gray>\n")
                .append("  Memory Available: <gold>").append(getAllocatedMemory()).append(" MB<gray>\n")
                .append("  Memory Free: <gold>").append(getFreeMemory()).append(" MB<gray>\n")
                .append("<gray>Garbage Collection Info:\n");
        getGcInfo().forEach((name, info) -> {
            String lastRunText;
            if (info == null) lastRunText = "No GC has run yet";
            else lastRunText = ofGreatestUnit(Duration.ofMillis(getUptime() - info.getEndTime()));
            sb.append("  <gold>").append(name).append("<gray>:\n").append("    Last Run: <gold>").append(lastRunText).append("<gray>\n");
        });
        return ChatColor.color(sb.toString());
    }

    private static String ofGreatestUnit(Duration duration) {
        if (duration.compareTo(TimeUnit.DAY.getDuration()) > -1) return duration.toDays() + " days";
        if (duration.compareTo(TimeUnit.HOUR.getDuration()) > -1) return duration.toHours() + " hours";
        if (duration.compareTo(TimeUnit.MINUTE.getDuration()) > -1) return duration.toMinutes() + " minutes";
        if (duration.compareTo(TimeUnit.SECOND.getDuration()) > -1) return duration.getSeconds() + " seconds";
        if (duration.compareTo(TimeUnit.MILLISECOND.getDuration()) > -1) return duration.toMillis() + " milliseconds";
        return duration.toNanos() + " nanoseconds";
    }

    private static String getUptimeReadable() {
        long uptime = getUptime();
        int days = (int) Math.floor(uptime / 86400000);
        int hours = (int) Math.floor((uptime % 86400000) / 3600000);
        int minutes = (int) Math.floor(((uptime % 86400000) % 3600000) / 60000);
        int seconds = (int) Math.floor((((uptime % 86400000) % 3600000) % 60000) / 1000);
        return days + "days " + hours + "hours " + minutes + "minutes " + seconds + "seconds";
    }

    private static long getUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    private static String getTPSHex() {
        double tps = getTPS();
        return TextColor.lerp((float) tps, NamedTextColor.RED, NamedTextColor.GREEN).asHexString();
    }

    private static Map<String, GcInfo> getGcInfo() {
        Map<String, GcInfo> map = new HashMap<>();
        for (var javaBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            GarbageCollectorMXBean bean = (GarbageCollectorMXBean) javaBean;
            map.put(bean.getName(), bean.getLastGcInfo());
        }
        return map;
    }

    private static double getTPS() {
        double ms = getTickMS();
        if (ms == 0.0D) return 20.0D;
        return Math.min(MinecraftServer.TICK_PER_SECOND, Math.floor(1000.0D / ms));
    }

    private static double getUsedRamPercentage() {
        double value = (getUsedMemory() / getAllocatedMemory()) * 100;
        return Math.floor(value * 100) / 100;
    }

    private static float getTPSLerpDiff() {
        return (float) (getTPS() / MinecraftServer.TICK_PER_SECOND);
    }

    private static long getAllocatedMemory() {
        return Runtime.getRuntime().totalMemory() / byteToMBConstant;
    }

    private static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory() / byteToMBConstant;
    }

    private static long getUsedMemory() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / byteToMBConstant;
    }

    private static double getTickMS() {
        if (lastTick.get() == null) return 0.0D;
        return lastTick.get().getTickTime();
    }
}
