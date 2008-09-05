package org.hibernate.eclipse.console.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;

/**
 * A visitor class that will make a "best guess" on which files the user
 * want for the properties and config file.
 * 
 * @author max
 * 
 */
public class BestGuessConsoleConfigurationVisitor implements IResourceProxyVisitor {

	private IPath propertyFile;
	private IPath configFile;
	private IPath persistencexml;
	private IJavaProject javaProject;
	private List<IPath> classpath = new ArrayList<IPath>();
	private List<IPath> mappings = new ArrayList<IPath>();

	public IPath getPropertyFile() {
		return propertyFile;
	}

	public IPath getConfigFile() {
		return configFile;
	}

	public IPath getPersistencexml() {
		return persistencexml;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public List<IPath> getClasspath() {
		return classpath;
	}

	public List<IPath> getMappings() {
		return mappings;
	}
	
	public boolean visit(IResourceProxy proxy) throws CoreException {
		// System.out.println("visit: " + proxy.getName() );
		IPath fullPath = proxy.requestFullPath();
		if (proxy.getType() == IResource.FILE) {
			if ("hibernate.properties".equals(proxy.getName())) { //$NON-NLS-1$
				propertyFile = fullPath;
				return false;
			}

			if ("hibernate.cfg.xml".equals(proxy.getName())) { //$NON-NLS-1$
				configFile = fullPath;
				mappings.clear(); // we prefer af cfg.xml over mappings
				return false;
			}

			if ("persistence.xml".equals(proxy.getName())) { //$NON-NLS-1$
				if (javaProject != null
						&& javaProject.isOnClasspath(proxy
								.requestResource())) {
					persistencexml = fullPath;
					mappings.clear();
					return false;
				}
			}

			// only add mappings if we don't have a config file.
			if ((configFile == null || persistencexml == null)
					&& proxy.getName().endsWith(".hbm.xml")) { //$NON-NLS-1$
				mappings.add(fullPath);
				return false;
			}
		} else if (proxy.getType() == IResource.FOLDER) {
			if (javaProject != null) {
				if (javaProject.getOutputLocation().isPrefixOf(fullPath)) {
					// classpath.add(fullPath);
					return false; // skip output locations
				}
			}
		}
		return true;
	}

	public void setJavaProject(IJavaProject project) {
		javaProject = project;		
	}
}