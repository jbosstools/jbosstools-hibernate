package org.hibernate.eclipse.console.utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.ejb.HibernatePersistence;
import org.hibernate.ejb.packaging.JarVisitor;
import org.hibernate.ejb.packaging.NamedInputStream;
import org.hibernate.ejb.packaging.PersistenceMetadata;
import org.hibernate.ejb.packaging.PersistenceXmlLoader;
import org.hibernate.util.CollectionHelper;

/**
 * Most functions in this utility class are copy of 
 * org.hibernate.ejb.Ejb3Configuration private functions.
 * These functions are responsible for bypass all ejb3 configuration files.
 * 
 * @author Vitali Yemialyanchyk
 */
public class OpenMappingUtilsEjb3 {
	
	public static final String META_INF_PERS_XML = "META-INF/persistence.xml"; //$NON-NLS-1$
	public static final String META_INF_ORM_XML = "META-INF/orm.xml"; //$NON-NLS-1$

	private OpenMappingUtilsEjb3() {}
	
	/**
	 * Collect list of paths to ejb3 resource configuration files.
	 * This function code based on code from Ejb3Configuration.
	 * @see Ejb3Configuration configure(String persistenceUnitName, Map integration)
	 * 
	 * @param consoleConfiguration
	 * @return
	 */
	public static List<String> enumDocuments(ConsoleConfiguration consoleConfiguration) {
		String persistenceUnitName = consoleConfiguration.getPreferences().getPersistenceUnitName();
		Enumeration<URL> xmls = null;
		try {
			xmls = Thread.currentThread().getContextClassLoader().getResources(META_INF_PERS_XML);
		} catch (IOException e) {
			//ignore
		}
		if (xmls == null || !xmls.hasMoreElements()) {
	    	return null;
		}
		final String IMPLEMENTATION_NAME = HibernatePersistence.class.getName();
		List<String> res = null;
		while (xmls.hasMoreElements() && res == null) {
			URL url = xmls.nextElement();
			List<PersistenceMetadata> metadataFiles = null;
			try {
				metadataFiles = PersistenceXmlLoader.deploy(
						url, CollectionHelper.EMPTY_MAP,
						consoleConfiguration.getConfiguration().getEntityResolver(),
						PersistenceUnitTransactionType.RESOURCE_LOCAL);
			} catch (Exception e1) {
				//ignore
			}
			if (metadataFiles == null) {
				continue;
			}
			boolean stopErrorFlag = false;
			for (PersistenceMetadata metadata : metadataFiles) {
				boolean tmp = metadata.getProvider() == null;
				tmp = tmp || IMPLEMENTATION_NAME.equalsIgnoreCase(metadata.getProvider());
				if (!tmp) {
					continue;
				}
				//correct provider
				//lazy compute the visitor if possible to avoid useless exceptions if an unexpected state happens
				JarVisitor visitor = null;
				URL visitorJarURL = null;
				if (metadata.getName() == null) {
					visitor = getMainJarVisitor(url, metadata, CollectionHelper.EMPTY_MAP);
					visitorJarURL = JarVisitor.getJarURLFromURLEntry(url, "/" + META_INF_PERS_XML); //$NON-NLS-1$
					metadata.setName(visitor.getUnqualifiedJarName());
				}
				if (persistenceUnitName == null && xmls.hasMoreElements()) {
					//throw new PersistenceException( "No name provided and several persistence units found" );
					stopErrorFlag = true;
			    	break;
				}
				else if (persistenceUnitName == null || metadata.getName().equals(persistenceUnitName)) {
					if (visitor == null) {
						visitor = getMainJarVisitor(url, metadata, CollectionHelper.EMPTY_MAP);
						visitorJarURL = JarVisitor.getJarURLFromURLEntry(url, "/" + META_INF_PERS_XML); //$NON-NLS-1$
					}
					try {
						addMetadataFromVisitor(visitor, visitorJarURL.getPath(), metadata);
						/** /
						JarVisitor.Filter[] otherXmlFilter = getFilters(metadata, CollectionHelper.EMPTY_MAP, false);
						for (String jarFile : metadata.getJarFiles()) {
							// TODO: test this code
							//vit//visitor = JarVisitor.getVisitor(jarFile, otherXmlFilter);
							//vit//addMetadataFromVisitor(visitor, metadata);
						}
						/**/
					} catch (IOException e) {
						//ignore
					}
					res = new ArrayList<String>();
					Iterator<NamedInputStream> it = metadata.getHbmfiles().iterator();
					while (it.hasNext()) {
						NamedInputStream nis = it.next();
						res.add(nis.getName());
						try {
							nis.getStream().close();
						}
						catch (IOException ioe) {
			    			HibernateConsolePlugin.getDefault().logErrorMessage("could not close input stream for", ioe);	//$NON-NLS-1$
						}
					}
					break;
				}
			}
			if (stopErrorFlag) {
				break;
			}
		}
    	return res;
	}
	
	/**
	 * This function code based on code from Ejb3Configuration.
	 * @see JarVisitor Ejb3Configuration.getMainJarVisitor(URL url, PersistenceMetadata metadata, Map integration)
	 * 
	 * @param url
	 * @param metadata
	 * @param integration
	 * @return
	 */
	public static JarVisitor getMainJarVisitor(URL url, PersistenceMetadata metadata, Map integration) {
		URL jarURL = JarVisitor.getJarURLFromURLEntry(url, "/" + META_INF_PERS_XML); //$NON-NLS-1$
		JarVisitor.Filter[] persistenceXmlFilter = getFilters(metadata, integration, metadata.getExcludeUnlistedClasses());
		JarVisitor visitor = JarVisitor.getVisitor(jarURL, persistenceXmlFilter);
		return visitor;
	}

