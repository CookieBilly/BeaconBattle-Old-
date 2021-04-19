

package ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.dropwizard;

import java.util.concurrent.TimeUnit;
import com.codahale.metrics.Metric;
import java.util.Objects;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.PoolStats;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.IMetricsTracker;

public final class CodaHaleMetricsTracker implements IMetricsTracker
{
    private final String poolName;
    private final Timer connectionObtainTimer;
    private final Histogram connectionUsage;
    private final Histogram connectionCreation;
    private final Meter connectionTimeoutMeter;
    private final MetricRegistry registry;
    private static final String METRIC_CATEGORY = "pool";
    private static final String METRIC_NAME_WAIT = "Wait";
    private static final String METRIC_NAME_USAGE = "Usage";
    private static final String METRIC_NAME_CONNECT = "ConnectionCreation";
    private static final String METRIC_NAME_TIMEOUT_RATE = "ConnectionTimeoutRate";
    private static final String METRIC_NAME_TOTAL_CONNECTIONS = "TotalConnections";
    private static final String METRIC_NAME_IDLE_CONNECTIONS = "IdleConnections";
    private static final String METRIC_NAME_ACTIVE_CONNECTIONS = "ActiveConnections";
    private static final String METRIC_NAME_PENDING_CONNECTIONS = "PendingConnections";
    private static final String METRIC_NAME_MAX_CONNECTIONS = "MaxConnections";
    private static final String METRIC_NAME_MIN_CONNECTIONS = "MinConnections";
    
    CodaHaleMetricsTracker(final String poolName, final PoolStats poolStats, final MetricRegistry registry) {
        this.poolName = poolName;
        this.registry = registry;
        this.connectionObtainTimer = registry.timer(MetricRegistry.name(poolName, new String[] { "pool", "Wait" }));
        this.connectionUsage = registry.histogram(MetricRegistry.name(poolName, new String[] { "pool", "Usage" }));
        this.connectionCreation = registry.histogram(MetricRegistry.name(poolName, new String[] { "pool", "ConnectionCreation" }));
        this.connectionTimeoutMeter = registry.meter(MetricRegistry.name(poolName, new String[] { "pool", "ConnectionTimeoutRate" }));
        final String name = MetricRegistry.name(poolName, new String[] { "pool", "TotalConnections" });
        Objects.requireNonNull(poolStats);
        registry.register(name, (Metric)poolStats::getTotalConnections);
        final String name2 = MetricRegistry.name(poolName, new String[] { "pool", "IdleConnections" });
        Objects.requireNonNull(poolStats);
        registry.register(name2, (Metric)poolStats::getIdleConnections);
        final String name3 = MetricRegistry.name(poolName, new String[] { "pool", "ActiveConnections" });
        Objects.requireNonNull(poolStats);
        registry.register(name3, (Metric)poolStats::getActiveConnections);
        final String name4 = MetricRegistry.name(poolName, new String[] { "pool", "PendingConnections" });
        Objects.requireNonNull(poolStats);
        registry.register(name4, (Metric)poolStats::getPendingThreads);
        final String name5 = MetricRegistry.name(poolName, new String[] { "pool", "MaxConnections" });
        Objects.requireNonNull(poolStats);
        registry.register(name5, (Metric)poolStats::getMaxConnections);
        final String name6 = MetricRegistry.name(poolName, new String[] { "pool", "MinConnections" });
        Objects.requireNonNull(poolStats);
        registry.register(name6, (Metric)poolStats::getMinConnections);
    }
    
    @Override
    public void close() {
        this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "Wait" }));
        this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "Usage" }));
        this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "ConnectionCreation" }));
        this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "ConnectionTimeoutRate" }));
        this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "TotalConnections" }));
        this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "IdleConnections" }));
        this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "ActiveConnections" }));
        this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "PendingConnections" }));
        this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "MaxConnections" }));
        this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "MinConnections" }));
    }
    
    @Override
    public void recordConnectionAcquiredNanos(final long n) {
        this.connectionObtainTimer.update(n, TimeUnit.NANOSECONDS);
    }
    
    @Override
    public void recordConnectionUsageMillis(final long n) {
        this.connectionUsage.update(n);
    }
    
    @Override
    public void recordConnectionTimeout() {
        this.connectionTimeoutMeter.mark();
    }
    
    @Override
    public void recordConnectionCreatedMillis(final long n) {
        this.connectionCreation.update(n);
    }
    
    public Timer getConnectionAcquisitionTimer() {
        return this.connectionObtainTimer;
    }
    
    public Histogram getConnectionDurationHistogram() {
        return this.connectionUsage;
    }
    
    public Histogram getConnectionCreationHistogram() {
        return this.connectionCreation;
    }
}
