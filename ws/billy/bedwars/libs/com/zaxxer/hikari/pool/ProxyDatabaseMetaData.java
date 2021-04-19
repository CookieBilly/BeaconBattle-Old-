

package ws.billy.bedwars.libs.com.zaxxer.hikari.pool;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

public abstract class ProxyDatabaseMetaData implements DatabaseMetaData
{
    protected final ProxyConnection connection;
    protected final DatabaseMetaData delegate;
    
    ProxyDatabaseMetaData(final ProxyConnection connection, final DatabaseMetaData delegate) {
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
    public final Connection getConnection() {
        return this.connection;
    }
    
    @Override
    public ResultSet getProcedures(final String s, final String s2, final String s3) {
        final ResultSet procedures = this.delegate.getProcedures(s, s2, s3);
        Statement statement = procedures.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, procedures);
    }
    
    @Override
    public ResultSet getProcedureColumns(final String s, final String s2, final String s3, final String s4) {
        final ResultSet procedureColumns = this.delegate.getProcedureColumns(s, s2, s3, s4);
        Statement statement = procedureColumns.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, procedureColumns);
    }
    
    @Override
    public ResultSet getTables(final String s, final String s2, final String s3, final String[] array) {
        final ResultSet tables = this.delegate.getTables(s, s2, s3, array);
        Statement statement = tables.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, tables);
    }
    
    @Override
    public ResultSet getSchemas() {
        final ResultSet schemas = this.delegate.getSchemas();
        Statement statement = schemas.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, schemas);
    }
    
    @Override
    public ResultSet getCatalogs() {
        final ResultSet catalogs = this.delegate.getCatalogs();
        Statement statement = catalogs.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, catalogs);
    }
    
    @Override
    public ResultSet getTableTypes() {
        final ResultSet tableTypes = this.delegate.getTableTypes();
        Statement statement = tableTypes.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, tableTypes);
    }
    
    @Override
    public ResultSet getColumns(final String s, final String s2, final String s3, final String s4) {
        final ResultSet columns = this.delegate.getColumns(s, s2, s3, s4);
        Statement statement = columns.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, columns);
    }
    
    @Override
    public ResultSet getColumnPrivileges(final String s, final String s2, final String s3, final String s4) {
        final ResultSet columnPrivileges = this.delegate.getColumnPrivileges(s, s2, s3, s4);
        Statement statement = columnPrivileges.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, columnPrivileges);
    }
    
    @Override
    public ResultSet getTablePrivileges(final String s, final String s2, final String s3) {
        final ResultSet tablePrivileges = this.delegate.getTablePrivileges(s, s2, s3);
        Statement statement = tablePrivileges.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, tablePrivileges);
    }
    
    @Override
    public ResultSet getBestRowIdentifier(final String s, final String s2, final String s3, final int n, final boolean b) {
        final ResultSet bestRowIdentifier = this.delegate.getBestRowIdentifier(s, s2, s3, n, b);
        Statement statement = bestRowIdentifier.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, bestRowIdentifier);
    }
    
    @Override
    public ResultSet getVersionColumns(final String s, final String s2, final String s3) {
        final ResultSet versionColumns = this.delegate.getVersionColumns(s, s2, s3);
        Statement statement = versionColumns.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, versionColumns);
    }
    
    @Override
    public ResultSet getPrimaryKeys(final String s, final String s2, final String s3) {
        final ResultSet primaryKeys = this.delegate.getPrimaryKeys(s, s2, s3);
        Statement statement = primaryKeys.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, primaryKeys);
    }
    
    @Override
    public ResultSet getImportedKeys(final String s, final String s2, final String s3) {
        final ResultSet importedKeys = this.delegate.getImportedKeys(s, s2, s3);
        Statement statement = importedKeys.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, importedKeys);
    }
    
    @Override
    public ResultSet getExportedKeys(final String s, final String s2, final String s3) {
        final ResultSet exportedKeys = this.delegate.getExportedKeys(s, s2, s3);
        Statement statement = exportedKeys.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, exportedKeys);
    }
    
    @Override
    public ResultSet getCrossReference(final String s, final String s2, final String s3, final String s4, final String s5, final String s6) {
        final ResultSet crossReference = this.delegate.getCrossReference(s, s2, s3, s4, s5, s6);
        Statement statement = crossReference.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, crossReference);
    }
    
    @Override
    public ResultSet getTypeInfo() {
        final ResultSet typeInfo = this.delegate.getTypeInfo();
        Statement statement = typeInfo.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, typeInfo);
    }
    
    @Override
    public ResultSet getIndexInfo(final String s, final String s2, final String s3, final boolean b, final boolean b2) {
        final ResultSet indexInfo = this.delegate.getIndexInfo(s, s2, s3, b, b2);
        Statement statement = indexInfo.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, indexInfo);
    }
    
    @Override
    public ResultSet getUDTs(final String s, final String s2, final String s3, final int[] array) {
        final ResultSet udTs = this.delegate.getUDTs(s, s2, s3, array);
        Statement statement = udTs.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, udTs);
    }
    
    @Override
    public ResultSet getSuperTypes(final String s, final String s2, final String s3) {
        final ResultSet superTypes = this.delegate.getSuperTypes(s, s2, s3);
        Statement statement = superTypes.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, superTypes);
    }
    
    @Override
    public ResultSet getSuperTables(final String s, final String s2, final String s3) {
        final ResultSet superTables = this.delegate.getSuperTables(s, s2, s3);
        Statement statement = superTables.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, superTables);
    }
    
    @Override
    public ResultSet getAttributes(final String s, final String s2, final String s3, final String s4) {
        final ResultSet attributes = this.delegate.getAttributes(s, s2, s3, s4);
        Statement statement = attributes.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, attributes);
    }
    
    @Override
    public ResultSet getSchemas(final String s, final String s2) {
        final ResultSet schemas = this.delegate.getSchemas(s, s2);
        Statement statement = schemas.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, schemas);
    }
    
    @Override
    public ResultSet getClientInfoProperties() {
        final ResultSet clientInfoProperties = this.delegate.getClientInfoProperties();
        Statement statement = clientInfoProperties.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, clientInfoProperties);
    }
    
    @Override
    public ResultSet getFunctions(final String s, final String s2, final String s3) {
        final ResultSet functions = this.delegate.getFunctions(s, s2, s3);
        Statement statement = functions.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, functions);
    }
    
    @Override
    public ResultSet getFunctionColumns(final String s, final String s2, final String s3, final String s4) {
        final ResultSet functionColumns = this.delegate.getFunctionColumns(s, s2, s3, s4);
        Statement statement = functionColumns.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, functionColumns);
    }
    
    @Override
    public ResultSet getPseudoColumns(final String s, final String s2, final String s3, final String s4) {
        final ResultSet pseudoColumns = this.delegate.getPseudoColumns(s, s2, s3, s4);
        Statement statement = pseudoColumns.getStatement();
        if (statement != null) {
            statement = ProxyFactory.getProxyStatement(this.connection, statement);
        }
        return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, pseudoColumns);
    }
    
    @Override
    public final <T> T unwrap(final Class<T> obj) {
        if (obj.isInstance(this.delegate)) {
            return (T)this.delegate;
        }
        if (this.delegate != null) {
            return this.delegate.unwrap(obj);
        }
        throw new SQLException("Wrapped DatabaseMetaData is not an instance of " + obj);
    }
}
