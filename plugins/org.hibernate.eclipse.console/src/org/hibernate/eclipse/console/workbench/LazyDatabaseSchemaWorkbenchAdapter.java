package org.hibernate.eclipse.console.workbench;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class LazyDatabaseSchemaWorkbenchAdapter extends BasicWorkbenchAdapter {
	
	public Object[] getChildren(Object o) {
		return getChildren(o, new NullProgressMonitor());
	}
	
	public Object[] getChildren(Object o, final IProgressMonitor monitor) {
		LazyDatabaseSchema dbs = getLazyDatabaseSchema( o );
		final DefaultDatabaseCollector db = new DefaultDatabaseCollector();
		
		ConsoleConfiguration consoleConfiguration = dbs.getConsoleConfiguration();
		readDatabaseSchema(monitor, db, consoleConfiguration, dbs.getReverseEngineeringStrategy());
				
		List result = new ArrayList();
		
		Iterator qualifierEntries = db.getQualifierEntries();
		while ( qualifierEntries.hasNext() ) {
			Map.Entry entry = (Map.Entry) qualifierEntries.next();
			result.add(new TableContainer((String) entry.getKey(),(List)entry.getValue()));
		}
		return toArray(result.iterator(), TableContainer.class);
	}

	private LazyDatabaseSchema getLazyDatabaseSchema(Object o) {
		return (LazyDatabaseSchema) o;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.TABLE);
	}

	public String getLabel(Object o) {
		return "Database";
	}

	public Object getParent(Object o) {
		return getLazyDatabaseSchema(o).getConsoleConfiguration();
	}
	
	protected void readDatabaseSchema(final IProgressMonitor monitor, final DefaultDatabaseCollector db, ConsoleConfiguration consoleConfiguration, final ReverseEngineeringStrategy strategy) {
		final Configuration configuration = consoleConfiguration.buildWith(new Configuration(), false);
		
		consoleConfiguration.getExecutionContext().execute(new ExecutionContext.Command() {
			
			public Object execute() {
				Settings settings = configuration.buildSettings();
				ConnectionProvider connectionProvider = null;
				try {
					connectionProvider = settings.getConnectionProvider();
				
					JDBCReader reader = JDBCReaderFactory.newJDBCReader(configuration.getProperties(), settings, strategy);
					reader.readDatabaseSchema(db, settings.getDefaultCatalogName(), settings.getDefaultSchemaName(), new ProgressListenerMonitor(monitor));
				} catch(HibernateException he) {
					HibernateConsolePlugin.getDefault().logErrorMessage("Problem while reading database schema", he);
					return new Object[] { "<Schema not available>"};
				}
			    finally {
					if (connectionProvider!=null) {
						connectionProvider.close();
					}				
				}
							
				return null;
			}
		});
	}

}
