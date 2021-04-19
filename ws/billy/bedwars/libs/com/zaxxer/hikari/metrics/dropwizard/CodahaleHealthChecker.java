

package ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.dropwizard;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import com.codahale.metrics.Metric;
import java.util.SortedMap;
import java.util.Properties;

import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import ws.billy.bedwars.libs.com.zaxxer.hikari.HikariConfig;
import ws.billy.bedwars.libs.com.zaxxer.hikari.pool.HikariPool;

public final class CodahaleHealthChecker
{
    public static void registerHealthChecks(final HikariPool hikariPool, final HikariConfig hikariConfig, final HealthCheckRegistry healthCheckRegistry) {
        final Properties healthCheckProperties = hikariConfig.getHealthCheckProperties();
        final MetricRegistry metricRegistry = (MetricRegistry)hikariConfig.getMetricRegistry();
        healthCheckRegistry.register(MetricRegistry.name(hikariConfig.getPoolName(), new String[] { "pool", "ConnectivityCheck" }), (HealthCheck)new ConnectivityHealthCheck(hikariPool, Long.parseLong(healthCheckProperties.getProperty("connectivityCheckTimeoutMs", String.valueOf(hikariConfig.getConnectionTimeout())))));
        final long long1 = Long.parseLong(healthCheckProperties.getProperty("expected99thPercentileMs", "0"));
        if (metricRegistry != null && long1 > 0L) {
            final SortedMap timers = metricRegistry.getTimers((s, metric) -> s.equals(MetricRegistry.name(hikariConfig.getPoolName(), new String[] { "pool", "Wait" })));
            if (!timers.isEmpty()) {
                healthCheckRegistry.register(MetricRegistry.name(hikariConfig.getPoolName(), new String[] { "pool", "Connection99Percent" }), (HealthCheck)new Connection99Percent(timers.entrySet().iterator().next().getValue(), long1));
            }
        }
    }
    
    private CodahaleHealthChecker() {
    }
    
    private static class ConnectivityHealthCheck extends HealthCheck
    {
        private final HikariPool pool;
        private final long checkTimeoutMs;
        
        ConnectivityHealthCheck(final HikariPool pool, final long n) {
            this.pool = pool;
            this.checkTimeoutMs = ((n > 0L && n != 2147483647L) ? n : TimeUnit.SECONDS.toMillis(10L));
        }
        
        protected HealthCheck.Result check() {
            try {
                final Connection connection = this.pool.getConnection(this.checkTimeoutMs);
                try {
                    final HealthCheck.Result healthy = HealthCheck.Result.healthy();
                    if (connection != null) {
                        connection.close();
                    }
                    return healthy;
                }
                catch (Throwable t) {
                    if (connection != null) {
                        try {
                            connection.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
            }
            catch (SQLException ex) {
                return HealthCheck.Result.unhealthy((Throwable)ex);
            }
        }
    }
    
    private static class Connection99Percent extends HealthCheck
    {
        private final Timer waitTimer;
        private final long expected99thPercentile;
        
        Connection99Percent(final Timer waitTimer, final long expected99thPercentile) {
            this.waitTimer = waitTimer;
            this.expected99thPercentile = expected99thPercentile;
        }
        
        protected HealthCheck.Result check() {
            final long millis = TimeUnit.NANOSECONDS.toMillis(Math.round(this.waitTimer.getSnapshot().get99thPercentile()));
            return (millis <= this.expected99thPercentile) ? HealthCheck.Result.healthy() : HealthCheck.Result.unhealthy("99th percentile connection wait time of %dms exceeds the threshold %dms", new Object[] { millis, this.expected99thPercentile });
        }
    }
}
