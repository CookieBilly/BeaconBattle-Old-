

package ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.prometheus;

import java.util.concurrent.ConcurrentHashMap;
import io.prometheus.client.Collector;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.IMetricsTracker;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.PoolStats;
import io.prometheus.client.CollectorRegistry;
import java.util.Map;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.MetricsTrackerFactory;

public class PrometheusMetricsTrackerFactory implements MetricsTrackerFactory
{
    private static final Map<CollectorRegistry, RegistrationStatus> registrationStatuses;
    private final HikariCPCollector collector;
    private final CollectorRegistry collectorRegistry;
    
    public PrometheusMetricsTrackerFactory() {
        this(CollectorRegistry.defaultRegistry);
    }
    
    public PrometheusMetricsTrackerFactory(final CollectorRegistry collectorRegistry) {
        this.collector = new HikariCPCollector();
        this.collectorRegistry = collectorRegistry;
    }
    
    @Override
    public IMetricsTracker create(final String s, final PoolStats poolStats) {
        this.registerCollector(this.collector, this.collectorRegistry);
        this.collector.add(s, poolStats);
        return new PrometheusMetricsTracker(s, this.collectorRegistry, this.collector);
    }
    
    private void registerCollector(final Collector collector, final CollectorRegistry key) {
        if (PrometheusMetricsTrackerFactory.registrationStatuses.putIfAbsent(key, RegistrationStatus.REGISTERED) == null) {
            collector.register(key);
        }
    }
    
    static {
        registrationStatuses = new ConcurrentHashMap<CollectorRegistry, RegistrationStatus>();
    }
    
    public enum RegistrationStatus
    {
        REGISTERED;
    }
}
