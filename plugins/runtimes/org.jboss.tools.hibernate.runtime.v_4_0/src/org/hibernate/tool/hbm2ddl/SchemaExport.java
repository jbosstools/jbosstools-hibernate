/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.tool.hbm2ddl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ConfigHelper;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.config.spi.ConfigurationService;
import org.hibernate.service.internal.StandardServiceRegistryImpl;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.jboss.logging.Logger;

/**
 * Commandline tool to export table schema to the database. This class may also be called from inside an application.
 *
 * @author Daniel Bradby
 * @author Gavin King
 * @author Steve Ebersole
 */
public class SchemaExport {
    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaExport.class.getName());
	private static final String DEFAULT_IMPORT_FILE = "/import.sql";

	public static enum Type {
		CREATE,
		DROP,
		NONE,
		BOTH;

		public boolean doCreate() {
			return this == BOTH || this == CREATE;
		}

		public boolean doDrop() {
			return this == BOTH || this == DROP;
		}
	}

	private final ConnectionHelper connectionHelper;
	private final SqlStatementLogger sqlStatementLogger;
	private final SqlExceptionHelper sqlExceptionHelper;
	private final String[] dropSQL;
	private final String[] createSQL;
	private final String importFiles;

	private final List<Exception> exceptions = new ArrayList<Exception>();

	private Formatter formatter;
	private ImportSqlCommandExtractor importSqlCommandExtractor = ImportSqlCommandExtractorInitiator.DEFAULT_EXTRACTOR;

	private String outputFile = null;
	private String delimiter;
	private boolean haltOnError = false;

	public SchemaExport(ServiceRegistry serviceRegistry, Configuration configuration) {
		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper(
				serviceRegistry.getService( ConnectionProvider.class )
		);
		this.sqlStatementLogger = serviceRegistry.getService( JdbcServices.class ).getSqlStatementLogger();
		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
		this.sqlExceptionHelper = serviceRegistry.getService( JdbcServices.class ).getSqlExceptionHelper();

		this.importFiles = ConfigurationHelper.getString(
				AvailableSettings.HBM2DDL_IMPORT_FILES,
				configuration.getProperties(),
				DEFAULT_IMPORT_FILE
		);

		final Dialect dialect = serviceRegistry.getService( JdbcServices.class ).getDialect();
		this.dropSQL = configuration.generateDropSchemaScript( dialect );
		this.createSQL = configuration.generateSchemaCreationScript( dialect );
	}

	public SchemaExport(MetadataImplementor metadata) {
		ServiceRegistry serviceRegistry = metadata.getServiceRegistry();
		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper(
				serviceRegistry.getService( ConnectionProvider.class )
		);
        JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
		this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
		this.sqlExceptionHelper = jdbcServices.getSqlExceptionHelper();

		this.importFiles = ConfigurationHelper.getString(
				AvailableSettings.HBM2DDL_IMPORT_FILES,
				serviceRegistry.getService( ConfigurationService.class ).getSettings(),
				DEFAULT_IMPORT_FILE
		);

		final Dialect dialect = jdbcServices.getDialect();
		this.dropSQL = metadata.getDatabase().generateDropSchemaScript( dialect );
		this.createSQL = metadata.getDatabase().generateSchemaCreationScript( dialect );
	}

	/**
	 * Create a schema exporter for the given Configuration
	 *
	 * @param configuration The configuration from which to build a schema export.
	 * @throws HibernateException Indicates problem preparing for schema export.
	 */
	public SchemaExport(Configuration configuration) {
		this( configuration, configuration.getProperties() );
	}

	/**
	 * Create a schema exporter for the given Configuration, with the given
	 * database connection properties.
	 *
	 * @param configuration The configuration from which to build a schema export.
	 * @param properties The properties from which to configure connectivity etc.
	 * @throws HibernateException Indicates problem preparing for schema export.
	 *
	 * @deprecated properties may be specified via the Configuration object
	 */
	@Deprecated
    public SchemaExport(Configuration configuration, Properties properties) throws HibernateException {
		final Dialect dialect = Dialect.getDialect( properties );

		Properties props = new Properties();
		props.putAll( dialect.getDefaultProperties() );
		props.putAll( properties );
		this.connectionHelper = new ManagedProviderConnectionHelper( props );

		this.sqlStatementLogger = new SqlStatementLogger( false, true );
		this.formatter = FormatStyle.DDL.getFormatter();
		this.sqlExceptionHelper = new SqlExceptionHelper();

		this.importFiles = ConfigurationHelper.getString(
				AvailableSettings.HBM2DDL_IMPORT_FILES,
				properties,
				DEFAULT_IMPORT_FILE
		);

		this.dropSQL = configuration.generateDropSchemaScript( dialect );
		this.createSQL = configuration.generateSchemaCreationScript( dialect );
	}

	/**
	 * Create a schema exporter for the given Configuration, using the supplied connection for connectivity.
	 *
	 * @param configuration The configuration to use.
	 * @param connection The JDBC connection to use.
	 * @throws HibernateException Indicates problem preparing for schema export.
	 */
	public SchemaExport(Configuration configuration, Connection connection) throws HibernateException {
		this.connectionHelper = new SuppliedConnectionHelper( connection );

		this.sqlStatementLogger = new SqlStatementLogger( false, true );
		this.formatter = FormatStyle.DDL.getFormatter();
		this.sqlExceptionHelper = new SqlExceptionHelper();

		this.importFiles = ConfigurationHelper.getString(
				AvailableSettings.HBM2DDL_IMPORT_FILES,
				configuration.getProperties(),
				DEFAULT_IMPORT_FILE
		);

		final Dialect dialect = Dialect.getDialect( configuration.getProperties() );
		this.dropSQL = configuration.generateDropSchemaScript( dialect );
		this.createSQL = configuration.generateSchemaCreationScript( dialect );
	}

	public SchemaExport(
			ConnectionHelper connectionHelper,
			String[] dropSql,
			String[] createSql) {
		this.connectionHelper = connectionHelper;
		this.dropSQL = dropSql;
		this.createSQL = createSql;
		this.importFiles = "";
		this.sqlStatementLogger = new SqlStatementLogger( false, true );
		this.sqlExceptionHelper = new SqlExceptionHelper();
		this.formatter = FormatStyle.DDL.getFormatter();
	}

	/**
	 * For generating a export script file, this is the file which will be written.
	 *
	 * @param filename The name of the file to which to write the export script.
	 * @return this
	 */
	public SchemaExport setOutputFile(String filename) {
		outputFile = filename;
		return this;
	}

	/**
	 * Set the end of statement delimiter
	 *
	 * @param delimiter The delimiter
	 * @return this
	 */
	public SchemaExport setDelimiter(String delimiter) {
		this.delimiter = delimiter;
		return this;
	}

	/**
	 * Should we format the sql strings?
	 *
	 * @param format Should we format SQL strings
	 * @return this
	 */
	public SchemaExport setFormat(boolean format) {
		this.formatter = ( format ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
		return this;
	}

	/**
	 * Set <i>import.sql</i> command extractor. By default {@link SingleLineSqlCommandExtractor} is used.
	 *
	 * @param importSqlCommandExtractor <i>import.sql</i> command extractor.
	 * @return this
	 */
	public SchemaExport setImportSqlCommandExtractor(ImportSqlCommandExtractor importSqlCommandExtractor) {
		this.importSqlCommandExtractor = importSqlCommandExtractor;
		return this;
	}

	/**
	 * Should we stop once an error occurs?
	 *
	 * @param haltOnError True if export should stop after error.
	 * @return this
	 */
	public SchemaExport setHaltOnError(boolean haltOnError) {
		this.haltOnError = haltOnError;
		return this;
	}

	/**
	 * Run the schema creation script; drop script is automatically
	 * executed before running the creation script.
	 *
	 * @param script print the DDL to the console
	 * @param export export the script to the database
	 */
	public void create(boolean script, boolean export) {
		create( Target.interpret( script, export ) );
	}

	/**
	 * Run the schema creation script; drop script is automatically
	 * executed before running the creation script.
	 *
	 * @param output the target of the script.
	 */
	public void create(Target output) {
		// need to drop tables before creating so need to specify Type.BOTH
		execute( output, Type.BOTH );
	}

	/**
	 * Run the drop schema script.
	 *
	 * @param script print the DDL to the console
	 * @param export export the script to the database
	 */
	public void drop(boolean script, boolean export) {
		drop( Target.interpret( script, export ) );
	}

	public void drop(Target output) {
		execute( output, Type.DROP );
	}

	public void execute(boolean script, boolean export, boolean justDrop, boolean justCreate) {
		execute( Target.interpret( script, export ), interpretType( justDrop, justCreate ) );
	}

	private Type interpretType(boolean justDrop, boolean justCreate) {
		if ( justDrop ) {
			return Type.DROP;
		}
		else if ( justCreate ) {
			return Type.CREATE;
		}
		else {
			return Type.BOTH;
		}
	}

	public void execute(Target output, Type type) {
		if ( (outputFile == null && output == Target.NONE) || type == SchemaExport.Type.NONE ) {
			return;
		}
		exceptions.clear();

		LOG.runningHbm2ddlSchemaExport();

		final List<NamedReader> importFileReaders = new ArrayList<NamedReader>();
		for ( String currentFile : importFiles.split(",") ) {
			try {
				final String resourceName = currentFile.trim();
				InputStream stream = ConfigHelper.getResourceAsStream( resourceName );
				importFileReaders.add( new NamedReader( resourceName, stream ) );
			}
			catch ( HibernateException e ) {
				LOG.debugf("Import file not found: %s", currentFile);
			}
		}

		final List<Exporter> exporters = new ArrayList<Exporter>();
		try {
			// prepare exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if ( output.doScript() ) {
				exporters.add( new ScriptExporter() );
			}
			if ( outputFile != null ) {
				exporters.add( new FileExporter( outputFile ) );
			}
			if ( output.doExport() ) {
				exporters.add( new DatabaseExporter( connectionHelper, sqlExceptionHelper ) );
			}

			// perform exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if ( type.doDrop() ) {
				perform( dropSQL, exporters );
			}
			if ( type.doCreate() ) {
				perform( createSQL, exporters );
				if ( ! importFileReaders.isEmpty() ) {
					for ( NamedReader namedReader : importFileReaders ) {
						importScript( namedReader, exporters );
					}
				}
			}
		}
		catch (Exception e) {
			exceptions.add( e );
			LOG.schemaExportUnsuccessful( e );
		}
		finally {
			// release exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			for ( Exporter exporter : exporters ) {
				try {
					exporter.release();
				}
				catch (Exception ignore) {
				}
			}

			// release the named readers from import scripts
			for ( NamedReader namedReader : importFileReaders ) {
				try {
					namedReader.getReader().close();
				}
				catch (Exception ignore) {
				}
			}
            LOG.schemaExportComplete();
		}
	}

	private void perform(String[] sqlCommands, List<Exporter> exporters) {
		for ( String sqlCommand : sqlCommands ) {
			String formatted = formatter.format( sqlCommand );
	        if ( delimiter != null ) {
				formatted += delimiter;
			}
			sqlStatementLogger.logStatement( sqlCommand, formatter );
			for ( Exporter exporter : exporters ) {
				try {
					exporter.export( formatted );
				}
				catch (Exception e) {
					if ( haltOnError ) {
						throw new HibernateException( "Error during DDL export", e );
					}
					exceptions.add( e );
					LOG.unsuccessfulCreate( sqlCommand );
					LOG.error( e.getMessage() );
				}
			}
		}
	}

	private void importScript(NamedReader namedReader, List<Exporter> exporters) throws Exception {
		BufferedReader reader = new BufferedReader( namedReader.getReader() );
		String[] statements = importSqlCommandExtractor.extractCommands( reader );
		if (statements != null) {
			for ( String statement : statements ) {
				if ( statement != null ) {
					String trimmedSql = statement.trim();
					if ( trimmedSql.endsWith( ";" )) {
						trimmedSql = trimmedSql.substring( 0, statement.length() - 1 );
					}
					if ( !StringHelper.isEmpty( trimmedSql ) ) {
						try {
							for ( Exporter exporter : exporters ) {
								if ( exporter.acceptsImportScripts() ) {
									exporter.export( trimmedSql );
								}
							}
						}
						catch ( Exception e ) {
							throw new ImportScriptException( "Error during statement execution (file: '" + namedReader.getName() + "'): " + trimmedSql, e );
						}
					}
				}
			}
		}
	}

	private static class NamedReader {
		private final Reader reader;
		private final String name;

		public NamedReader(String name, InputStream stream) {
			this.name = name;
			this.reader = new InputStreamReader( stream );
		}

		public Reader getReader() {
			return reader;
		}

		public String getName() {
			return name;
		}
	}

	private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
		Environment.verifyProperties( properties );
		ConfigurationHelper.resolvePlaceHolders( properties );
		return (StandardServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
	}

	public static void main(String[] args) {
		try {
			Configuration cfg = new Configuration();

			boolean script = true;
			boolean drop = false;
			boolean create = false;
			boolean halt = false;
			boolean export = true;
			String outFile = null;
			String importFile = DEFAULT_IMPORT_FILE;
			String propFile = null;
			boolean format = false;
			String delim = null;

			for ( int i = 0; i < args.length; i++ ) {
				if ( args[i].startsWith( "--" ) ) {
					if ( args[i].equals( "--quiet" ) ) {
						script = false;
					}
					else if ( args[i].equals( "--drop" ) ) {
						drop = true;
					}
					else if ( args[i].equals( "--create" ) ) {
						create = true;
					}
					else if ( args[i].equals( "--haltonerror" ) ) {
						halt = true;
					}
					else if ( args[i].equals( "--text" ) ) {
						export = false;
					}
					else if ( args[i].startsWith( "--output=" ) ) {
						outFile = args[i].substring( 9 );
					}
					else if ( args[i].startsWith( "--import=" ) ) {
						importFile = args[i].substring( 9 );
					}
					else if ( args[i].startsWith( "--properties=" ) ) {
						propFile = args[i].substring( 13 );
					}
					else if ( args[i].equals( "--format" ) ) {
						format = true;
					}
					else if ( args[i].startsWith( "--delimiter=" ) ) {
						delim = args[i].substring( 12 );
					}
					else if ( args[i].startsWith( "--config=" ) ) {
						cfg.configure( args[i].substring( 9 ) );
					}
					else if ( args[i].startsWith( "--naming=" ) ) {
						cfg.setNamingStrategy(
								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) )
										.newInstance()
						);
					}
				}
				else {
					String filename = args[i];
					if ( filename.endsWith( ".jar" ) ) {
						cfg.addJar( new File( filename ) );
					}
					else {
						cfg.addFile( filename );
					}
				}

			}

			if ( propFile != null ) {
				Properties props = new Properties();
				props.putAll( cfg.getProperties() );
				props.load( new FileInputStream( propFile ) );
				cfg.setProperties( props );
			}

			if (importFile != null) {
				cfg.setProperty( AvailableSettings.HBM2DDL_IMPORT_FILES, importFile );
			}

			StandardServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
			try {
				SchemaExport se = new SchemaExport( serviceRegistry, cfg )
						.setHaltOnError( halt )
						.setOutputFile( outFile )
						.setDelimiter( delim )
						.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
				if ( format ) {
					se.setFormat( true );
				}
				se.execute( script, export, drop, create );
			}
			finally {
				serviceRegistry.destroy();
			}
		}
		catch ( Exception e ) {
            LOG.unableToCreateSchema(e);
			e.printStackTrace();
		}
	}

	/**
	 * Returns a List of all Exceptions which occured during the export.
	 *
	 * @return A List containig the Exceptions occured during the export
	 */
	public List<?> getExceptions() {
		return exceptions;
	}

}
