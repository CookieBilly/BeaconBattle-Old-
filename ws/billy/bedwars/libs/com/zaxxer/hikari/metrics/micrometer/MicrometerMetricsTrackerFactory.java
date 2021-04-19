

package ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.micrometer;

import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.IMetricsTracker;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.PoolStats;
import io.micrometer.core.instrument.MeterRegistry;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.MetricsTrackerFactory;

public class MicrometerMetricsTrackerFactory implements MetricsTrackerFactory
{
    private final MeterRegistry registry;
    
    public MicrometerMetricsTrackerFactory(final MeterRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public IMetricsTracker create(final String s, final PoolStats poolStats) {
        return new MicrometerMetricsTracker(s, poolStats, this.registry);
    }
}
