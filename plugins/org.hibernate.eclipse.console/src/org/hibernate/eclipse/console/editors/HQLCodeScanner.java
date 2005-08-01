package org.hibernate.eclipse.console.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * This class implements a RuleBaseScanner for SQL source code text.
 */
public class HQLCodeScanner extends RuleBasedScanner {

    /** Defines the SQL keywords. */
    private static String[] fgKeywords = {
            "ABSOLUTE", //$NON-NLS-1$
            "ACQUIRE", //$NON-NLS-1$
            "ACTION", //$NON-NLS-1$
            "ADD", //$NON-NLS-1$
            "ALL", //$NON-NLS-1$
            "ALLOCATE", //$NON-NLS-1$
            "ALTER", //$NON-NLS-1$
            "AND", //$NON-NLS-1$
            "ANY", //$NON-NLS-1$
            "ARE", //$NON-NLS-1$
            "AS", //$NON-NLS-1$
            "ASC", //$NON-NLS-1$
            "ASSERTION", //$NON-NLS-1$
            "AT", //$NON-NLS-1$
            "AUDIT", //$NON-NLS-1$
            "AUTHORIZATION", //$NON-NLS-1$
            "AVG", //$NON-NLS-1$
            "BEGIN", //$NON-NLS-1$
            "BETWEEN", //$NON-NLS-1$
            "BIT_LENGTH", //$NON-NLS-1$
            "BOTH", //$NON-NLS-1$
            "BUFFERPOOL", //$NON-NLS-1$
            "BY", //$NON-NLS-1$
            "CALL", //$NON-NLS-1$
            "CAPTURE", //$NON-NLS-1$
            "CASCADED", //$NON-NLS-1$
            "CASE", //$NON-NLS-1$
            "CAST", //$NON-NLS-1$
            "CATALOG", //$NON-NLS-1$
            "CCSID", //$NON-NLS-1$
            "CHAR", //$NON-NLS-1$
            "CHAR_LENGTH", //$NON-NLS-1$
            "CHARACTER", //$NON-NLS-1$
            "CHARACTER_LENGTH", //$NON-NLS-1$
            "CHECK", //$NON-NLS-1$
            "CLOSE", //$NON-NLS-1$
            "CLUSTER", //$NON-NLS-1$
            "COALESCE", //$NON-NLS-1$
            "COLLATE", //$NON-NLS-1$
            "COLLATION", //$NON-NLS-1$
            "COLLECTION", //$NON-NLS-1$
            "COLUMN", //$NON-NLS-1$
            "COMMENT", //$NON-NLS-1$
            "COMMIT", //$NON-NLS-1$
            "CONCAT", //$NON-NLS-1$
            "CONNECT", //$NON-NLS-1$
            "CONNECTION", //$NON-NLS-1$
            "CONSTRAINT", //$NON-NLS-1$
            "CONSTRAINTS", //$NON-NLS-1$
            "CONTINUE", //$NON-NLS-1$
            "CONVERT", //$NON-NLS-1$
            "CORRESPONDING", //$NON-NLS-1$
            "COUNT", //$NON-NLS-1$
            "CREATE", //$NON-NLS-1$
            "CROSS", //$NON-NLS-1$
            "CURRENT", //$NON-NLS-1$
            "CURRENT_DATE", //$NON-NLS-1$
            "CURRENT_SERVER", //$NON-NLS-1$
            "CURRENT_TIME", //$NON-NLS-1$
            "CURRENT_TIMESTAMP", //$NON-NLS-1$
            "CURRENT_TIMEZONE", //$NON-NLS-1$
            "CURRENT_USER", //$NON-NLS-1$
            "CURSOR", //$NON-NLS-1$
            "DATABASE", //$NON-NLS-1$
            "DATE", //$NON-NLS-1$
            "DAY", //$NON-NLS-1$
            "DAYS", //$NON-NLS-1$
            "DBA", //$NON-NLS-1$
            "DBSPACE", //$NON-NLS-1$
            "DEALLOCATE", //$NON-NLS-1$
            "DEC", //$NON-NLS-1$
            "DECIMAL", //$NON-NLS-1$
            "DECLARE", //$NON-NLS-1$
            "DEFAULT", //$NON-NLS-1$
            "DEFERRABLE", //$NON-NLS-1$
            "DEFERRED", //$NON-NLS-1$
            "DELETE", //$NON-NLS-1$
            "DESC", //$NON-NLS-1$
            "DESCRIBE", //$NON-NLS-1$
            "DESCRIPTOR", //$NON-NLS-1$
            "DIAGNOSTICS", //$NON-NLS-1$
            "DISCONNECT", //$NON-NLS-1$
            "DISTINCT", //$NON-NLS-1$
            "DOMAIN", //$NON-NLS-1$
            "DOUBLE", //$NON-NLS-1$
            "DROP", //$NON-NLS-1$
            "EDITPROC", //$NON-NLS-1$
            "ELSE", //$NON-NLS-1$
            "END", //$NON-NLS-1$
            "END-EXEC", //$NON-NLS-1$
            "ERASE", //$NON-NLS-1$
            "ESCAPE", //$NON-NLS-1$
            "EXCEPT", //$NON-NLS-1$
            "EXCEPTION", //$NON-NLS-1$
            "EXCLUSIVE", //$NON-NLS-1$
            "EXECUTE", //$NON-NLS-1$
            "EXISTS", //$NON-NLS-1$
            "EXPLAIN", //$NON-NLS-1$
            "EXTERNAL", //$NON-NLS-1$
            "EXTRACT", //$NON-NLS-1$
            "FETCH", //$NON-NLS-1$
            "FIELDPROC", //$NON-NLS-1$
            "FIRST", //$NON-NLS-1$
            "FLOAT", //$NON-NLS-1$
            "FOR", //$NON-NLS-1$
            "FOREIGN", //$NON-NLS-1$
            "FOUND", //$NON-NLS-1$
            "FROM", //$NON-NLS-1$
            "FULL", //$NON-NLS-1$
            "FULL", //$NON-NLS-1$
            "GET", //$NON-NLS-1$
            "GLOBAL", //$NON-NLS-1$
            "GO", //$NON-NLS-1$
            "GOTO", //$NON-NLS-1$
            "GRANT", //$NON-NLS-1$
            "GRAPHIC", //$NON-NLS-1$
            "GROUP", //$NON-NLS-1$
            "HAVING", //$NON-NLS-1$
            "HOUR", //$NON-NLS-1$
            "HOURS", //$NON-NLS-1$
            "IDENTIFIED", //$NON-NLS-1$
            "IDENTITY", //$NON-NLS-1$
            "IMMEDIATE", //$NON-NLS-1$
            "IN", //$NON-NLS-1$
            "INDEX", //$NON-NLS-1$
            "INDICATOR", //$NON-NLS-1$
            "INITIALLY", //$NON-NLS-1$
            "INNER", //$NON-NLS-1$
            "INNER", //$NON-NLS-1$
            "INOUT", //$NON-NLS-1$
            "INPUT", //$NON-NLS-1$
            "INSENSITIVE", //$NON-NLS-1$
            "INSERT", //$NON-NLS-1$
            "INTERSECT", //$NON-NLS-1$
            "INTERVAL", //$NON-NLS-1$
            "INTO", //$NON-NLS-1$
            "IS", //$NON-NLS-1$
            "ISOLATION", //$NON-NLS-1$
            "JOIN", //$NON-NLS-1$
            "JOIN", //$NON-NLS-1$
            "KEY", //$NON-NLS-1$
            "LABEL", //$NON-NLS-1$
            "LANGUAGE", //$NON-NLS-1$
            "LAST", //$NON-NLS-1$
            "LEADING", //$NON-NLS-1$
            "LEFT", //$NON-NLS-1$
            "LEFT", //$NON-NLS-1$
            "LEVEL", //$NON-NLS-1$
            "LIKE", //$NON-NLS-1$
            "LOCAL", //$NON-NLS-1$
            "LOCK", //$NON-NLS-1$
            "LOCKSIZE", //$NON-NLS-1$
            "LONG", //$NON-NLS-1$
            "LOWER", //$NON-NLS-1$
            "MATCH", //$NON-NLS-1$
            "MAX", //$NON-NLS-1$
            "MICROSECOND", //$NON-NLS-1$
            "MICROSECONDS", //$NON-NLS-1$
            "MIN", //$NON-NLS-1$
            "MINUTE", //$NON-NLS-1$
            "MINUTES", //$NON-NLS-1$
            "MODE", //$NON-NLS-1$
            "MODULE", //$NON-NLS-1$
            "MONTH", //$NON-NLS-1$
            "MONTHS", //$NON-NLS-1$
            "NAMED", //$NON-NLS-1$
            "NAMES", //$NON-NLS-1$
            "NATIONAL", //$NON-NLS-1$
            "NATURAL", //$NON-NLS-1$
            "NCHAR", //$NON-NLS-1$
            "NEXT", //$NON-NLS-1$
            "NHEADER", //$NON-NLS-1$
            "NO", //$NON-NLS-1$
            "NOT", //$NON-NLS-1$
            "NULL", //$NON-NLS-1$
            "NULLIF", //$NON-NLS-1$
            "NUMERIC", //$NON-NLS-1$
            "NUMPARTS", //$NON-NLS-1$
            "OBID", //$NON-NLS-1$
            "OCTET_LENGTH", //$NON-NLS-1$
            "OF", //$NON-NLS-1$
            "ON", //$NON-NLS-1$
            "ONLY", //$NON-NLS-1$
            "OPEN", //$NON-NLS-1$
            "OPTIMIZE", //$NON-NLS-1$
            "OPTION", //$NON-NLS-1$
            "OR", //$NON-NLS-1$
            "ORDER", //$NON-NLS-1$
            "OUT", //$NON-NLS-1$
            "OUTER", //$NON-NLS-1$
            "OUTPUT", //$NON-NLS-1$
            "OVERLAPS", //$NON-NLS-1$
            "PACKAGE", //$NON-NLS-1$
            "PAD", //$NON-NLS-1$
            "PAGE", //$NON-NLS-1$
            "PAGES", //$NON-NLS-1$
            "PART", //$NON-NLS-1$
            "PARTIAL", //$NON-NLS-1$
            "PCTFREE", //$NON-NLS-1$
            "PCTINDEX", //$NON-NLS-1$
            "PLAN", //$NON-NLS-1$
            "POSITION", //$NON-NLS-1$
            "PRECISION", //$NON-NLS-1$
            "PREPARE", //$NON-NLS-1$
            "PRESERVE", //$NON-NLS-1$
            "PRIMARY", //$NON-NLS-1$
            "PRIOR", //$NON-NLS-1$
            "PRIQTY", //$NON-NLS-1$
            "PRIVATE", //$NON-NLS-1$
            "PRIVILEGES", //$NON-NLS-1$
            "PROCEDURE", //$NON-NLS-1$
            "PROGRAM", //$NON-NLS-1$
            "PUBLIC", //$NON-NLS-1$
            "READ", //$NON-NLS-1$
            "REAL", //$NON-NLS-1$
            "REFERENCES", //$NON-NLS-1$
            "RELATIVE", //$NON-NLS-1$
            "RELEASE", //$NON-NLS-1$
            "RESET", //$NON-NLS-1$
            "RESOURCE", //$NON-NLS-1$
            "REVOKE", //$NON-NLS-1$
            "RIGHT", //$NON-NLS-1$
            "RIGHT", //$NON-NLS-1$
            "ROLLBACK", //$NON-NLS-1$
            "ROW", //$NON-NLS-1$
            "ROWS", //$NON-NLS-1$
            "RRN", //$NON-NLS-1$
            "RUN", //$NON-NLS-1$
            "SCHEDULE", //$NON-NLS-1$
            "SCHEMA", //$NON-NLS-1$
            "SCROLL", //$NON-NLS-1$
            "SECOND", //$NON-NLS-1$
            "SECONDS", //$NON-NLS-1$
            "SECQTY", //$NON-NLS-1$
            "SECTION", //$NON-NLS-1$
            "SELECT", //$NON-NLS-1$
            "SESSION", //$NON-NLS-1$
            "SESSION_USER", //$NON-NLS-1$
            "SET", //$NON-NLS-1$
            "SHARE", //$NON-NLS-1$
            "SIMPLE", //$NON-NLS-1$
            "SIZE", //$NON-NLS-1$
            "SMALLINT", //$NON-NLS-1$
            "SOME", //$NON-NLS-1$
            "SPACE", //$NON-NLS-1$
            "SQL", //$NON-NLS-1$
            "SQLCODE", //$NON-NLS-1$
            "SQLERROR", //$NON-NLS-1$
            "SQLSTATE", //$NON-NLS-1$
            "STATISTICS", //$NON-NLS-1$
            "STOGROUP", //$NON-NLS-1$
            "STORPOOL", //$NON-NLS-1$
            "SUBPAGES", //$NON-NLS-1$
            "SUBSTRING", //$NON-NLS-1$
            "SUM", //$NON-NLS-1$
            "SYNONYM", //$NON-NLS-1$
            "SYSTEM_USER", //$NON-NLS-1$
            "TABLE", //$NON-NLS-1$
            "TABLESPACE", //$NON-NLS-1$
            "TEMPORARY", //$NON-NLS-1$
            "THEN", //$NON-NLS-1$
            "TIMEZONE_HOUR", //$NON-NLS-1$
            "TIMEZONE_MINUTE", //$NON-NLS-1$
            "TO", //$NON-NLS-1$
            "TRAILING", //$NON-NLS-1$
            "TRANSACTION", //$NON-NLS-1$
            "TRANSLATION", //$NON-NLS-1$
            "TRIM", //$NON-NLS-1$
            "UNION", //$NON-NLS-1$
            "UNIQUE", //$NON-NLS-1$
            "UNKNOWN", //$NON-NLS-1$
            "UPDATE", //$NON-NLS-1$
            "UPPER", //$NON-NLS-1$
            "USAGE", //$NON-NLS-1$
            "USER", //$NON-NLS-1$
            "USING", //$NON-NLS-1$
            "VALIDPROC", //$NON-NLS-1$
            "VALUE", //$NON-NLS-1$
            "VALUES", //$NON-NLS-1$
            "VARCHAR", //$NON-NLS-1$
            "VARIABLE", //$NON-NLS-1$
            "VARYING", //$NON-NLS-1$
            "VCAT", //$NON-NLS-1$
            "VIEW", //$NON-NLS-1$
            "VOLUMES", //$NON-NLS-1$
            "WHEN", //$NON-NLS-1$
            "WHENEVER", //$NON-NLS-1$
            "WHERE", //$NON-NLS-1$
            "WITH", //$NON-NLS-1$
            "WORK", //$NON-NLS-1$
            "WRITE", //$NON-NLS-1$
            "YEAR", //$NON-NLS-1$
            "YEARS", //$NON-NLS-1$
            "ZONE", //$NON-NLS-1$
            "FALSE", //$NON-NLS-1$
            "TRUE" //$NON-NLS-1$
        };
    
