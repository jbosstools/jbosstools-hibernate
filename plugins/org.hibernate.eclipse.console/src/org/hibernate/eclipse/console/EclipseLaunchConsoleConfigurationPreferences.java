package org.hibernate.eclipse.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.osgi.util.NLS;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.utils.ClassLoaderHelper;
import org.hibernate.eclipse.launch.ICodeGenerationLaunchConstants;
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

	public File[] getMappingFiles() {
		try {
			List mappings = launchConfiguration.getAttribute( IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, Collections.EMPTY_LIST );
			File[] result = new File[mappings.size()];
			int i = 0;
			Iterator iter = mappings.iterator();
			while ( iter.hasNext() ) {
				String element = (String) iter.next();
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

	public String getPersistenceUnitName() {
		return getAttribute( IConsoleConfigurationLaunchConstants.PERSISTENCE_UNIT_NAME, null );
	}

	public Properties getProperties() {
		File propFile = getPropertyFile();
		if(propFile==null) return null;
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(propFile) );
			return p;
		}
		catch(IOException io) {
			throw new HibernateConsoleRuntimeException(HibernateConsoleMessages.EclipseLaunchConsoleConfigurationPreferences_could_not_load_property_file + propFile, io);
		}
	}

	public File getPropertyFile() {
		return strToFile(getAttribute( IConsoleConfigurationLaunchConstants.PROPERTY_FILE, null ));
	}

	public void readStateFrom(Element element) {
		throw new IllegalStateException(HibernateConsoleMessages.EclipseLaunchConsoleConfigurationPreferences_cannot_read_from_xml);
	}

	public void setName(String name) {
		throw new IllegalStateException(getName() + HibernateConsoleMessages.EclipseLaunchConsoleConfigurationPreferences_cannot_be_renamed);
	}

	public void writeStateTo(Element node) {
		throw new IllegalStateException(HibernateConsoleMessages.EclipseLaunchConsoleConfigurationPreferences_cannot_write_to_xml);
	}






}
