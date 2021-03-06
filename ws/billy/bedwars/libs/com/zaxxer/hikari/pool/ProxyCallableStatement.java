

package ws.billy.bedwars.libs.com.zaxxer.hikari.pool;

import java.sql.PreparedStatement;
import java.sql.CallableStatement;

public abstract class ProxyCallableStatement extends ProxyPreparedStatement implements CallableStatement
{
    protected ProxyCallableStatement(final ProxyConnection proxyConnection, final CallableStatement callableStatement) {
        super(proxyConnection, callableStatement);
    }
}
