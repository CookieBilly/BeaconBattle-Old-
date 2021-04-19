

package ws.billy.bedwars.libs.com.zaxxer.hikari.metrics;

public interface MetricsTrackerFactory
{
    IMetricsTracker create(final String p0, final PoolStats p1);
}
