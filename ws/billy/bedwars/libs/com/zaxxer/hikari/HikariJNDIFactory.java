

package ws.billy.bedwars.libs.com.zaxxer.hikari;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.Enumeration;
import java.util.Set;
import javax.naming.RefAddr;
import java.util.Properties;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.PropertyElf;
import javax.naming.Reference;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

public class HikariJNDIFactory implements ObjectFactory
{
    @Override
    public synchronized Object getObjectInstance(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) {
        if (o instanceof Reference && "javax.sql.DataSource".equals(((Reference)o).getClassName())) {
            final Reference reference = (Reference)o;
            final Set<String> propertyNames = PropertyElf.getPropertyNames(HikariConfig.class);
            final Properties properties = new Properties();
            final Enumeration<RefAddr> all = reference.getAll();
            while (all.hasMoreElements()) {
                final RefAddr refAddr = all.nextElement();
                final String type = refAddr.getType();
                if (type.startsWith("dataSource.") || propertyNames.contains(type)) {
                    properties.setProperty(type, refAddr.getContent().toString());
                }
            }
            return this.createDataSource(properties, context);
        }
        return null;
    }
    
    private DataSource createDataSource(final Properties properties, final Context context) {
        final String property = properties.getProperty("dataSourceJNDI");
        if (property != null) {
            return this.lookupJndiDataSource(properties, context, property);
        }
        return new HikariDataSource(new HikariConfig(properties));
    }
    
    private DataSource lookupJndiDataSource(final Properties properties, final Context context, final String str) {
        if (context == null) {
            throw new RuntimeException("JNDI context does not found for dataSourceJNDI : " + str);
        }
        DataSource dataSource = (DataSource)context.lookup(str);
        if (dataSource == null) {
            final InitialContext initialContext = new InitialContext();
            dataSource = (DataSource)initialContext.lookup(str);
            initialContext.close();
        }
        if (dataSource != null) {
            final HikariConfig hikariConfig = new HikariConfig(properties);
            hikariConfig.setDataSource(dataSource);
            return new HikariDataSource(hikariConfig);
        }
        return null;
    }
}
