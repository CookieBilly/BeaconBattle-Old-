

package ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.micrometer;

import java.util.concurrent.TimeUnit;
import io.micrometer.core.instrument.MeterRegistry;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.PoolStats;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.IMetricsTracker;

public class MicrometerMetricsTracker implements IMetricsTracker
{
    public static final String HIKARI_METRIC_NAME_PREFIX = "hikaricp";
    private static final String METRIC_CATEGORY = "pool";
    private static final String METRIC_NAME_WAIT = "hikaricp.connections.acquire";
    private static final String METRIC_NAME_USAGE = "hikaricp.connections.usage";
    private static final String METRIC_NAME_CONNECT = "hikaricp.connections.creation";
    private static final String METRIC_NAME_TIMEOUT_RATE = "hikaricp.connections.timeout";
    private static final String METRIC_NAME_TOTAL_CONNECTIONS = "hikaricp.connections";
    private static final String METRIC_NAME_IDLE_CONNECTIONS = "hikaricp.connections.idle";
    private static final String METRIC_NAME_ACTIVE_CONNECTIONS = "hikaricp.connections.active";
    private static final String METRIC_NAME_PENDING_CONNECTIONS = "hikaricp.connections.pending";
    private static final String METRIC_NAME_MAX_CONNECTIONS = "hikaricp.connections.max";
    private static final String METRIC_NAME_MIN_CONNECTIONS = "hikaricp.connections.min";
    private final Timer connectionObtainTimer;
    private final Counter connectionTimeoutCounter;
    private final Timer connectionUsage;
    private final Timer connectionCreation;
    private final Gauge totalConnectionGauge;
    private final Gauge idleConnectionGauge;
    private final Gauge activeConnectionGauge;
    private final Gauge pendingConnectionGauge;
    private final Gauge maxConnectionGauge;
    private final Gauge minConnectionGauge;
    private final PoolStats poolStats;
    
    MicrometerMetricsTracker(final String s, final PoolStats poolStats, final MeterRegistry meterRegistry) {
        this.poolStats = poolStats;
        this.connectionObtainTimer = Timer.builder("hikaricp.connections.acquire").description("Connection acquire time").tags(new String[] { "pool", s }).register(meterRegistry);
        this.connectionCreation = Timer.builder("hikaricp.connections.creation").description("Connection creation time").tags(new String[] { "pool", s }).register(meterRegistry);
        this.connectionUsage = Timer.builder("hikaricp.connections.usage").description("Connection usage time").tags(new String[] { "pool", s }).register(meterRegistry);
        this.connectionTimeoutCounter = Counter.builder("hikaricp.connections.timeout").description("Connection timeout total count").tags(new String[] { "pool", s }).register(meterRegistry);
        this.totalConnectionGauge = Gauge.builder("hikaricp.connections", (Object)poolStats, PoolStats::getTotalConnections).description("Total connections").tags(new String[] { "pool", s }).register(meterRegistry);
        this.idleConnectionGauge = Gauge.builder("hikaricp.connections.idle", (Object)poolStats, PoolStats::getIdleConnections).description("Idle connections").tags(new String[] { "pool", s }).register(meterRegistry);
        this.activeConnectionGauge = Gauge.builder("hikaricp.connections.active", (Object)poolStats, PoolStats::getActiveConnections).description("Active connections").tags(new String[] { "pool", s }).register(meterRegistry);
        this.pendingConnectionGauge = Gauge.builder("hikaricp.connections.pending", (Object)poolStats, PoolStats::getPendingThreads).description("Pending threads").tags(new String[] { "pool", s }).register(meterRegistry);
        this.maxConnectionGauge = Gauge.builder("hikaricp.connections.max", (Object)poolStats, PoolStats::getMaxConnections).description("Max connections").tags(new String[] { "pool", s }).register(meterRegistry);
        this.minConnectionGauge = Gauge.builder("hikaricp.connections.min", (Object)poolStats, PoolStats::getMinConnections).description("Min connections").tags(new String[] { "pool", s }).register(meterRegistry);
    }
    
    @Override
    public void recordConnectionAcquiredNanos(final long n) {
        this.connectionObtainTimer.record(n, TimeUnit.NANOSECONDS);
    }
    
    @Override
    public void recordConnectionUsageMillis(final long n) {
        this.connectionUsage.record(n, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void recordConnectionTimeout() {
        this.connectionTimeoutCounter.increment();
    }
    
    @Override
    public void recordConnectionCreatedMillis(final long n) {
        this.connectionCreation.record(n, TimeUnit.MILLISECONDS);
    }
}
