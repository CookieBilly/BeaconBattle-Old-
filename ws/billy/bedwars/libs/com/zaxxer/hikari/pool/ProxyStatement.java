

package ws.billy.bedwars.libs.com.zaxxer.hikari.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class ProxyStatement implements Statement
{
    protected final ProxyConnection connection;
    final Statement delegate;
    private boolean isClosed;
    private ResultSet proxyResultSet;
    
    ProxyStatement(final ProxyConnection connection, final Statement delegate) {
        this.connection = connection;
        this.delegate = delegate;
    }
    
    final SQLException checkException(final SQLException ex) {
        return this.connection.checkException(ex);
    }
    
    @Override
    public final String toString() {
        return this.getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + this.delegate.toString();
    }
    
    @Override
    public final void close() {
        synchronized (this) {
            if (this.isClosed) {
                return;
            }
            this.isClosed = true;
        }
        this.connection.untrackStatement(this.delegate);
        try {
            this.delegate.close();
        }
        catch (SQLException ex) {
            throw this.connection.checkException(ex);
        }
    }
    
    @Override
    public Connection getConnection() {
        return this.connection;
    }
    
    @Override
    public boolean execute(final String s) {
        this.connection.markCommitStateDirty();
        return this.delegate.execute(s);
    }
    
    @Override
    public boolean execute(final String s, final int n) {
        this.connection.markCommitStateDirty();
        return this.delegate.execute(s, n);
    }
    
    @Override
    public ResultSet executeQuery(final String s) {
        this.connection.markCommitStateDirty();
        return ProxyFactory.getProxyResultSet(this.connection, this, this.delegate.executeQuery(s));
    }
    
    @Override
    public int executeUpdate(final String s) {
        this.connection.markCommitStateDirty();
        return this.delegate.executeUpdate(s);
    }
    
    @Override
    public int[] executeBatch() {
        this.connection.markCommitStateDirty();
        return this.delegate.executeBatch();
    }
    
    @Override
    public int executeUpdate(final String s, final int n) {
        this.connection.markCommitStateDirty();
        return this.delegate.executeUpdate(s, n);
    }
    
    @Override
    public int executeUpdate(final String s, final int[] array) {
        this.connection.markCommitStateDirty();
        return this.delegate.executeUpdate(s, array);
    }
    
    @Override
    public int executeUpdate(final String s, final String[] array) {
        this.connection.markCommitStateDirty();
        return this.delegate.executeUpdate(s, array);
    }
    
    @Override
    public boolean execute(final String s, final int[] array) {
        this.connection.markCommitStateDirty();
        return this.delegate.execute(s, array);
    }
    
    @Override
    public boolean execute(final String s, final String[] array) {
        this.connection.markCommitStateDirty();
        return this.delegate.execute(s, array);
    }
    
    @Override
    public long[] executeLargeBatch() {
        this.connection.markCommitStateDirty();
        return this.delegate.executeLargeBatch();
    }
    
    @Override
    public long executeLargeUpdate(final String sql) {
        this.connection.markCommitStateDirty();
        return this.delegate.executeLargeUpdate(sql);
    }
    
    @Override
    public long executeLargeUpdate(final String sql, final int autoGeneratedKeys) {
        this.connection.markCommitStateDirty();
        return this.delegate.executeLargeUpdate(sql, autoGeneratedKeys);
    }
    
    @Override
    public long executeLargeUpdate(final String sql, final int[] columnIndexes) {
        this.connection.markCommitStateDirty();
        return this.delegate.executeLargeUpdate(sql, columnIndexes);
    }
    
    @Override
    public long executeLargeUpdate(final String sql, final String[] columnNames) {
        this.connection.markCommitStateDirty();
        return this.delegate.executeLargeUpdate(sql, columnNames);
    }
    
    @Override
    public ResultSet getResultSet() {
        final ResultSet resultSet = this.delegate.getResultSet();
        if (resultSet != null) {
            if (this.proxyResultSet == null || ((ProxyResultSet)this.proxyResultSet).delegate != resultSet) {
                this.proxyResultSet = ProxyFactory.getProxyResultSet(this.connection, this, resultSet);
            }
        }
        else {
            this.proxyResultSet = null;
        }
        return this.proxyResultSet;
    }
    
    @Override
    public ResultSet getGeneratedKeys() {
        final ResultSet generatedKeys = this.delegate.getGeneratedKeys();
        if (this.proxyResultSet == null || ((ProxyResultSet)this.proxyResultSet).delegate != generatedKeys) {
            this.proxyResultSet = ProxyFactory.getProxyResultSet(this.connection, this, generatedKeys);
        }
        return this.proxyResultSet;
    }
    
    @Override
    public final <T> T unwrap(final Class<T> obj) {
        if (obj.isInstance(this.delegate)) {
            return (T)this.delegate;
        }
        if (this.delegate != null) {
            return this.delegate.unwrap(obj);
        }
        throw new SQLException("Wrapped statement is not an instance of " + obj);
    }
}
