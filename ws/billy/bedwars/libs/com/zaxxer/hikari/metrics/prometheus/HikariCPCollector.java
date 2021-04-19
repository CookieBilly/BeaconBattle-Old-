

package ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.prometheus;

import java.util.Collections;
import io.prometheus.client.GaugeMetricFamily;
import java.util.function.Function;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.PoolStats;
import java.util.Map;
import java.util.List;
import io.prometheus.client.Collector;

class HikariCPCollector extends Collector
{
    private static final List<String> LABEL_NAMES;
    private final Map<String, PoolStats> poolStatsMap;
    
    HikariCPCollector() {
        this.poolStatsMap = new ConcurrentHashMap<String, PoolStats>();
    }
    
    public List<Collector.MetricFamilySamples> collect() {
        return Arrays.asList((Collector.MetricFamilySamples)this.createGauge("hikaricp_active_connections", "Active connections", PoolStats::getActiveConnections), (Collector.MetricFamilySamples)this.createGauge("hikaricp_idle_connections", "Idle connections", PoolStats::getIdleConnections), (Collector.MetricFamilySamples)this.createGauge("hikaricp_pending_threads", "Pending threads", PoolStats::getPendingThreads), (Collector.MetricFamilySamples)this.createGauge("hikaricp_connections", "The number of current connections", PoolStats::getTotalConnections), (Collector.MetricFamilySamples)this.createGauge("hikaricp_max_connections", "Max connections", PoolStats::getMaxConnections), (Collector.MetricFamilySamples)this.createGauge("hikaricp_min_connections", "Min connections", PoolStats::getMinConnections));
    }
    
    void add(final String s, final PoolStats poolStats) {
        this.poolStatsMap.put(s, poolStats);
    }
    
    void remove(final String s) {
        this.poolStatsMap.remove(s);
    }
    
    private GaugeMetricFamily createGauge(final String s, final String s2, final Function<PoolStats, Integer> function) {
        final GaugeMetricFamily gaugeMetricFamily = new GaugeMetricFamily(s, s2, (List)HikariCPCollector.LABEL_NAMES);
        this.poolStatsMap.forEach((o, poolStats) -> gaugeMetricFamily.addMetric((List)Collections.singletonList(o), (double)function.apply(poolStats)));
        return gaugeMetricFamily;
    }
    
    static {
        LABEL_NAMES = Collections.singletonList("pool");
    }
}
