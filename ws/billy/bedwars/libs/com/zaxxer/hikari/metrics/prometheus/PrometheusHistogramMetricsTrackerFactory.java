

package ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.prometheus;

import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.IMetricsTracker;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.PoolStats;
import io.prometheus.client.CollectorRegistry;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.MetricsTrackerFactory;

public class PrometheusHistogramMetricsTrackerFactory implements MetricsTrackerFactory
{
    private HikariCPCollector collector;
    private CollectorRegistry collectorRegistry;
    
    public PrometheusHistogramMetricsTrackerFactory() {
        this.collectorRegistry = CollectorRegistry.defaultRegistry;
    }
    
    public PrometheusHistogramMetricsTrackerFactory(final CollectorRegistry collectorRegistry) {
        this.collectorRegistry = collectorRegistry;
    }
    
    @Override
    public IMetricsTracker create(final String s, final PoolStats poolStats) {
        this.getCollector().add(s, poolStats);
        return new PrometheusHistogramMetricsTracker(s, this.collectorRegistry);
    }
    
    private HikariCPCollector getCollector() {
        if (this.collector == null) {
            this.collector = (HikariCPCollector)new HikariCPCollector().register(this.collectorRegistry);
        }
        return this.collector;
    }
}
