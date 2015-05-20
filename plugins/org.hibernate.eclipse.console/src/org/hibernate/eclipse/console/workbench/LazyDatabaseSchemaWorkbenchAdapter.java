/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.workbench;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;
import org.jboss.tools.hibernate.runtime.spi.IProgressListener;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.ISettings;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public class LazyDatabaseSchemaWorkbenchAdapter extends BasicWorkbenchAdapter {
	
	public Object[] getChildren(Object o) {
		return getChildren(o, new NullProgressMonitor());
	}

	public Object[] getChildren(Object o, final IProgressMonitor monitor) {
		LazyDatabaseSchema dbs = getLazyDatabaseSchema( o );
		dbs.setConnected(false);
		dbs.setErrorFlag(false);
		ConsoleConfiguration consoleConfiguration = dbs.getConsoleConfiguration();
		Object[] res;
		try {
			IDatabaseCollector db = readDatabaseSchema(monitor, consoleConfiguration, dbs.getReverseEngineeringStrategy());

			List<TableContainer> result = new ArrayList<TableContainer>();

			Iterator<Map.Entry<String, List<ITable>>> qualifierEntries = db.getQualifierEntries();
			while (qualifierEntries.hasNext()) {
				Map.Entry<String, List<ITable>> entry = qualifierEntries.next();
				result.add(new TableContainer(entry.getKey(), entry.getValue()));
			}
			res = toArray(result.iterator(), Object.class, new Comparator<Object>() {
				public int compare(Object arg0, Object arg1) {
					return getName(arg0).compareTo(getName(arg1));
				}
				private String getName(Object o) {
					String result = null;
					try {
						Method m = o.getClass().getMethod("getName", new Class[] {}); //$NON-NLS-1$
						result = (String) m.invoke(o, new Object[] {});
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					return result;
				}
			});
			dbs.setConnected(true);
		} catch (Exception e) {
			HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.LazyDatabaseSchemaWorkbenchAdapter_problems_while_reading_database_schema, e);
			String out = NLS.bind(HibernateConsoleMessages.LazyDatabaseSchemaWorkbenchAdapter_reading_schema_error, e.getMessage());
			res = new Object[] { out };
			dbs.setErrorFlag(true);
		}
		return res;
	}

	private LazyDatabaseSchema getLazyDatabaseSchema(Object o) {
		return (LazyDatabaseSchema) o;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		LazyDatabaseSchema dbs = getLazyDatabaseSchema(object);
		Map<String, Integer> imageMap = new HashMap<String, Integer>();
		if (dbs.isConnected()) {
			imageMap.put(ImageConstants.OVR_DBS_CONNECTED, OverlayImageIcon.BOTTOM_LEFT);
		}
		if (dbs.getErrorFlag()) {
			imageMap.put(ImageConstants.OVR_WARNING, OverlayImageIcon.BOTTOM_LEFT);
		}
        return new OverlayImageIcon(EclipseImages.getImage(ImageConstants.TABLE), imageMap);
	}

	public String getLabel(Object o) {
		return HibernateConsoleMessages.LazyDatabaseSchemaWorkbenchAdapter_database;
	}

	public Object getParent(Object o) {
		return getLazyDatabaseSchema(o).getConsoleConfiguration();
	}

	protected IDatabaseCollector readDatabaseSchema(final IProgressMonitor monitor, final ConsoleConfiguration consoleConfiguration, final IReverseEngineeringStrategy strategy) {
		final IConfiguration configuration = consoleConfiguration.buildWith(null, false);
		return (IDatabaseCollector) consoleConfiguration.execute(new ExecutionContext.Command() {

			public Object execute() {
				IDatabaseCollector db = null;
				ISettings settings = consoleConfiguration.getSettings(configuration);
				try {
					IService service = consoleConfiguration.getHibernateExtension().getHibernateService();
					IJDBCReader reader = service.newJDBCReader(configuration, settings, strategy);
					db = service.newDatabaseCollector(reader.getMetaDataDialect());
					reader.readDatabaseSchema(db, settings.getDefaultCatalogName(), settings.getDefaultSchemaName(), new ProgressListener(monitor));
				} catch (UnsupportedOperationException he) {
					throw new HibernateException(he);
				} catch (Exception he) {
					throw new HibernateException(he.getMessage(), he.getCause());
				}
				return db;
			}
		});
	}
	
	private class ProgressListener implements IProgressListener {
		private IProgressMonitor progressMonitor;
		public ProgressListener(IProgressMonitor monitor) {
			progressMonitor = monitor;
		}
		@Override
		public void startSubTask(String name) {
			progressMonitor.subTask(name);
		}
		
	}
	
}
