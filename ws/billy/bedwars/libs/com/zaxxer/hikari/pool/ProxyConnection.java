

package ws.billy.bedwars.libs.com.zaxxer.hikari.pool;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import ws.billy.bedwars.libs.org.slf4j.LoggerFactory;
import java.util.concurrent.Executor;
import java.sql.Savepoint;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.ClockSource;
import ws.billy.bedwars.libs.com.zaxxer.hikari.SQLExceptionOverride;
import java.sql.SQLTimeoutException;
import java.sql.SQLException;
import java.sql.Statement;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.FastList;
import java.util.Set;
import ws.billy.bedwars.libs.org.slf4j.Logger;

import java.sql.Connection;

public abstract class ProxyConnection implements Connection
{
    static final int DIRTY_BIT_READONLY = 1;
    static final int DIRTY_BIT_AUTOCOMMIT = 2;
    static final int DIRTY_BIT_ISOLATION = 4;
    static final int DIRTY_BIT_CATALOG = 8;
    static final int DIRTY_BIT_NETTIMEOUT = 16;
    static final int DIRTY_BIT_SCHEMA = 32;
    private static final Logger LOGGER;
    private static final Set<String> ERROR_STATES;
    private static final Set<Integer> ERROR_CODES;
    protected Connection delegate;
    private final PoolEntry poolEntry;
    private final ProxyLeakTask leakTask;
    private final FastList<Statement> openStatements;
    private int dirtyBits;
    private long lastAccess;
    private boolean isCommitStateDirty;
    private boolean isReadOnly;
    private boolean isAutoCommit;
    private int networkTimeout;
    private int transactionIsolation;
    private String dbcatalog;
    private String dbschema;
    
    protected ProxyConnection(final PoolEntry poolEntry, final Connection delegate, final FastList<Statement> openStatements, final ProxyLeakTask leakTask, final long lastAccess, final boolean isReadOnly, final boolean isAutoCommit) {
        this.poolEntry = poolEntry;
        this.delegate = delegate;
        this.openStatements = openStatements;
        this.leakTask = leakTask;
        this.lastAccess = lastAccess;
        this.isReadOnly = isReadOnly;
        this.isAutoCommit = isAutoCommit;
    }
    
    @Override
    public final String toString() {
        return this.getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + this.delegate;
    }
    
    final boolean getAutoCommitState() {
        return this.isAutoCommit;
    }
    
    final String getCatalogState() {
        return this.dbcatalog;
    }
    
    final String getSchemaState() {
        return this.dbschema;
    }
    
    final int getTransactionIsolationState() {
        return this.transactionIsolation;
    }
    
    final boolean getReadOnlyState() {
        return this.isReadOnly;
    }
    
    final int getNetworkTimeoutState() {
        return this.networkTimeout;
    }
    
    final PoolEntry getPoolEntry() {
        return this.poolEntry;
    }
    
    final SQLException checkException(final SQLException ex) {
        boolean b = false;
        SQLException nextException = ex;
        final SQLExceptionOverride exceptionOverride = this.poolEntry.getPoolBase().exceptionOverride;
        int n = 0;
        while (this.delegate != ClosedConnection.CLOSED_CONNECTION && nextException != null && n < 10) {
            final String sqlState = nextException.getSQLState();
            if ((sqlState != null && sqlState.startsWith("08")) || nextException instanceof SQLTimeoutException || ProxyConnection.ERROR_STATES.contains(sqlState) || ProxyConnection.ERROR_CODES.contains(nextException.getErrorCode())) {
                if (exceptionOverride != null && exceptionOverride.adjudicate(nextException) == SQLExceptionOverride.Override.DO_NOT_EVICT) {
                    break;
                }
                b = true;
                break;
            }
            else {
                nextException = nextException.getNextException();
                ++n;
            }
        }
        if (b) {
            final SQLException ex2 = (nextException != null) ? nextException : ex;
            ProxyConnection.LOGGER.warn("{} - Connection {} marked as broken because of SQLSTATE({}), ErrorCode({})", this.poolEntry.getPoolName(), this.delegate, ex2.getSQLState(), ex2.getErrorCode(), ex2);
            this.leakTask.cancel();
            this.poolEntry.evict("(connection is broken)");
            this.delegate = ClosedConnection.CLOSED_CONNECTION;
        }
        return ex;
    }
    
    final synchronized void untrackStatement(final Statement statement) {
        this.openStatements.remove(statement);
    }
    
