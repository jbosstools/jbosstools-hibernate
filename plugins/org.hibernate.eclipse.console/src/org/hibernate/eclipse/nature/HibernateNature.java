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
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.eclipse.builder.HibernateBuilder;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.mapping.Table;
import org.osgi.service.prefs.Preferences;

public class HibernateNature implements IProjectNature {

	final public static String ID = "org.hibernate.eclipse.console.hibernateNature";
	
	private IProject project;

	public void configure() throws CoreException {
		HibernateConsolePlugin.getDefault().log("Configuring " + project + " as a Hibernate project");
		
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
		      ArrayList list = new ArrayList();
		      list.addAll(Arrays.asList(commands) );
		      list.add(command);
		      desc.setBuildSpec( (ICommand[])list.toArray(new ICommand[]{}) );
		      project.setDescription(desc, new NullProgressMonitor() );
		   }
	}

	public void deconfigure() throws CoreException {
		HibernateConsolePlugin.getDefault().log("Deconfiguring " + project + " as a Hibernate project");
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
		
		Preferences node = scope.getNode("org.hibernate.eclipse.console");
		
		if(node!=null) {
			String cfg = node.get("default.configuration", prj.getProject().getName() );
			return cfg;						
		} else {
			return null;
		}
	}
	
	List tables = null;

	private ReadDatabaseMetaData job;
	
	public List getTables() {
		ConsoleConfiguration ccfg = getDefaultConsoleConfiguration();
		if(ccfg==null) return Collections.EMPTY_LIST;
		
		if(tables!=null) {
			return tables;
		} else {
			if(job==null) {
				job = new ReadDatabaseMetaData(ccfg);
				job.setPriority(Job.DECORATE);
				job.schedule();
			}
			return Collections.EMPTY_LIST;
		}
	}
	
	public class ReadDatabaseMetaData extends Job {
		
		private ConsoleConfiguration ccfg;

		public ReadDatabaseMetaData(ConsoleConfiguration ccfg) {
			super("Reading database metadata for " + getProject().getName() );
			this.ccfg = ccfg;
		}
		
		protected IStatus run(IProgressMonitor monitor) {
			final JDBCMetaDataConfiguration jcfg = (JDBCMetaDataConfiguration) ccfg.buildWith(new JDBCMetaDataConfiguration(), false);
			monitor.beginTask("Reading database metadata", IProgressMonitor.UNKNOWN);
			try {
				ccfg.execute(new Command() {
					public Object execute() {
						jcfg.readFromJDBC();
						return null;
					}
				});
				
				
				List result = new ArrayList();
				Iterator tabs = jcfg.getTableMappings();
				
				while (tabs.hasNext() ) {
					Table table = (Table) tabs.next();
					monitor.subTask(table.getName() );
					result.add(table);
				}
				
				tables = result;
				monitor.done();
				return Status.OK_STATUS;
			} catch(Throwable t) {
				return new Status(Status.ERROR, HibernateConsolePlugin.ID, 1, "Error while performing background reading of database schema", t); 
			}
		}			

	}
}
