

package ws.billy.bedwars.libs.com.zaxxer.hikari.util;

import ws.billy.bedwars.libs.org.slf4j.Logger;
import ws.billy.bedwars.libs.org.slf4j.LoggerFactory;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Method;
import java.util.List;
import ws.billy.bedwars.libs.com.zaxxer.hikari.HikariConfig;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

public final class PropertyElf
{
    private static final Pattern GETTER_PATTERN;
    
    private PropertyElf() {
    }
    
    public static void setTargetFromProperties(final Object o, final Properties properties) {
        if (o == null || properties == null) {
            return;
        }
        final List<Method> list;
        properties.forEach((o2, o3) -> {
            Arrays.asList(o.getClass().getMethods());
            if (o instanceof HikariConfig && o2.toString().startsWith("dataSource.")) {
                ((HikariConfig)o).addDataSourceProperty(o2.toString().substring("dataSource.".length()), o3);
            }
            else {
                setProperty(o, o2.toString(), o3, list);
            }
        });
    }
    
    public static Set<String> getPropertyNames(final Class<?> clazz) {
        final HashSet<String> set = new HashSet<String>();
        final Matcher matcher = PropertyElf.GETTER_PATTERN.matcher("");
        for (final Method method : clazz.getMethods()) {
            final String name = method.getName();
            if (method.getParameterTypes().length == 0 && matcher.reset(name).matches()) {
                final String replaceFirst = name.replaceFirst("(get|is)", "");
                try {
                    if (clazz.getMethod("set" + replaceFirst, method.getReturnType()) != null) {
                        set.add(Character.toLowerCase(replaceFirst.charAt(0)) + replaceFirst.substring(1));
                    }
                }
                catch (Exception ex) {}
            }
        }
        return set;
    }
    
    public static Object getProperty(final String s, final Object o) {
        try {
            return o.getClass().getMethod("get" + s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1), (Class<?>[])new Class[0]).invoke(o, new Object[0]);
        }
        catch (Exception ex) {
            try {
                return o.getClass().getMethod("is" + s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1), (Class<?>[])new Class[0]).invoke(o, new Object[0]);
            }
            catch (Exception ex2) {
                return null;
            }
        }
    }
    
    public static Properties copyProperties(final Properties properties) {
        final Properties properties2 = new Properties();
        properties.forEach((o, o2) -> properties2.setProperty(o.toString(), o2.toString()));
        return properties2;
    }
    
    private static void setProperty(final Object o, final String s, final Object o2, final List<Method> list) {
        final Logger logger = LoggerFactory.getLogger(PropertyElf.class);
        Method method3 = list.stream().filter(method -> method.getName().equals("set" + s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1)) && method.getParameterCount() == 1).findFirst().orElse(null);
        if (method3 == null) {
            method3 = list.stream().filter(method2 -> method2.getName().equals("set" + s.toUpperCase(Locale.ENGLISH)) && method2.getParameterCount() == 1).findFirst().orElse(null);
        }
        if (method3 == null) {
            logger.error("Property {} does not exist on target {}", s, o.getClass());
            throw new RuntimeException(String.format("Property %s does not exist on target %s", s, o.getClass()));
        }
        try {
            final Class<?> clazz = method3.getParameterTypes()[0];
            if (clazz == Integer.TYPE) {
                method3.invoke(o, Integer.parseInt(o2.toString()));
            }
            else if (clazz == Long.TYPE) {
                method3.invoke(o, Long.parseLong(o2.toString()));
            }
            else if (clazz == Boolean.TYPE || clazz == Boolean.class) {
                method3.invoke(o, Boolean.parseBoolean(o2.toString()));
            }
            else if (clazz == String.class) {
                method3.invoke(o, o2.toString());
            }
            else {
                try {
                    logger.debug("Try to create a new instance of \"{}\"", o2.toString());
                    method3.invoke(o, Class.forName(o2.toString()).newInstance());
                }
                catch (InstantiationException | ClassNotFoundException ex) {
                    logger.debug("Class \"{}\" not found or could not instantiate it (Default constructor)", o2.toString());
                    method3.invoke(o, o2);
                }
            }
        }
        catch (Exception cause) {
            logger.error("Failed to set property {} on target {}", s, o.getClass(), cause);
            throw new RuntimeException(cause);
        }
    }
    
    static {
        GETTER_PATTERN = Pattern.compile("(get|is)[A-Z].+");
    }
}
