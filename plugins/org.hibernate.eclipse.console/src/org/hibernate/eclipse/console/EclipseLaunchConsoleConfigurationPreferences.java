package org.hibernate.eclipse.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jpt.core.JpaDataSource;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.osgi.util.NLS;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.utils.ClassLoaderHelper;
import org.hibernate.eclipse.console.utils.DriverClassHelpers;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.w3c.dom.Element;

public class EclipseLaunchConsoleConfigurationPreferences implements ConsoleConfigurationPreferences {

	private final ILaunchConfiguration launchConfiguration;

	public EclipseLaunchConsoleConfigurationPreferences(ILaunchConfiguration configuration) {
		this.launchConfiguration = configuration;
	}


	private File strToFile(String epath) {
		if(epath==null) return null;
		IPath path = new Path(epath);
		return pathToFile( path );
	}

	private File pathToFile(IPath path) {
		if(path==null) return null;
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);

		if(resource == null) {
			File file = new File(path.toOSString());
			if (file.exists()) {
				if (file.isFile()) {
					return file;
				}
			}
		}
		return pathToFile(path.toString(), resource);
	}

	private File pathToFile(String path, IResource resource) {
		if(resource != null) {
			IPath rawLocation = resource.getRawLocation();
			if(rawLocation !=null) {
				return rawLocation.toFile();
			}
		}
		String out = NLS.bind(HibernateConsoleMessages.EclipseLaunchConsoleConfigurationPreferences_could_not_resolve_to_file, path);
		throw new HibernateConsoleRuntimeException(out);
	}


	protected String getAttribute( String attr, String defaultValue ) {
		try {
			String value = launchConfiguration.getAttribute( attr, defaultValue );
			return value;
		}
		catch (CoreException e) {
			throw new HibernateConsoleRuntimeException(e);
		}
	}

	public File getConfigXMLFile() {
		String file = getAttribute( IConsoleConfigurationLaunchConstants.CFG_XML_FILE, null );
		return strToFile( file );
	}

	public ConfigurationMode getConfigurationMode() {
		return ConfigurationMode.parse( getAttribute( IConsoleConfigurationLaunchConstants.CONFIGURATION_FACTORY, "" ) );		 //$NON-NLS-1$
	}

	public URL[] getCustomClassPathURLS() {
		try {
			String[] classpath = ClassLoaderHelper.getClasspath( launchConfiguration );
			URL[] cp = new URL[classpath.length];
			for (int i = 0; i < classpath.length; i++) {
				String str = classpath[i];
				cp[i] = new File(str).toURL();
			}
			return cp;
		}
		catch (CoreException e) {
			throw new HibernateConsoleRuntimeException(HibernateConsoleMessages.EclipseLaunchConsoleConfigurationPreferences_could_not_compute_classpath, e);
		}
		catch (MalformedURLException e) {
			throw new HibernateConsoleRuntimeException(HibernateConsoleMessages.EclipseLaunchConsoleConfigurationPreferences_could_not_compute_classpath, e);
		}
	}


	public String getEntityResolverName() {
		return getAttribute( IConsoleConfigurationLaunchConstants.ENTITY_RESOLVER, null );
	}

	@SuppressWarnings("unchecked")
	public File[] getMappingFiles() {
		try {
			List<String> mappings = launchConfiguration.getAttribute( IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, Collections.EMPTY_LIST );
			File[] result = new File[mappings.size()];
			int i = 0;
			for (String element : mappings) {
				result[i++] = strToFile( element );
			}
			return result;				
		}
		catch (CoreException e) {
			throw new HibernateConsoleRuntimeException(e);
		}
	}

	public String getName() {
		return launchConfiguration.getName();
	}

	public String getNamingStrategy() {
		return getAttribute( IConsoleConfigurationLaunchConstants.NAMING_STRATEGY, null );
	}

	public String getConnectionProfileName() {
		if (Boolean.parseBoolean(getAttribute(IConsoleConfigurationLaunchConstants.USE_JPA_PROJECT_PROFILE, null))){
			String projName = getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, null);
			if (projName != null){
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
				if (project != null){
					JpaProject jpaProject = (JpaProject) project.getAdapter(JpaProject.class);
					if (jpaProject != null) {
						JpaDataSource ds = jpaProject.getDataSource();
						if (ds != null)
							return "".equals(ds.getConnectionProfileName()) ? null : ds.getConnectionProfileName();//$NON-NLS-1$
					}
				}
			}
		}
		return getAttribute(IConsoleConfigurationLaunchConstants.CONNECTION_PROFILE_NAME, null);
	}

	public String getPersistenceUnitName() {
		return getAttribute( IConsoleConfigurationLaunchConstants.PERSISTENCE_UNIT_NAME, null );
	}

	public Properties getProperties() {		
		File propFile = getPropertyFile();
		if(propFile==null) return null;
		Properties p = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(propFile);
			p.load(fis);
		}
		catch(IOException io) {
			throw new HibernateConsoleRuntimeException(NLS.bind(HibernateConsoleMessages.EclipseLaunchConsoleConfigurationPreferences_could_not_load_property_file, propFile), io);
		}
		finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return p;
	}

	public File getPropertyFile() {
		return strToFile(getAttribute( IConsoleConfigurationLaunchConstants.PROPERTY_FILE, null ));
	}

	public void readStateFrom(Element element) {
		throw new IllegalStateException(HibernateConsoleMessages.EclipseLaunchConsoleConfigurationPreferences_cannot_read_from_xml);
	}

	public void setName(String name) {
		throw new IllegalStateException(NLS.bind(HibernateConsoleMessages.EclipseLaunchConsoleConfigurationPreferences_cannot_be_renamed, getName()));
	}

	public void writeStateTo(Element node) {
		throw new IllegalStateException(HibernateConsoleMessages.EclipseLaunchConsoleConfigurationPreferences_cannot_write_to_xml);
	}
	
	public String getDialectName() {
		String dialect = getAttribute( IConsoleConfigurationLaunchConstants.DIALECT, null );
		// determine dialect when connection profile is used
		if (dialect == null && getConnectionProfileName() != null && getConnectionProfileName() != null) {
			IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(getConnectionProfileName());	
			if (profile == null) return null;
			String driver = profile.getProperties(profile.getProviderId()).getProperty("org.eclipse.datatools.connectivity.db.driverClass"); //$NON-NLS-1$
			dialect = new DriverClassHelpers().getDialect(driver);
		}
		return dialect;
	}

}