    /** Defines the SQL datatype names. */
    private static String[] fgDatatypes = {
            "INTEGER", //$NON-NLS-1$
            "SMALLINTEGER", //$NON-NLS-1$
            "BIGINT", //$NON-NLS-1$
            "DECIMAL", //$NON-NLS-1$
            "DOUBLE", //$NON-NLS-1$
            "REAL", //$NON-NLS-1$
            "TIME", //$NON-NLS-1$
            "TIMESTAMP", //$NON-NLS-1$
            "DATE", //$NON-NLS-1$
            "DATALINK", //$NON-NLS-1$
            "CHAR", //$NON-NLS-1$
            "VARCHAR", //$NON-NLS-1$
            "BLOB", //$NON-NLS-1$
            "CLOB", //$NON-NLS-1$
            "GRAPHIC", //$NON-NLS-1$
            "VARGRAPHIC", //$NON-NLS-1$
            "DBCLOB" //$NON-NLS-1$
        };
    
    /** Defines the SQL built-in function names. */
    private static String[] fgFunctions = {
            "ABS", //$NON-NLS-1$
            "ABSVAL", //$NON-NLS-1$
            "ACOS", //$NON-NLS-1$
            "ASCII", //$NON-NLS-1$
            "ASIN", //$NON-NLS-1$
            "ATAN", //$NON-NLS-1$
            "ATAN2", //$NON-NLS-1$
            "BIGINT", //$NON-NLS-1$
            "BLOB", //$NON-NLS-1$
            "CEILING", //$NON-NLS-1$
            "CHAR", //$NON-NLS-1$
            "CHR", //$NON-NLS-1$
            "CLOB", //$NON-NLS-1$
            "COALESCE", //$NON-NLS-1$
            "CONCAT", //$NON-NLS-1$
            "CORRELATION", //$NON-NLS-1$
            "COS", //$NON-NLS-1$
            "COT", //$NON-NLS-1$
            "COUNT", //$NON-NLS-1$
            "COUNT_BIG", //$NON-NLS-1$
            "COVARIANCE", //$NON-NLS-1$
            "DATE", //$NON-NLS-1$
            "DAY", //$NON-NLS-1$
            "DAYNAME", //$NON-NLS-1$
            "DAYOFWEEK", //$NON-NLS-1$
            "DAYOFWEEK_ISO", //$NON-NLS-1$
            "DAYOFYEAR", //$NON-NLS-1$
            "DAYS", //$NON-NLS-1$
            "DBCLOB", //$NON-NLS-1$
            "DECIMAL", //$NON-NLS-1$
            "DEGREES", //$NON-NLS-1$
            "DEREF", //$NON-NLS-1$
            "DIFFERENCE", //$NON-NLS-1$
            "DIGITS", //$NON-NLS-1$
            "DLCOMMENT", //$NON-NLS-1$
            "DLLINKTYPE", //$NON-NLS-1$
            "DLURLCOMPLETE", //$NON-NLS-1$
            "DLURLPATH", //$NON-NLS-1$
            "DLURLPATHONLY", //$NON-NLS-1$
            "DLURLSCHEME", //$NON-NLS-1$
            "DLURLSERVER", //$NON-NLS-1$
            "DLVALUE", //$NON-NLS-1$
            "DOUBLE", //$NON-NLS-1$
            "EVENT_MON_STATE", //$NON-NLS-1$
            "EXP", //$NON-NLS-1$
            "FLOAT", //$NON-NLS-1$
            "FLOOR", //$NON-NLS-1$
            "GENERATE_UNIQUE", //$NON-NLS-1$
            "GRAPHIC", //$NON-NLS-1$
            "GROUPING", //$NON-NLS-1$
            "HEX", //$NON-NLS-1$
            "HOUR", //$NON-NLS-1$
            "INSERT", //$NON-NLS-1$
            "INTEGER", //$NON-NLS-1$
            "JULIAN_DAY", //$NON-NLS-1$
            "LCASE", //$NON-NLS-1$
            "LCASE", //$NON-NLS-1$
            "LEFT", //$NON-NLS-1$
            "LENGTH", //$NON-NLS-1$
            "LN", //$NON-NLS-1$
            "LOCATE", //$NON-NLS-1$
            "LOG", //$NON-NLS-1$
            "LOG10", //$NON-NLS-1$
            "LONG_VARCHAR", //$NON-NLS-1$
            "LONG_VARGRAPHIC", //$NON-NLS-1$
            "LTRIM", //$NON-NLS-1$
            "LTRIM", //$NON-NLS-1$
            "MAX", //$NON-NLS-1$
            "MICROSECOND", //$NON-NLS-1$
            "MIDNIGHT_SECONDS", //$NON-NLS-1$
            "MIN", //$NON-NLS-1$
            "MINUTE", //$NON-NLS-1$
            "MOD", //$NON-NLS-1$
            "MONTH", //$NON-NLS-1$
            "MONTHNAME", //$NON-NLS-1$
            "NODENUMBER", //$NON-NLS-1$
            "NULLIF", //$NON-NLS-1$
            "PARTITION", //$NON-NLS-1$
            "POSSTR", //$NON-NLS-1$
            "POWER", //$NON-NLS-1$
            "QUARTER", //$NON-NLS-1$
            "RADIANS", //$NON-NLS-1$
            "RAISE_ERROR", //$NON-NLS-1$
            "RAND", //$NON-NLS-1$
            "REAL", //$NON-NLS-1$
            "REPEAT", //$NON-NLS-1$
            "REPLACE", //$NON-NLS-1$
            "RIGHT", //$NON-NLS-1$
            "ROUND", //$NON-NLS-1$
            "RTRIM", //$NON-NLS-1$
            "RTRIM", //$NON-NLS-1$
            "SECOND", //$NON-NLS-1$
            "SIGN", //$NON-NLS-1$
            "SIN", //$NON-NLS-1$
            "SMALLINT", //$NON-NLS-1$
            "SOUNDEX", //$NON-NLS-1$
            "SPACE", //$NON-NLS-1$
            "SQLCACHE_SNAPSHOT", //$NON-NLS-1$
            "SQRT", //$NON-NLS-1$
            "STDDEV", //$NON-NLS-1$
            "SUBSTR", //$NON-NLS-1$
            "SUM", //$NON-NLS-1$
            "TABLE_NAME", //$NON-NLS-1$
            "TABLE_SCHEMA", //$NON-NLS-1$
            "TAN", //$NON-NLS-1$
            "TIME", //$NON-NLS-1$
            "TIMESTAMP", //$NON-NLS-1$
            "TIMESTAMP_ISO", //$NON-NLS-1$
            "TIMESTAMPDIFF", //$NON-NLS-1$
            "TRANSLATE", //$NON-NLS-1$
            "TRUNCATE", //$NON-NLS-1$
            "TRUNC", //$NON-NLS-1$
            "TYPE_ID", //$NON-NLS-1$
            "TYPE_NAME", //$NON-NLS-1$
            "TYPE_SCHEMA", //$NON-NLS-1$
            "UCASE", //$NON-NLS-1$
            "UPPER", //$NON-NLS-1$
            "VALUE", //$NON-NLS-1$
            "VARCHAR", //$NON-NLS-1$
            "VARGRAPHIC", //$NON-NLS-1$
            "VARIANCE", //$NON-NLS-1$
            "WEEK", //$NON-NLS-1$
            "WEEK_ISO", //$NON-NLS-1$
            "YEAR" //$NON-NLS-1$
        };
    
