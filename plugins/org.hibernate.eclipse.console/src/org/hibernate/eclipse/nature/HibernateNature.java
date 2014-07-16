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
package org.hibernate.eclipse.nature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.eclipse.builder.HibernateBuilder;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.properties.HibernatePropertiesConstants;
import org.hibernate.mapping.Table;
import org.osgi.service.prefs.Preferences;

public class HibernateNature implements IProjectNature {

	final public static String ID = "org.hibernate.eclipse.console.hibernateNature"; //$NON-NLS-1$

	private IProject project;

	public void configure() throws CoreException {
		//HibernateConsolePlugin.getDefault().log("Configuring " + project + " as a Hibernate project");

		IProjectDescription desc = project.getDescription();
		   ICommand[] commands = desc.getBuildSpec();
		   boolean found = false;

		   for (int i = 0; i < commands.length; ++i) {
		      if (commands[i].getBuilderName().equals(HibernateBuilder.BUILDER_ID) ) {
		         found = true;
		         break;
		      }
		   }
		   if (!found) {
		      //add builder to project
		      ICommand command = desc.newCommand();
		      command.setBuilderName(HibernateBuilder.BUILDER_ID);
		      List<ICommand> list = new ArrayList<ICommand>();
		      list.addAll(Arrays.asList(commands) );
		      list.add(command);
		      desc.setBuildSpec( list.toArray(new ICommand[]{}) );
		      project.setDescription(desc, new NullProgressMonitor() );
		   }
	}

	public void deconfigure() throws CoreException {
		//HibernateConsolePlugin.getDefault().log("Deconfiguring " + project + " as a Hibernate project");
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public ConsoleConfiguration getDefaultConsoleConfiguration() {
			String cfg = getDefaultConsoleConfigurationName();
			ConsoleConfiguration configuration = KnownConfigurations.getInstance().find(cfg);
			return configuration;
	}

	public String getDefaultConsoleConfigurationName() {
		IJavaProject prj = JavaCore.create(project);
		IScopeContext scope = new ProjectScope(prj.getProject() );

		Preferences node = scope.getNode(HibernatePropertiesConstants.HIBERNATE_CONSOLE_NODE);

		if(node!=null) {
			String cfg = node.get(HibernatePropertiesConstants.DEFAULT_CONFIGURATION, prj.getProject().getName() );
			return cfg;
		} else {
			return null;
		}
	}

	List<Table> tables = null;

	private ReadDatabaseMetaData job;

	public List<Table> getTables() {
		ConsoleConfiguration ccfg = getDefaultConsoleConfiguration();
		if(ccfg==null) return Collections.emptyList();

		if(tables!=null) {
			return tables;
		} else {
			if(job==null) {
				job = new ReadDatabaseMetaData(ccfg);
				job.setPriority(Job.DECORATE);
				job.schedule();
			} else if(job.getState()==Job.NONE) {
				job.schedule();
			}
			return Collections.emptyList();
		}
	}

	public class ReadDatabaseMetaData extends Job {

		private ConsoleConfiguration ccfg;

		public ReadDatabaseMetaData(ConsoleConfiguration ccfg) {
			super(HibernateConsoleMessages.HibernateNature_reading_database_metadata_for + getProject().getName() );
			this.ccfg = ccfg;
		}

		@SuppressWarnings("unchecked")
		protected IStatus run(IProgressMonitor monitor) {
			Configuration cfg = ccfg.buildWith(null, false);
			final JDBCMetaDataConfiguration jcfg = new JDBCMetaDataConfiguration();
			jcfg.setProperties(cfg.getProperties());
			monitor.beginTask(HibernateConsoleMessages.HibernateNature_reading_database_metadata, IProgressMonitor.UNKNOWN);
			try {
				ccfg.execute(new Command() {
					public Object execute() {
						jcfg.readFromJDBC();
						return null;
					}
				});


				List<Table> result = new ArrayList<Table>();
				Iterator<Table> tabs = jcfg.getTableMappings();

				while (tabs.hasNext() ) {
					Table table = tabs.next();
					monitor.subTask(table.getName() );
					result.add(table);
				}

				tables = result;
				monitor.done();
				return Status.OK_STATUS;
			} catch(Throwable t) {
				return new Status(IStatus.ERROR, HibernateConsolePlugin.ID, 1, HibernateConsoleMessages.HibernateNature_error_while_performing_background_reading_of_database_schema, t);
			}
		}

	}

	public List<Table> getMatchingTables(String tableName) {
		List<Table> result = new ArrayList<Table>();
		Iterator<Table> tableMappings = getTables().iterator();
		while (tableMappings.hasNext() ) {
			Table table = tableMappings.next();
			if(table.getName().toUpperCase().startsWith(tableName.toUpperCase()) ) {
				result.add(table);
			}
		}
		return result;
	}

	public Table getTable(TableIdentifier nearestTableName) {
		// TODO: can be made MUCH more efficient with proper indexing of the tables.
		// TODO: handle catalog/schema properly
		for (Table table : getTables()) {
			if(nearestTableName.getName().equals(table.getName())) {
				return table;
			}
		}
		return null;
	}

	/** return HibernateNature or null for a project **/
	public static HibernateNature getHibernateNature(IJavaProject project) {
		try {
			if(project!=null && project.getProject().isOpen() && project.getProject().hasNature(HibernateNature.ID)) {
				final HibernateNature nature = (HibernateNature) project.getProject().getNature(HibernateNature.ID);
				return nature;
			}
		}
		catch (CoreException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage( HibernateConsoleMessages.HibernateNature_exception_when_trying_to_locate_hibernate_nature, e );
		}
		return null;
	}

}