    final void markCommitStateDirty() {
        if (this.isAutoCommit) {
            this.lastAccess = ClockSource.currentTime();
        }
        else {
            this.isCommitStateDirty = true;
        }
    }
    
    void cancelLeakTask() {
        this.leakTask.cancel();
    }
    
    private synchronized <T extends Statement> T trackStatement(final T t) {
        this.openStatements.add(t);
        return t;
    }
    
    private synchronized void closeStatements() {
        final int size = this.openStatements.size();
        if (size > 0) {
            for (int n = 0; n < size && this.delegate != ClosedConnection.CLOSED_CONNECTION; ++n) {
                try {
                    final Statement statement = this.openStatements.get(n);
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (SQLException ex) {
                    ProxyConnection.LOGGER.warn("{} - Connection {} marked as broken because of an exception closing open statements during Connection.close()", this.poolEntry.getPoolName(), this.delegate);
                    this.leakTask.cancel();
                    this.poolEntry.evict("(exception closing Statements during Connection.close())");
                    this.delegate = ClosedConnection.CLOSED_CONNECTION;
                }
            }
            this.openStatements.clear();
        }
    }
    
    @Override
    public final void close() {
        this.closeStatements();
        if (this.delegate != ClosedConnection.CLOSED_CONNECTION) {
            this.leakTask.cancel();
            try {
                if (this.isCommitStateDirty && !this.isAutoCommit) {
                    this.delegate.rollback();
                    this.lastAccess = ClockSource.currentTime();
                    ProxyConnection.LOGGER.debug("{} - Executed rollback on connection {} due to dirty commit state on close().", this.poolEntry.getPoolName(), this.delegate);
                }
                if (this.dirtyBits != 0) {
                    this.poolEntry.resetConnectionState(this, this.dirtyBits);
                    this.lastAccess = ClockSource.currentTime();
                }
                this.delegate.clearWarnings();
            }
            catch (SQLException ex) {
                if (!this.poolEntry.isMarkedEvicted()) {
                    throw this.checkException(ex);
                }
            }
            finally {
                this.delegate = ClosedConnection.CLOSED_CONNECTION;
                this.poolEntry.recycle(this.lastAccess);
            }
        }
    }
    
    @Override
    public boolean isClosed() {
        return this.delegate == ClosedConnection.CLOSED_CONNECTION;
    }
    
    @Override
    public Statement createStatement() {
        return ProxyFactory.getProxyStatement(this, this.trackStatement(this.delegate.createStatement()));
    }
    
    @Override
    public Statement createStatement(final int n, final int n2) {
        return ProxyFactory.getProxyStatement(this, this.trackStatement(this.delegate.createStatement(n, n2)));
    }
    
    @Override
    public Statement createStatement(final int n, final int n2, final int n3) {
        return ProxyFactory.getProxyStatement(this, this.trackStatement(this.delegate.createStatement(n, n2, n3)));
    }
    
    @Override
    public CallableStatement prepareCall(final String s) {
        return ProxyFactory.getProxyCallableStatement(this, this.trackStatement(this.delegate.prepareCall(s)));
    }
    
    @Override
    public CallableStatement prepareCall(final String s, final int n, final int n2) {
        return ProxyFactory.getProxyCallableStatement(this, this.trackStatement(this.delegate.prepareCall(s, n, n2)));
    }
    
    @Override
    public CallableStatement prepareCall(final String s, final int n, final int n2, final int n3) {
        return ProxyFactory.getProxyCallableStatement(this, this.trackStatement(this.delegate.prepareCall(s, n, n2, n3)));
    }
    
    @Override
    public PreparedStatement prepareStatement(final String s) {
        return ProxyFactory.getProxyPreparedStatement(this, this.trackStatement(this.delegate.prepareStatement(s)));
    }
    
    @Override
    public PreparedStatement prepareStatement(final String s, final int n) {
        return ProxyFactory.getProxyPreparedStatement(this, this.trackStatement(this.delegate.prepareStatement(s, n)));
    }
    
    @Override
    public PreparedStatement prepareStatement(final String s, final int n, final int n2) {
        return ProxyFactory.getProxyPreparedStatement(this, this.trackStatement(this.delegate.prepareStatement(s, n, n2)));
    }
    
    @Override
    public PreparedStatement prepareStatement(final String s, final int n, final int n2, final int n3) {
        return ProxyFactory.getProxyPreparedStatement(this, this.trackStatement(this.delegate.prepareStatement(s, n, n2, n3)));
    }
    
    @Override
    public PreparedStatement prepareStatement(final String s, final int[] array) {
        return ProxyFactory.getProxyPreparedStatement(this, this.trackStatement(this.delegate.prepareStatement(s, array)));
    }
    
    @Override
    public PreparedStatement prepareStatement(final String s, final String[] array) {
        return ProxyFactory.getProxyPreparedStatement(this, this.trackStatement(this.delegate.prepareStatement(s, array)));
    }
    
    @Override
    public DatabaseMetaData getMetaData() {
        this.markCommitStateDirty();
        return ProxyFactory.getProxyDatabaseMetaData(this, this.delegate.getMetaData());
    }
    
    @Override
    public void commit() {
        this.delegate.commit();
        this.isCommitStateDirty = false;
        this.lastAccess = ClockSource.currentTime();
    }
    
    @Override
    public void rollback() {
        this.delegate.rollback();
        this.isCommitStateDirty = false;
        this.lastAccess = ClockSource.currentTime();
    }
    
    @Override
    public void rollback(final Savepoint savepoint) {
        this.delegate.rollback(savepoint);
        this.isCommitStateDirty = false;
        this.lastAccess = ClockSource.currentTime();
    }
    
    @Override
    public void setAutoCommit(final boolean b) {
        this.delegate.setAutoCommit(b);
        this.isAutoCommit = b;
        this.dirtyBits |= 0x2;
    }
    
    @Override
    public void setReadOnly(final boolean b) {
        this.delegate.setReadOnly(b);
        this.isReadOnly = b;
        this.isCommitStateDirty = false;
        this.dirtyBits |= 0x1;
    }
    
    @Override
    public void setTransactionIsolation(final int n) {
        this.delegate.setTransactionIsolation(n);
        this.transactionIsolation = n;
        this.dirtyBits |= 0x4;
    }
    
    @Override
    public void setCatalog(final String s) {
        this.delegate.setCatalog(s);
        this.dbcatalog = s;
        this.dirtyBits |= 0x8;
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int networkTimeout) {
        this.delegate.setNetworkTimeout(executor, networkTimeout);
        this.networkTimeout = networkTimeout;
        this.dirtyBits |= 0x10;
    }
    
    @Override
    public void setSchema(final String s) {
        this.delegate.setSchema(s);
        this.dbschema = s;
        this.dirtyBits |= 0x20;
    }
    
    @Override
    public final boolean isWrapperFor(final Class<?> clazz) {
        return clazz.isInstance(this.delegate) || (this.delegate != null && this.delegate.isWrapperFor(clazz));
    }
    
    @Override
    public final <T> T unwrap(final Class<T> obj) {
        if (obj.isInstance(this.delegate)) {
            return (T)this.delegate;
        }
        if (this.delegate != null) {
            return this.delegate.unwrap(obj);
        }
        throw new SQLException("Wrapped connection is not an instance of " + obj);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(ProxyConnection.class);
        (ERROR_STATES = new HashSet<String>()).add("0A000");
        ProxyConnection.ERROR_STATES.add("57P01");
        ProxyConnection.ERROR_STATES.add("57P02");
        ProxyConnection.ERROR_STATES.add("57P03");
        ProxyConnection.ERROR_STATES.add("01002");
        ProxyConnection.ERROR_STATES.add("JZ0C0");
        ProxyConnection.ERROR_STATES.add("JZ0C1");
        (ERROR_CODES = new HashSet<Integer>()).add(500150);
        ProxyConnection.ERROR_CODES.add(2399);
    }
    
    private static final class ClosedConnection
    {
        static final Connection CLOSED_CONNECTION;
        
        private static Connection getClosedConnection() {
            final String anObject;
            return (Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[] { Connection.class }, (p0, method, p2) -> {
                method.getName();
                if ("isClosed".equals(anObject)) {
                    return Boolean.TRUE;
                }
                else if ("isValid".equals(anObject)) {
                    return Boolean.FALSE;
                }
                else if ("abort".equals(anObject)) {
                    return Void.TYPE;
                }
                else if ("close".equals(anObject)) {
                    return Void.TYPE;
                }
                else if ("toString".equals(anObject)) {
                    return ClosedConnection.class.getCanonicalName();
                }
                else {
                    throw new SQLException("Connection is closed");
                }
            });
        }
        
        static {
            CLOSED_CONNECTION = getClosedConnection();
        }
    }
}
