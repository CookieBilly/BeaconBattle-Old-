

package ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.prometheus;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.Counter;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.IMetricsTracker;

class PrometheusHistogramMetricsTracker implements IMetricsTracker
{
    private static final Counter CONNECTION_TIMEOUT_COUNTER;
    private static final Histogram ELAPSED_ACQUIRED_HISTOGRAM;
    private static final Histogram ELAPSED_BORROWED_HISTOGRAM;
    private static final Histogram ELAPSED_CREATION_HISTOGRAM;
    private final Counter.Child connectionTimeoutCounterChild;
    private final Histogram.Child elapsedAcquiredHistogramChild;
    private final Histogram.Child elapsedBorrowedHistogramChild;
    private final Histogram.Child elapsedCreationHistogramChild;
    
    private static Histogram registerHistogram(final String s, final String s2, final double n) {
        return ((Histogram.Builder)((Histogram.Builder)((Histogram.Builder)Histogram.build().name(s)).labelNames(new String[] { "pool" })).help(s2)).exponentialBuckets(n, 2.0, 11).create();
    }
    
    PrometheusHistogramMetricsTracker(final String s, final CollectorRegistry collectorRegistry) {
        this.registerMetrics(collectorRegistry);
        this.connectionTimeoutCounterChild = (Counter.Child)PrometheusHistogramMetricsTracker.CONNECTION_TIMEOUT_COUNTER.labels(new String[] { s });
        this.elapsedAcquiredHistogramChild = (Histogram.Child)PrometheusHistogramMetricsTracker.ELAPSED_ACQUIRED_HISTOGRAM.labels(new String[] { s });
        this.elapsedBorrowedHistogramChild = (Histogram.Child)PrometheusHistogramMetricsTracker.ELAPSED_BORROWED_HISTOGRAM.labels(new String[] { s });
        this.elapsedCreationHistogramChild = (Histogram.Child)PrometheusHistogramMetricsTracker.ELAPSED_CREATION_HISTOGRAM.labels(new String[] { s });
    }
    
    private void registerMetrics(final CollectorRegistry collectorRegistry) {
        PrometheusHistogramMetricsTracker.CONNECTION_TIMEOUT_COUNTER.register(collectorRegistry);
        PrometheusHistogramMetricsTracker.ELAPSED_ACQUIRED_HISTOGRAM.register(collectorRegistry);
        PrometheusHistogramMetricsTracker.ELAPSED_BORROWED_HISTOGRAM.register(collectorRegistry);
        PrometheusHistogramMetricsTracker.ELAPSED_CREATION_HISTOGRAM.register(collectorRegistry);
    }
    
    @Override
    public void recordConnectionAcquiredNanos(final long n) {
        this.elapsedAcquiredHistogramChild.observe((double)n);
    }
    
    @Override
    public void recordConnectionUsageMillis(final long n) {
        this.elapsedBorrowedHistogramChild.observe((double)n);
    }
    
    @Override
    public void recordConnectionCreatedMillis(final long n) {
        this.elapsedCreationHistogramChild.observe((double)n);
    }
    
    @Override
    public void recordConnectionTimeout() {
        this.connectionTimeoutCounterChild.inc();
    }
    
    static {
        CONNECTION_TIMEOUT_COUNTER = ((Counter.Builder)((Counter.Builder)((Counter.Builder)Counter.build().name("hikaricp_connection_timeout_total")).labelNames(new String[] { "pool" })).help("Connection timeout total count")).create();
        ELAPSED_ACQUIRED_HISTOGRAM = registerHistogram("hikaricp_connection_acquired_nanos", "Connection acquired time (ns)", 1000.0);
        ELAPSED_BORROWED_HISTOGRAM = registerHistogram("hikaricp_connection_usage_millis", "Connection usage (ms)", 1.0);
        ELAPSED_CREATION_HISTOGRAM = registerHistogram("hikaricp_connection_creation_millis", "Connection creation (ms)", 1.0);
    }
}