	/**
	 * This function code based on code from Ejb3Configuration.
	 * @see JarVisitor Ejb3Configuration.addMetadataFromVisitor(JarVisitor visitor, PersistenceMetadata metadata) throws IOException
	 * 
	 * @param visitor
	 * @param metadata
	 * @throws IOException
	 */
	public static void addMetadataFromVisitor(JarVisitor visitor, String addPath, PersistenceMetadata metadata) throws IOException {
		Set[] entries = visitor.getMatchingEntries();
		JarVisitor.Filter[] filters = visitor.getFilters();
		int size = filters.length;
		List<String> classes = metadata.getClasses();
		List<String> packages = metadata.getPackages();
		List<NamedInputStream> hbmFiles = metadata.getHbmfiles();
		for (int index = 0; index < size; index++) {
			Iterator homogeneousEntry = entries[index].iterator();
			while (homogeneousEntry.hasNext()) {
				JarVisitor.Entry entry = (JarVisitor.Entry) homogeneousEntry.next();
				if (filters[index] instanceof JarVisitor.ClassFilter) {
					//TODO only add entry if there is annotations (Javassist)
					classes.add(entry.getName());
				}
				else if (filters[index] instanceof JarVisitor.PackageFilter) {
					packages.add(entry.getName());
				}
				else if (filters[index] instanceof JarVisitor.FileFilter) {
					hbmFiles.add(new NamedInputStream(addPath + "/" + entry.getName(),  //$NON-NLS-1$
							entry.getInputStream()));
					metadata.getMappingFiles().remove(entry.getName());
				}
			}
		}
	}
	
	/**
	 * This function code based on code from Ejb3Configuration.
	 * @see boolean[] Ejb3Configuration.getDetectedArtifacts(Properties properties, Map overridenProperties, boolean excludeIfNotOverriden)
	 * 
	 * @param properties
	 * @param overridenProperties
	 * @param excludeIfNotOverriden
	 * @return
	 */
	public static boolean[] getDetectedArtifacts(Properties properties, Map overridenProperties, boolean excludeIfNotOverriden) {
		//result[0] - detect classes
		//result[1] - detect hbm
		boolean[] result = { false, false };
		String detect = overridenProperties != null ?
				(String) overridenProperties.get(HibernatePersistence.AUTODETECTION) : null;
		detect = detect == null ?
				properties.getProperty( HibernatePersistence.AUTODETECTION) : detect;
		if (detect == null && excludeIfNotOverriden) {
			//not overriden through HibernatePersistence.AUTODETECTION so we comply with the spec excludeUnlistedClasses
			return result;
		}
		else if (detect == null){
			detect = "class,hbm"; //$NON-NLS-1$
		}
		StringTokenizer st = new StringTokenizer(detect, ", ", false); //$NON-NLS-1$
		while (st.hasMoreElements()) {
			String element = (String)st.nextElement();
			if ("class".equalsIgnoreCase(element)) { //$NON-NLS-1$
				result[0] = true;
			}
			if ("hbm".equalsIgnoreCase(element)) { //$NON-NLS-1$
				result[1] = true;
			}
		}
		return result;
	}
	
	/**
	 * This function code based on code from Ejb3Configuration.
	 * @see JarVisitor.Filter[] Ejb3Configuration.getFilters(PersistenceMetadata metadata, Map overridenProperties, boolean excludeIfNotOverriden)
	 * 
	 * @param metadata
	 * @param overridenProperties
	 * @param excludeIfNotOverriden
	 * @return
	 */
	public static JarVisitor.Filter[] getFilters(PersistenceMetadata metadata, Map overridenProperties, boolean excludeIfNotOverriden) {
		Properties properties = metadata.getProps();
		final List<String> mappingFiles = metadata.getMappingFiles();
		boolean[] result = getDetectedArtifacts(properties, overridenProperties, excludeIfNotOverriden);

		int size = ( result[0] ? 2 : 0 ) + 1; //class involves classes and packages, xml files are always involved because of orm.xml
		JarVisitor.Filter[] filters = new JarVisitor.Filter[size];
		if (result[0]) {
			filters[0] = new JarVisitor.PackageFilter(false, null) {
				public boolean accept(String javaElementName) {
					return true;
				}
			};
			filters[1] = new JarVisitor.ClassFilter(
					false, new Class[]{
					Entity.class,
					MappedSuperclass.class,
					Embeddable.class}
			) {
				public boolean accept(String javaElementName) {
					return true;
				}
			};
		}
		if (result[1]) {
			filters[size - 1] = new JarVisitor.FileFilter(true) {
				public boolean accept(String javaElementName) {
					return javaElementName.endsWith("hbm.xml") //$NON-NLS-1$
							|| javaElementName.endsWith(META_INF_ORM_XML)
							|| mappingFiles.contains(javaElementName);
				}
			};
		}
		else {
			filters[size - 1] = new JarVisitor.FileFilter(true) {
				public boolean accept(String javaElementName) {
					return javaElementName.endsWith(META_INF_ORM_XML)
							|| mappingFiles.contains(javaElementName);
				}
			};
		}
		return filters;
	}
	
}
