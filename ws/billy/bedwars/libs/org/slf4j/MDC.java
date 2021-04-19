

package ws.billy.bedwars.libs.org.slf4j;

import java.io.Closeable;
import ws.billy.bedwars.libs.org.slf4j.helpers.Util;
import ws.billy.bedwars.libs.org.slf4j.helpers.NOPMDCAdapter;
import java.util.Map;
import ws.billy.bedwars.libs.org.slf4j.impl.StaticMDCBinder;
import ws.billy.bedwars.libs.org.slf4j.spi.MDCAdapter;

public class MDC
{
    static final String NULL_MDCA_URL = "http://www.slf4j.org/codes.html#null_MDCA";
    static final String NO_STATIC_MDC_BINDER_URL = "http://www.slf4j.org/codes.html#no_static_mdc_binder";
    static MDCAdapter mdcAdapter;
    
    private MDC() {
    }
    
    private static MDCAdapter bwCompatibleGetMDCAdapterFromBinder() {
        try {
            return StaticMDCBinder.getSingleton().getMDCA();
        }
        catch (NoSuchMethodError noSuchMethodError) {
            return StaticMDCBinder.SINGLETON.getMDCA();
        }
    }
    
    public static void put(final String s, final String s2) {
        if (s == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (MDC.mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        MDC.mdcAdapter.put(s, s2);
    }
    
    public static MDCCloseable putCloseable(final String s, final String s2) {
        put(s, s2);
        return new MDCCloseable(s);
    }
    
    public static String get(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (MDC.mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        return MDC.mdcAdapter.get(s);
    }
    
    public static void remove(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (MDC.mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        MDC.mdcAdapter.remove(s);
    }
    
    public static void clear() {
        if (MDC.mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        MDC.mdcAdapter.clear();
    }
    
    public static Map<String, String> getCopyOfContextMap() {
        if (MDC.mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        return MDC.mdcAdapter.getCopyOfContextMap();
    }
    
    public static void setContextMap(final Map<String, String> contextMap) {
        if (MDC.mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        MDC.mdcAdapter.setContextMap(contextMap);
    }
    
    public static MDCAdapter getMDCAdapter() {
        return MDC.mdcAdapter;
    }
    
    static {
        try {
            MDC.mdcAdapter = bwCompatibleGetMDCAdapterFromBinder();
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            MDC.mdcAdapter = new NOPMDCAdapter();
            final String message = noClassDefFoundError.getMessage();
            if (message == null || !message.contains("StaticMDCBinder")) {
                throw noClassDefFoundError;
            }
            Util.report("Failed to load class \"org.slf4j.impl.StaticMDCBinder\".");
            Util.report("Defaulting to no-operation MDCAdapter implementation.");
            Util.report("See http://www.slf4j.org/codes.html#no_static_mdc_binder for further details.");
        }
        catch (Exception ex) {
            Util.report("MDC binding unsuccessful.", ex);
        }
    }
    
    public static class MDCCloseable implements Closeable
    {
        private final String key;
        
        private MDCCloseable(final String key) {
            this.key = key;
        }
        
        public void close() {
            MDC.remove(this.key);
        }
    }
}