    /** Defines an array of arrays that combines all the SQL language elements defined previously. */ 
    private static Object[] fgAllWords = { fgKeywords, fgDatatypes, fgFunctions };
    
    /**
     * This class determines if a character is a whitespace character.
     */
    public class SQLWhiteSpaceDetector implements IWhitespaceDetector {

        /**
         * Determines if the given character is a whitespace character.
         * @see org.eclipse.jface.text.rules.IWhitespaceDetector#isWhitespace(char)
         */
        public boolean isWhitespace( char c ) {
            return Character.isWhitespace( c );
        }

    } // end inner class
    
    /**
     * Constructs an instance of this class using the given color provider.
     */
    public HQLCodeScanner( HQLColorProvider colorProvider ) {
        // Define tokens for the SQL language elements.
        IToken commentToken    = null;
        IToken stringToken     = null;
        IToken keywordToken    = null;
        IToken datatypeToken   = null;
        IToken functionToken   = null;
//        IToken identifierToken = null;
        IToken delimitedIdentifierToken = null;
        IToken otherToken      = null;
        
        /* On "high contrast" displays the default text color (black) is a problem,
         * since the normal high contrast background is black.  ("High contrast" is
         * a Windows feature that helps vision-impaired people.)  When high contrast mode is enabled,
         * use different colors that show up better against a black background.
         */
        if (Display.getDefault().getHighContrast() == true) {
            commentToken    = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_COMMENT_COLOR )));
            stringToken     = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_QUOTED_LITERAL_COLOR )));
            keywordToken    = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_KEYWORD_COLOR )));
            datatypeToken   = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_KEYWORD_COLOR )));
            functionToken   = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_KEYWORD_COLOR )));
            delimitedIdentifierToken = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_DELIMITED_IDENTIFIER_COLOR )));
            otherToken      = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_DEFAULT_COLOR )));
        }
        else {
            commentToken    = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_COMMENT_COLOR )));
            stringToken     = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_QUOTED_LITERAL_COLOR )));
            keywordToken    = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_KEYWORD_COLOR ), null, SWT.BOLD));
            datatypeToken   = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_KEYWORD_COLOR )));
            functionToken   = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_KEYWORD_COLOR )));
            delimitedIdentifierToken = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_DELIMITED_IDENTIFIER_COLOR )));
            otherToken      = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_DEFAULT_COLOR )));
        }

        setDefaultReturnToken( otherToken );

        List rules = new ArrayList();

        // Add rule for single-line comments.
        rules.add( new EndOfLineRule( "--", commentToken )); //$NON-NLS-1$

        // Add rules for delimited identifiers and string literals.
        rules.add( new SingleLineRule( "'", "'", stringToken, '\\' )); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        rules.add( new SingleLineRule( "\"", "\"", delimitedIdentifierToken, '\\' )); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Add generic whitespace rule.
        rules.add( new WhitespaceRule( new SQLWhiteSpaceDetector() ));

        // Define a word rule and add SQL keywords to it.
        WordRule wordRule = new WordRule( new HQLWordDetector(), otherToken );
        String[] reservedWords = getHQLKeywords();
        for (int i = 0; i < reservedWords.length; i++) {
            wordRule.addWord( reservedWords[i], keywordToken );
            wordRule.addWord( reservedWords[i].toLowerCase(), keywordToken );
        }
        
        // Add the SQL datatype names to the word rule.
        String[] datatypes = getHQLDatatypes();
        for (int i = 0; i < datatypes.length; i++) {
            wordRule.addWord( datatypes[i], datatypeToken );
            wordRule.addWord( datatypes[i].toLowerCase(), datatypeToken );
        }

        // Add the SQL function names to the word rule.
        String[] functions = getHQLFunctionNames();
        for (int i = 0; i< functions.length; i++) {
            wordRule.addWord( functions[i], functionToken );
            wordRule.addWord( functions[i].toLowerCase(), functionToken );
        }
        
        // Add the word rule to the list of rules.
        rules.add( wordRule );

        // Convert the list of rules to an array and return it.
        IRule[] result = new IRule[ rules.size() ];
        rules.toArray( result );
        setRules( result );
    }

    /**
     * Gets an array of SQL keywords (in upper case).
     * 
     * @return the SQL reserved words
     */
    public static String[] getHQLKeywords() {
        return fgKeywords;
    }
        
    /**
     * Gets an array of SQL datatype names (in upper case).
     * 
     * @return the SQL datatype names
     */
    public static String[] getHQLDatatypes() {
        return fgDatatypes;
    }
    
    /**
     * Gets an array of SQL built-in function names (in upper case).
     * 
     * @return the SQL built-in function names
     */
    public static String[] getHQLFunctionNames() {
        return fgFunctions;
    }
    
    /**
     * Gets an array of arrays containing all SQL words, including keywords,
     * data types names, and function names (in upper case).
     * 
     * @return the array of SQL words
     */
    public static Object[] getSQLAllWords() {
        return fgAllWords;
    }
    
} // end class
