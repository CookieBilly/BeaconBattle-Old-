

package ws.billy.bedwars.libs.com.zaxxer.hikari.pool;

import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

public abstract class ProxyResultSet implements ResultSet
{
    protected final ProxyConnection connection;
    protected final ProxyStatement statement;
    final ResultSet delegate;
    
    protected ProxyResultSet(final ProxyConnection connection, final ProxyStatement statement, final ResultSet delegate) {
        this.connection = connection;
        this.statement = statement;
        this.delegate = delegate;
    }
    
    final SQLException checkException(final SQLException ex) {
        return this.connection.checkException(ex);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + this.delegate;
    }
    
    @Override
    public final Statement getStatement() {
        return this.statement;
    }
    
    @Override
    public void updateRow() {
        this.connection.markCommitStateDirty();
        this.delegate.updateRow();
    }
    
    @Override
    public void insertRow() {
        this.connection.markCommitStateDirty();
        this.delegate.insertRow();
    }
    
    @Override
    public void deleteRow() {
        this.connection.markCommitStateDirty();
        this.delegate.deleteRow();
    }
    
    @Override
    public final <T> T unwrap(final Class<T> obj) {
        if (obj.isInstance(this.delegate)) {
            return (T)this.delegate;
        }
        if (this.delegate != null) {
            return this.delegate.unwrap(obj);
        }
        throw new SQLException("Wrapped ResultSet is not an instance of " + obj);
    }
}
