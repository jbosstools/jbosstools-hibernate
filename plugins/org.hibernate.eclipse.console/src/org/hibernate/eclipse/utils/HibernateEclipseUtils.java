package org.hibernate.eclipse.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jpt.jpa.core.JpaPreferences;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public class HibernateEclipseUtils {

	public static final String JPT_JPA_CORE = "org.eclipse.jpt.jpa.core";  //$NON-NLS-1$

	public static boolean isJpaFacetInstalled(IProject project) {
		try {
			return FacetedProjectFramework.hasProjectFacet(project, JpaProject.FACET_ID);
		} catch (CoreException e) {
			HibernateConsolePlugin.getDefault().log(e);
			return false;
		}
	}
	
	private static final QualifiedName DATA_SOURCE_CONNECTION_PROFILE_NAME = 
			new QualifiedName(
					JPT_JPA_CORE, 		  
					"dataSource.connectionProfileName");  //$NON-NLS-1$ 
	
	private static boolean stringIsEmpty(String string) {
		if (string == null) {
			return true;
		}
		int len = string.length();
		if (len == 0) {
			return true;
		}
		return stringIsEmpty_(string.toCharArray(), len);
	}
	
	private static boolean stringIsEmpty_(char[] s, int len) {
		for (int i = len; i-- > 0; ) {
			if ( ! Character.isWhitespace(s[i])) {
				return false;
			}
		}
		return true;
	}
	
	public static String getConnectionProfileName(IProject project) {
		try {
			String connectionProfileName = project.getPersistentProperty(
					DATA_SOURCE_CONNECTION_PROFILE_NAME);
			// some old connection profile names were stored as empty strings instead of nulls :-(
			// convert them here
			return (stringIsEmpty(connectionProfileName)) ? null : connectionProfileName;
		} catch (CoreException ex) {
			HibernateConsolePlugin.getDefault().log(ex);
			return null;
		}
	}
	
	public static String getJpaPlatformID(IProject project) {
		return JpaPreferences.getJpaPlatformID(project);
	}
	
	private static final QualifiedName USER_OVERRIDE_DEFAULT_CATALOG = 
			new QualifiedName(
					JPT_JPA_CORE, 		
					"userOverrideDefaultCatalogName");  //$NON-NLS-1$
	public static String getUserOverrideDefaultCatalog(IProject project) {
		try {
			return project.getPersistentProperty(USER_OVERRIDE_DEFAULT_CATALOG);
		} catch (CoreException ex) {
			HibernateConsolePlugin.getDefault().log(ex);
			return null;
		}
	}
	
	private static final QualifiedName USER_OVERRIDE_DEFAULT_SCHEMA = 
			new QualifiedName(
					JPT_JPA_CORE, 
					"userOverrideDefaultSchemaName");  //$NON-NLS-1$
	public static String getUserOverrideDefaultSchema(IProject project) {
		try {
			return project.getPersistentProperty(USER_OVERRIDE_DEFAULT_SCHEMA);
		} catch (CoreException ex) {
			HibernateConsolePlugin.getDefault().log(ex);
			return null;
		}
	}

}
