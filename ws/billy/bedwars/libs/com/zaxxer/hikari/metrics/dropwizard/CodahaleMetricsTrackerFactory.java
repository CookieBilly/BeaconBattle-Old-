

package ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.dropwizard;

import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.IMetricsTracker;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.PoolStats;
import com.codahale.metrics.MetricRegistry;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.MetricsTrackerFactory;

public final class CodahaleMetricsTrackerFactory implements MetricsTrackerFactory
{
    private final MetricRegistry registry;
    
    public CodahaleMetricsTrackerFactory(final MetricRegistry registry) {
        this.registry = registry;
    }
    
    public MetricRegistry getRegistry() {
        return this.registry;
    }
    
    @Override
    public IMetricsTracker create(final String s, final PoolStats poolStats) {
        return new CodaHaleMetricsTracker(s, poolStats, this.registry);
    }
}
