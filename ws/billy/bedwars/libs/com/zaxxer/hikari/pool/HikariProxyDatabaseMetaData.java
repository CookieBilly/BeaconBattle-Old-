

package ws.billy.bedwars.libs.com.zaxxer.hikari.pool;

import java.sql.RowIdLifetime;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.Wrapper;

public class HikariProxyDatabaseMetaData extends ProxyDatabaseMetaData implements Wrapper, DatabaseMetaData
{
    @Override
    public boolean isWrapperFor(final Class clazz) {
        try {
            return super.delegate.isWrapperFor(clazz);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean allProceduresAreCallable() {
        try {
            return super.delegate.allProceduresAreCallable();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean allTablesAreSelectable() {
        try {
            return super.delegate.allTablesAreSelectable();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getURL() {
        try {
            return super.delegate.getURL();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getUserName() {
        try {
            return super.delegate.getUserName();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean isReadOnly() {
        try {
            return super.delegate.isReadOnly();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean nullsAreSortedHigh() {
        try {
            return super.delegate.nullsAreSortedHigh();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean nullsAreSortedLow() {
        try {
            return super.delegate.nullsAreSortedLow();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean nullsAreSortedAtStart() {
        try {
            return super.delegate.nullsAreSortedAtStart();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean nullsAreSortedAtEnd() {
        try {
            return super.delegate.nullsAreSortedAtEnd();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getDatabaseProductName() {
        try {
            return super.delegate.getDatabaseProductName();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getDatabaseProductVersion() {
        try {
            return super.delegate.getDatabaseProductVersion();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getDriverName() {
        try {
            return super.delegate.getDriverName();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getDriverVersion() {
        try {
            return super.delegate.getDriverVersion();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getDriverMajorVersion() {
        return super.delegate.getDriverMajorVersion();
    }
    
    @Override
    public int getDriverMinorVersion() {
        return super.delegate.getDriverMinorVersion();
    }
    
    @Override
    public boolean usesLocalFiles() {
        try {
            return super.delegate.usesLocalFiles();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean usesLocalFilePerTable() {
        try {
            return super.delegate.usesLocalFilePerTable();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsMixedCaseIdentifiers() {
        try {
            return super.delegate.supportsMixedCaseIdentifiers();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean storesUpperCaseIdentifiers() {
        try {
            return super.delegate.storesUpperCaseIdentifiers();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean storesLowerCaseIdentifiers() {
        try {
            return super.delegate.storesLowerCaseIdentifiers();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean storesMixedCaseIdentifiers() {
        try {
            return super.delegate.storesMixedCaseIdentifiers();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() {
        try {
            return super.delegate.supportsMixedCaseQuotedIdentifiers();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean storesUpperCaseQuotedIdentifiers() {
        try {
            return super.delegate.storesUpperCaseQuotedIdentifiers();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean storesLowerCaseQuotedIdentifiers() {
        try {
            return super.delegate.storesLowerCaseQuotedIdentifiers();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean storesMixedCaseQuotedIdentifiers() {
        try {
            return super.delegate.storesMixedCaseQuotedIdentifiers();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getIdentifierQuoteString() {
        try {
            return super.delegate.getIdentifierQuoteString();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getSQLKeywords() {
        try {
            return super.delegate.getSQLKeywords();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getNumericFunctions() {
        try {
            return super.delegate.getNumericFunctions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getStringFunctions() {
        try {
            return super.delegate.getStringFunctions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getSystemFunctions() {
        try {
            return super.delegate.getSystemFunctions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getTimeDateFunctions() {
        try {
            return super.delegate.getTimeDateFunctions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getSearchStringEscape() {
        try {
            return super.delegate.getSearchStringEscape();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getExtraNameCharacters() {
        try {
            return super.delegate.getExtraNameCharacters();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsAlterTableWithAddColumn() {
        try {
            return super.delegate.supportsAlterTableWithAddColumn();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsAlterTableWithDropColumn() {
        try {
            return super.delegate.supportsAlterTableWithDropColumn();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsColumnAliasing() {
        try {
            return super.delegate.supportsColumnAliasing();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean nullPlusNonNullIsNull() {
        try {
            return super.delegate.nullPlusNonNullIsNull();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsConvert() {
        try {
            return super.delegate.supportsConvert();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsConvert(final int n, final int n2) {
        try {
            return super.delegate.supportsConvert(n, n2);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsTableCorrelationNames() {
        try {
            return super.delegate.supportsTableCorrelationNames();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsDifferentTableCorrelationNames() {
        try {
            return super.delegate.supportsDifferentTableCorrelationNames();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsExpressionsInOrderBy() {
        try {
            return super.delegate.supportsExpressionsInOrderBy();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsOrderByUnrelated() {
        try {
            return super.delegate.supportsOrderByUnrelated();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsGroupBy() {
        try {
            return super.delegate.supportsGroupBy();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsGroupByUnrelated() {
        try {
            return super.delegate.supportsGroupByUnrelated();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsGroupByBeyondSelect() {
        try {
            return super.delegate.supportsGroupByBeyondSelect();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsLikeEscapeClause() {
        try {
            return super.delegate.supportsLikeEscapeClause();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsMultipleResultSets() {
        try {
            return super.delegate.supportsMultipleResultSets();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsMultipleTransactions() {
        try {
            return super.delegate.supportsMultipleTransactions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsNonNullableColumns() {
        try {
            return super.delegate.supportsNonNullableColumns();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsMinimumSQLGrammar() {
        try {
            return super.delegate.supportsMinimumSQLGrammar();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsCoreSQLGrammar() {
        try {
            return super.delegate.supportsCoreSQLGrammar();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsExtendedSQLGrammar() {
        try {
            return super.delegate.supportsExtendedSQLGrammar();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsANSI92EntryLevelSQL() {
        try {
            return super.delegate.supportsANSI92EntryLevelSQL();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsANSI92IntermediateSQL() {
        try {
            return super.delegate.supportsANSI92IntermediateSQL();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsANSI92FullSQL() {
        try {
            return super.delegate.supportsANSI92FullSQL();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsIntegrityEnhancementFacility() {
        try {
            return super.delegate.supportsIntegrityEnhancementFacility();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsOuterJoins() {
        try {
            return super.delegate.supportsOuterJoins();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsFullOuterJoins() {
        try {
            return super.delegate.supportsFullOuterJoins();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsLimitedOuterJoins() {
        try {
            return super.delegate.supportsLimitedOuterJoins();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getSchemaTerm() {
        try {
            return super.delegate.getSchemaTerm();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getProcedureTerm() {
        try {
            return super.delegate.getProcedureTerm();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getCatalogTerm() {
        try {
            return super.delegate.getCatalogTerm();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean isCatalogAtStart() {
        try {
            return super.delegate.isCatalogAtStart();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public String getCatalogSeparator() {
        try {
            return super.delegate.getCatalogSeparator();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsSchemasInDataManipulation() {
        try {
            return super.delegate.supportsSchemasInDataManipulation();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsSchemasInProcedureCalls() {
        try {
            return super.delegate.supportsSchemasInProcedureCalls();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsSchemasInTableDefinitions() {
        try {
            return super.delegate.supportsSchemasInTableDefinitions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsSchemasInIndexDefinitions() {
        try {
            return super.delegate.supportsSchemasInIndexDefinitions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() {
        try {
            return super.delegate.supportsSchemasInPrivilegeDefinitions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsCatalogsInDataManipulation() {
        try {
            return super.delegate.supportsCatalogsInDataManipulation();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsCatalogsInProcedureCalls() {
        try {
            return super.delegate.supportsCatalogsInProcedureCalls();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsCatalogsInTableDefinitions() {
        try {
            return super.delegate.supportsCatalogsInTableDefinitions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsCatalogsInIndexDefinitions() {
        try {
            return super.delegate.supportsCatalogsInIndexDefinitions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() {
        try {
            return super.delegate.supportsCatalogsInPrivilegeDefinitions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsPositionedDelete() {
        try {
            return super.delegate.supportsPositionedDelete();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsPositionedUpdate() {
        try {
            return super.delegate.supportsPositionedUpdate();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsSelectForUpdate() {
        try {
            return super.delegate.supportsSelectForUpdate();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsStoredProcedures() {
        try {
            return super.delegate.supportsStoredProcedures();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsSubqueriesInComparisons() {
        try {
            return super.delegate.supportsSubqueriesInComparisons();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsSubqueriesInExists() {
        try {
            return super.delegate.supportsSubqueriesInExists();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsSubqueriesInIns() {
        try {
            return super.delegate.supportsSubqueriesInIns();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsSubqueriesInQuantifieds() {
        try {
            return super.delegate.supportsSubqueriesInQuantifieds();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsCorrelatedSubqueries() {
        try {
            return super.delegate.supportsCorrelatedSubqueries();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsUnion() {
        try {
            return super.delegate.supportsUnion();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsUnionAll() {
        try {
            return super.delegate.supportsUnionAll();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsOpenCursorsAcrossCommit() {
        try {
            return super.delegate.supportsOpenCursorsAcrossCommit();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsOpenCursorsAcrossRollback() {
        try {
            return super.delegate.supportsOpenCursorsAcrossRollback();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsOpenStatementsAcrossCommit() {
        try {
            return super.delegate.supportsOpenStatementsAcrossCommit();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsOpenStatementsAcrossRollback() {
        try {
            return super.delegate.supportsOpenStatementsAcrossRollback();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxBinaryLiteralLength() {
        try {
            return super.delegate.getMaxBinaryLiteralLength();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxCharLiteralLength() {
        try {
            return super.delegate.getMaxCharLiteralLength();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxColumnNameLength() {
        try {
            return super.delegate.getMaxColumnNameLength();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxColumnsInGroupBy() {
        try {
            return super.delegate.getMaxColumnsInGroupBy();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxColumnsInIndex() {
        try {
            return super.delegate.getMaxColumnsInIndex();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxColumnsInOrderBy() {
        try {
            return super.delegate.getMaxColumnsInOrderBy();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxColumnsInSelect() {
        try {
            return super.delegate.getMaxColumnsInSelect();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxColumnsInTable() {
        try {
            return super.delegate.getMaxColumnsInTable();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxConnections() {
        try {
            return super.delegate.getMaxConnections();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxCursorNameLength() {
        try {
            return super.delegate.getMaxCursorNameLength();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxIndexLength() {
        try {
            return super.delegate.getMaxIndexLength();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxSchemaNameLength() {
        try {
            return super.delegate.getMaxSchemaNameLength();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxProcedureNameLength() {
        try {
            return super.delegate.getMaxProcedureNameLength();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxCatalogNameLength() {
        try {
            return super.delegate.getMaxCatalogNameLength();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxRowSize() {
        try {
            return super.delegate.getMaxRowSize();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean doesMaxRowSizeIncludeBlobs() {
        try {
            return super.delegate.doesMaxRowSizeIncludeBlobs();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxStatementLength() {
        try {
            return super.delegate.getMaxStatementLength();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxStatements() {
        try {
            return super.delegate.getMaxStatements();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxTableNameLength() {
        try {
            return super.delegate.getMaxTableNameLength();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxTablesInSelect() {
        try {
            return super.delegate.getMaxTablesInSelect();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getMaxUserNameLength() {
        try {
            return super.delegate.getMaxUserNameLength();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getDefaultTransactionIsolation() {
        try {
            return super.delegate.getDefaultTransactionIsolation();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsTransactions() {
        try {
            return super.delegate.supportsTransactions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsTransactionIsolationLevel(final int n) {
        try {
            return super.delegate.supportsTransactionIsolationLevel(n);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() {
        try {
            return super.delegate.supportsDataDefinitionAndDataManipulationTransactions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsDataManipulationTransactionsOnly() {
        try {
            return super.delegate.supportsDataManipulationTransactionsOnly();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean dataDefinitionCausesTransactionCommit() {
        try {
            return super.delegate.dataDefinitionCausesTransactionCommit();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean dataDefinitionIgnoredInTransactions() {
        try {
            return super.delegate.dataDefinitionIgnoredInTransactions();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getProcedures(final String s, final String s2, final String s3) {
        try {
            return super.getProcedures(s, s2, s3);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getProcedureColumns(final String s, final String s2, final String s3, final String s4) {
        try {
            return super.getProcedureColumns(s, s2, s3, s4);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getTables(final String s, final String s2, final String s3, final String[] array) {
        try {
            return super.getTables(s, s2, s3, array);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getSchemas() {
        try {
            return super.getSchemas();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getCatalogs() {
        try {
            return super.getCatalogs();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getTableTypes() {
        try {
            return super.getTableTypes();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getColumns(final String s, final String s2, final String s3, final String s4) {
        try {
            return super.getColumns(s, s2, s3, s4);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getColumnPrivileges(final String s, final String s2, final String s3, final String s4) {
        try {
            return super.getColumnPrivileges(s, s2, s3, s4);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getTablePrivileges(final String s, final String s2, final String s3) {
        try {
            return super.getTablePrivileges(s, s2, s3);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getBestRowIdentifier(final String s, final String s2, final String s3, final int n, final boolean b) {
        try {
            return super.getBestRowIdentifier(s, s2, s3, n, b);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getVersionColumns(final String s, final String s2, final String s3) {
        try {
            return super.getVersionColumns(s, s2, s3);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getPrimaryKeys(final String s, final String s2, final String s3) {
        try {
            return super.getPrimaryKeys(s, s2, s3);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getImportedKeys(final String s, final String s2, final String s3) {
        try {
            return super.getImportedKeys(s, s2, s3);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getExportedKeys(final String s, final String s2, final String s3) {
        try {
            return super.getExportedKeys(s, s2, s3);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getCrossReference(final String s, final String s2, final String s3, final String s4, final String s5, final String s6) {
        try {
            return super.getCrossReference(s, s2, s3, s4, s5, s6);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getTypeInfo() {
        try {
            return super.getTypeInfo();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getIndexInfo(final String s, final String s2, final String s3, final boolean b, final boolean b2) {
        try {
            return super.getIndexInfo(s, s2, s3, b, b2);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsResultSetType(final int n) {
        try {
            return super.delegate.supportsResultSetType(n);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsResultSetConcurrency(final int n, final int n2) {
        try {
            return super.delegate.supportsResultSetConcurrency(n, n2);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean ownUpdatesAreVisible(final int n) {
        try {
            return super.delegate.ownUpdatesAreVisible(n);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean ownDeletesAreVisible(final int n) {
        try {
            return super.delegate.ownDeletesAreVisible(n);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean ownInsertsAreVisible(final int n) {
        try {
            return super.delegate.ownInsertsAreVisible(n);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean othersUpdatesAreVisible(final int n) {
        try {
            return super.delegate.othersUpdatesAreVisible(n);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean othersDeletesAreVisible(final int n) {
        try {
            return super.delegate.othersDeletesAreVisible(n);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean othersInsertsAreVisible(final int n) {
        try {
            return super.delegate.othersInsertsAreVisible(n);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean updatesAreDetected(final int n) {
        try {
            return super.delegate.updatesAreDetected(n);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean deletesAreDetected(final int n) {
        try {
            return super.delegate.deletesAreDetected(n);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean insertsAreDetected(final int n) {
        try {
            return super.delegate.insertsAreDetected(n);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsBatchUpdates() {
        try {
            return super.delegate.supportsBatchUpdates();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getUDTs(final String s, final String s2, final String s3, final int[] array) {
        try {
            return super.getUDTs(s, s2, s3, array);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsSavepoints() {
        try {
            return super.delegate.supportsSavepoints();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsNamedParameters() {
        try {
            return super.delegate.supportsNamedParameters();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsMultipleOpenResults() {
        try {
            return super.delegate.supportsMultipleOpenResults();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsGetGeneratedKeys() {
        try {
            return super.delegate.supportsGetGeneratedKeys();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getSuperTypes(final String s, final String s2, final String s3) {
        try {
            return super.getSuperTypes(s, s2, s3);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getSuperTables(final String s, final String s2, final String s3) {
        try {
            return super.getSuperTables(s, s2, s3);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getAttributes(final String s, final String s2, final String s3, final String s4) {
        try {
            return super.getAttributes(s, s2, s3, s4);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsResultSetHoldability(final int n) {
        try {
            return super.delegate.supportsResultSetHoldability(n);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getResultSetHoldability() {
        try {
            return super.delegate.getResultSetHoldability();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getDatabaseMajorVersion() {
        try {
            return super.delegate.getDatabaseMajorVersion();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getDatabaseMinorVersion() {
        try {
            return super.delegate.getDatabaseMinorVersion();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getJDBCMajorVersion() {
        try {
            return super.delegate.getJDBCMajorVersion();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getJDBCMinorVersion() {
        try {
            return super.delegate.getJDBCMinorVersion();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public int getSQLStateType() {
        try {
            return super.delegate.getSQLStateType();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean locatorsUpdateCopy() {
        try {
            return super.delegate.locatorsUpdateCopy();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsStatementPooling() {
        try {
            return super.delegate.supportsStatementPooling();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public RowIdLifetime getRowIdLifetime() {
        try {
            return super.delegate.getRowIdLifetime();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getSchemas(final String s, final String s2) {
        try {
            return super.getSchemas(s, s2);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() {
        try {
            return super.delegate.supportsStoredFunctionsUsingCallSyntax();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean autoCommitFailureClosesAllResultSets() {
        try {
            return super.delegate.autoCommitFailureClosesAllResultSets();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getClientInfoProperties() {
        try {
            return super.getClientInfoProperties();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getFunctions(final String s, final String s2, final String s3) {
        try {
            return super.getFunctions(s, s2, s3);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getFunctionColumns(final String s, final String s2, final String s3, final String s4) {
        try {
            return super.getFunctionColumns(s, s2, s3, s4);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public ResultSet getPseudoColumns(final String s, final String s2, final String s3, final String s4) {
        try {
            return super.getPseudoColumns(s, s2, s3, s4);
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean generatedKeyAlwaysReturned() {
        try {
            return super.delegate.generatedKeyAlwaysReturned();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public long getMaxLogicalLobSize() {
        try {
            return super.delegate.getMaxLogicalLobSize();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    @Override
    public boolean supportsRefCursors() {
        try {
            return super.delegate.supportsRefCursors();
        }
        catch (SQLException ex) {
            throw this.checkException(ex);
        }
    }
    
    HikariProxyDatabaseMetaData(final ProxyConnection proxyConnection, final DatabaseMetaData databaseMetaData) {
        super(proxyConnection, databaseMetaData);
    }
}
