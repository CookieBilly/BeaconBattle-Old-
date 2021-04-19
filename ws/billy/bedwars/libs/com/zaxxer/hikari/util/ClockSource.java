

package ws.billy.bedwars.libs.com.zaxxer.hikari.util;

import java.util.concurrent.TimeUnit;

public interface ClockSource
{
    public static final ClockSource CLOCK = create();
    public static final TimeUnit[] TIMEUNITS_DESCENDING = { TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS };
    public static final String[] TIMEUNIT_DISPLAY_VALUES = { "ns", "µs", "ms", "s", "m", "h", "d" };
    
    default long currentTime() {
        return ClockSource.CLOCK.currentTime0();
    }
    
    long currentTime0();
    
    default long toMillis(final long time) {
        return ClockSource.CLOCK.toMillis0(time);
    }
    
    long toMillis0(final long p0);
    
    default long toNanos(final long time) {
        return ClockSource.CLOCK.toNanos0(time);
    }
    
    long toNanos0(final long p0);
    
    default long elapsedMillis(final long startTime) {
        return ClockSource.CLOCK.elapsedMillis0(startTime);
    }
    
    long elapsedMillis0(final long p0);
    
    default long elapsedMillis(final long startTime, final long endTime) {
        return ClockSource.CLOCK.elapsedMillis0(startTime, endTime);
    }
    
    long elapsedMillis0(final long p0, final long p1);
    
    default long elapsedNanos(final long startTime) {
        return ClockSource.CLOCK.elapsedNanos0(startTime);
    }
    
    long elapsedNanos0(final long p0);
    
    default long elapsedNanos(final long startTime, final long endTime) {
        return ClockSource.CLOCK.elapsedNanos0(startTime, endTime);
    }
    
    long elapsedNanos0(final long p0, final long p1);
    
    default long plusMillis(final long time, final long millis) {
        return ClockSource.CLOCK.plusMillis0(time, millis);
    }
    
    long plusMillis0(final long p0, final long p1);
    
    default TimeUnit getSourceTimeUnit() {
        return ClockSource.CLOCK.getSourceTimeUnit0();
    }
    
    TimeUnit getSourceTimeUnit0();
    
    default String elapsedDisplayString(final long startTime, final long endTime) {
        return ClockSource.CLOCK.elapsedDisplayString0(startTime, endTime);
    }
    
    default String elapsedDisplayString0(final long startTime, final long endTime) {
        long elapsedNanos = this.elapsedNanos0(startTime, endTime);
        final StringBuilder sb = new StringBuilder((elapsedNanos < 0L) ? "-" : "");
        elapsedNanos = Math.abs(elapsedNanos);
        for (final TimeUnit unit : ClockSource.TIMEUNITS_DESCENDING) {
            final long converted = unit.convert(elapsedNanos, TimeUnit.NANOSECONDS);
            if (converted > 0L) {
                sb.append(converted).append(ClockSource.TIMEUNIT_DISPLAY_VALUES[unit.ordinal()]);
                elapsedNanos -= TimeUnit.NANOSECONDS.convert(converted, unit);
            }
        }
        return sb.toString();
    }
    
    public static class Factory
    {
        private static ClockSource create() {
            if ("Mac OS X".equals(System.getProperty("os.name"))) {
                return new MillisecondClockSource();
            }
            return new NanosecondClockSource();
        }
    }
    
    public static final class MillisecondClockSource implements ClockSource
    {
        @Override
        public long currentTime0() {
            return System.currentTimeMillis();
        }
        
        @Override
        public long elapsedMillis0(final long n) {
            return System.currentTimeMillis() - n;
        }
        
        @Override
        public long elapsedMillis0(final long n, final long n2) {
            return n2 - n;
        }
        
        @Override
        public long elapsedNanos0(final long n) {
            return TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() - n);
        }
        
        @Override
        public long elapsedNanos0(final long n, final long n2) {
            return TimeUnit.MILLISECONDS.toNanos(n2 - n);
        }
        
        @Override
        public long toMillis0(final long n) {
            return n;
        }
        
        @Override
        public long toNanos0(final long duration) {
            return TimeUnit.MILLISECONDS.toNanos(duration);
        }
        
        @Override
        public long plusMillis0(final long n, final long n2) {
            return n + n2;
        }
        
        @Override
        public TimeUnit getSourceTimeUnit0() {
            return TimeUnit.MILLISECONDS;
        }
    }
    
    public static class NanosecondClockSource implements ClockSource
    {
        @Override
        public long currentTime0() {
            return System.nanoTime();
        }
        
        @Override
        public long toMillis0(final long duration) {
            return TimeUnit.NANOSECONDS.toMillis(duration);
        }
        
        @Override
        public long toNanos0(final long n) {
            return n;
        }
        
        @Override
        public long elapsedMillis0(final long n) {
            return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - n);
        }
        
        @Override
        public long elapsedMillis0(final long n, final long n2) {
            return TimeUnit.NANOSECONDS.toMillis(n2 - n);
        }
        
        @Override
        public long elapsedNanos0(final long n) {
            return System.nanoTime() - n;
        }
        
        @Override
        public long elapsedNanos0(final long n, final long n2) {
            return n2 - n;
        }
        
        @Override
        public long plusMillis0(final long n, final long duration) {
            return n + TimeUnit.MILLISECONDS.toNanos(duration);
        }
        
        @Override
        public TimeUnit getSourceTimeUnit0() {
            return TimeUnit.NANOSECONDS;
        }
    }
}
