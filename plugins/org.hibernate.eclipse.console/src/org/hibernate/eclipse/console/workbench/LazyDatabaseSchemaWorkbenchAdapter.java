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
import org.eclipse.ui.progress.IElementCollector;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class LazyDatabaseSchemaWorkbenchAdapter extends BasicWorkbenchAdapter {

	public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor) {
		collector.add(getChildren(object, monitor), monitor);
	}
	
	public Object[] getChildren(Object o) {
		return getChildren(o, new NullProgressMonitor());
	}
	
	public Object[] getChildren(Object o, final IProgressMonitor monitor) {
		LazyDatabaseSchema dbs = getLazyDatabaseSchema( o );
		final DefaultDatabaseCollector db = new DefaultDatabaseCollector();
		
		ConsoleConfiguration consoleConfiguration = dbs.getConsoleConfiguration();
		final Configuration configuration = consoleConfiguration.buildWith(new Configuration(), false);
		
		consoleConfiguration.getExecutionContext().execute(new ExecutionContext.Command() {
			
			public Object execute() {
				Settings settings = configuration.buildSettings();
				Connection connection = null;
				ConnectionProvider connectionProvider = null;
				try {
					connectionProvider = settings.getConnectionProvider();
					connection = connectionProvider.getConnection();
				
					JDBCReader reader = new JDBCReader(connection, settings.getSQLExceptionConverter(), new DefaultReverseEngineeringStrategy());
					reader.readDatabaseSchema(db, settings.getDefaultCatalogName(), settings.getDefaultSchemaName(), new ProgressListenerMonitor(monitor));
				} catch(HibernateException he) {
					HibernateConsolePlugin.getDefault().logErrorMessage("Problem while reading database schema", he);
				}
				catch (SQLException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage("Could not open connection for reading database schema", e);
					return new Object[] { "<Connection error>" };
				} finally {
					if (connection!=null) {
						try {
							connectionProvider.closeConnection(connection);
						}
						catch (SQLException e) {
						 //noop
						}
					}
				}
							
				return null;
			}
		});
				
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
}
