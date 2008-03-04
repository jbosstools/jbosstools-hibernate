package org.hibernate.eclipse.console.actions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.util.StringHelper;
import org.hibernate.util.XMLHelper;
import org.xml.sax.InputSource;

public class OpenFileActionUtils {
	private static XMLHelper helper = new XMLHelper();
	
	private static final String HIBERNATE_TAG_CLASS = "class";
	private static final String HIBERNATE_TAG_TABLE = "table"; 
	private static final String HIBERNATE_TAG_SUBCLASS = "subclass";
	private static final String HIBERNATE_TAG_JOINED_SUBCLASS = "joined-subclass";
	private static final String HIBERNATE_TAG_UNION_SUBCLASS = "union-subclass";
	private static final String HIBERNATE_TAG_NAME = "name"; 
	private static final String HIBERNATE_TAG_ENTITY_NAME = "entity-name";
	private static final String HIBERNATE_TAG_SESSION_FACTORY = "session-factory";
	private static final String HIBERNATE_TAG_MAPPING = "mapping";
	private static final String HIBERNATE_TAG_RESOURCE = "resource";
	private static final String HIBERNATE_TAG_CATALOG = "catalog";
	private static final String HIBERNATE_TAG_SCHEMA = "schema";

	public static IEditorPart openEditor(IWorkbenchPage page, IResource resource) throws PartInitException {
        return IDE.openEditor(page, (IFile) resource);
	}
	

	public static boolean rootClassHasAnnotations(ConsoleConfiguration consoleConfiguration, java.io.File configXMLFile, PersistentClass rootClass) {
		if (configXMLFile == null) return true;
		Document doc = getDocument(consoleConfiguration, configXMLFile);
		return getElements(doc, HIBERNATE_TAG_MAPPING, HIBERNATE_TAG_CLASS, getPersistentClassName(rootClass)).hasNext();
	}
	
	static String getPersistentClassName(PersistentClass rootClass) {
		if (rootClass == null) {
			return "";
		} else { 
			return rootClass.getEntityName() != null ? rootClass.getEntityName() : rootClass.getClassName();
		}
	}
	
	private static String getTableName(String catalog, String schema, String name) {
		return (catalog != null ? catalog + "." : "") + (schema != null ? schema + "." : "") + name;
	}

	private static String getTableName(Table table) {
		return getTableName(table.getCatalog(), table.getSchema(), table.getName());
	}


	private static boolean elementInResource(ConsoleConfiguration consoleConfiguration, IResource resource, Object element) {
		if (element instanceof RootClass) {
			return rootClassInResource(consoleConfiguration, resource, (RootClass)element);
		} else if (element instanceof Subclass) {
			return subclassInResource(consoleConfiguration, resource, (Subclass)element);
		} else if (element instanceof Table) {
			return tableInResource(consoleConfiguration, resource, (Table)element);
		} else {
			return false;
		}
	}

	// TODO: this is *extremely* inefficient - no need to scan the whole tree again and again.
	private static boolean rootClassInResource(ConsoleConfiguration consoleConfiguration, IResource resource, RootClass persistentClass) {
		Document doc = getDocument(consoleConfiguration, resource.getLocation().toFile());
		return getElements(doc, OpenFileActionUtils.HIBERNATE_TAG_CLASS, HIBERNATE_TAG_NAME, StringHelper.unqualify(getPersistentClassName(persistentClass))).hasNext() ||
				getElements(doc, OpenFileActionUtils.HIBERNATE_TAG_CLASS, HIBERNATE_TAG_NAME, getPersistentClassName(persistentClass)).hasNext() ||
				getElements(doc, OpenFileActionUtils.HIBERNATE_TAG_CLASS, HIBERNATE_TAG_ENTITY_NAME, StringHelper.unqualify(getPersistentClassName(persistentClass))).hasNext() ||
				getElements(doc, OpenFileActionUtils.HIBERNATE_TAG_CLASS, HIBERNATE_TAG_ENTITY_NAME, getPersistentClassName(persistentClass)).hasNext();
	}

	// TODO: this is *extremely* inefficient - no need to scan the whole tree again and again.
	private static boolean subclassInResource(ConsoleConfiguration consoleConfiguration, IResource resource, Subclass persistentClass) {
		Document doc = getDocument(consoleConfiguration, resource.getLocation().toFile());
		return getElements(doc, HIBERNATE_TAG_SUBCLASS, HIBERNATE_TAG_NAME, StringHelper.unqualify(getPersistentClassName(persistentClass))).hasNext() ||
				getElements(doc, HIBERNATE_TAG_SUBCLASS, HIBERNATE_TAG_NAME, getPersistentClassName(persistentClass)).hasNext() ||
				getElements(doc, HIBERNATE_TAG_SUBCLASS, HIBERNATE_TAG_ENTITY_NAME, StringHelper.unqualify(getPersistentClassName(persistentClass))).hasNext() ||
				getElements(doc, HIBERNATE_TAG_SUBCLASS, HIBERNATE_TAG_ENTITY_NAME, getPersistentClassName(persistentClass)).hasNext() ||

				getElements(doc, HIBERNATE_TAG_JOINED_SUBCLASS, HIBERNATE_TAG_NAME, StringHelper.unqualify(getPersistentClassName(persistentClass))).hasNext() ||
				getElements(doc, HIBERNATE_TAG_JOINED_SUBCLASS, HIBERNATE_TAG_NAME, getPersistentClassName(persistentClass)).hasNext() ||
				getElements(doc, HIBERNATE_TAG_JOINED_SUBCLASS, HIBERNATE_TAG_ENTITY_NAME, StringHelper.unqualify(getPersistentClassName(persistentClass))).hasNext() ||
			    getElements(doc, HIBERNATE_TAG_JOINED_SUBCLASS, HIBERNATE_TAG_ENTITY_NAME, getPersistentClassName(persistentClass)).hasNext() ||

				getElements(doc, HIBERNATE_TAG_UNION_SUBCLASS, HIBERNATE_TAG_NAME, StringHelper.unqualify(getPersistentClassName(persistentClass))).hasNext() ||
				getElements(doc, HIBERNATE_TAG_UNION_SUBCLASS, HIBERNATE_TAG_NAME, getPersistentClassName(persistentClass)).hasNext() ||
				getElements(doc, HIBERNATE_TAG_UNION_SUBCLASS, HIBERNATE_TAG_ENTITY_NAME, StringHelper.unqualify(getPersistentClassName(persistentClass))).hasNext() ||
				getElements(doc, HIBERNATE_TAG_UNION_SUBCLASS, HIBERNATE_TAG_ENTITY_NAME, getPersistentClassName(persistentClass)).hasNext();
	}

	private static boolean tableInResource(ConsoleConfiguration consoleConfiguration, IResource resource, Table table) {
		Document doc = getDocument(consoleConfiguration, resource.getLocation().toFile());

		Iterator classes = getElements(doc, OpenFileActionUtils.HIBERNATE_TAG_CLASS);
		while (classes.hasNext()) {
			Element element = (Element) classes.next();

			Attribute tableAttr = element.attribute( HIBERNATE_TAG_TABLE );
			if (tableAttr != null) {
				Attribute catalogAttr = element.attribute( HIBERNATE_TAG_CATALOG );
				if (catalogAttr == null) catalogAttr = doc.getRootElement().attribute(HIBERNATE_TAG_CATALOG);
				Attribute schemaAttr = element.attribute( HIBERNATE_TAG_SCHEMA );
				if (schemaAttr == null) schemaAttr = doc.getRootElement().attribute(HIBERNATE_TAG_SCHEMA);
				if (
						getTableName(
							(catalogAttr != null ? catalogAttr.getValue() : null), 
							(schemaAttr != null ? schemaAttr.getValue() : null), 
							tableAttr.getValue()
						).equals(getTableName(table))
					) {
					return true;
				}

				
			}

			Attribute classNameAttr = element.attribute( HIBERNATE_TAG_NAME );
			if (classNameAttr == null) classNameAttr = element.attribute( HIBERNATE_TAG_ENTITY_NAME);
			if (classNameAttr != null) {
				String physicalTableName = consoleConfiguration.getConfiguration().getNamingStrategy().classToTableName(classNameAttr.getValue());
				if (table.getName().equals(physicalTableName)) {
					return true;
				}
			}
		}

		if (getElements(doc, HIBERNATE_TAG_TABLE, table.getName()).hasNext()) {
			return true;
		}

		return false;
	}

	private static Iterator getElements(Document doc, String elementName) {
		return getElements(doc, elementName, null, null);
	}

	private static Iterator getElements(Document doc, String attrName, String attrValue) {
		return getElements(doc, null, attrName, attrValue);
	}

	private static Iterator getElements(Document doc, String elementName, String attrName, String attrValue) {
		LVS visitor = new LVS(elementName, attrName, attrValue);
		doc.accept( visitor );
		return visitor.iterator();
	}

	static class LVS extends VisitorSupport {
		private String nodeName; 
		private String attrName; 
		private String attrValue;
		private List ret = new ArrayList();
		
		public LVS(String nodeName, String attrName, String attrValue) {
			super();
			this.nodeName = nodeName;
			this.attrName = attrName;
			this.attrValue = attrValue;
		}

		public void visit(Element element) {
			if (nodeName == null) {
				if (attrName != null && attrValue != null) {
					if (attrIsCorrect(element, attrName, attrValue)) {
			        	ret.add(element);  
					}
				}
			} else {
				if (nodeName.equals(element.getName())) {
					if (attrName != null) {
						if (attrIsCorrect(element, attrName, attrValue)) {
				        	ret.add(element);  
						}
					} else {
			        	ret.add(element);  
					}
				}
			}
			
		}

		public Iterator iterator() {
			return ret.iterator();
		}
	}

	private static boolean attrIsCorrect(Element element, String attrName, String attrValue) {
		Attribute attr = element.attribute(attrName);
		if (attr != null && attrValue.equals(attr.getValue())) {
			return attrValue.equals(attr.getValue());
		}
		return false;
	}

	public static Document getDocument(ConsoleConfiguration consoleConfiguration, java.io.File configXMLFile) {
		Document doc = null;
		if (consoleConfiguration != null && configXMLFile != null) {
			InputStream stream = null;
			try {
				stream = new FileInputStream( configXMLFile );
			} catch (FileNotFoundException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("Configuration file not found", e);
			}
			try {
				List errors = new ArrayList();
				doc = helper.createSAXReader( configXMLFile.getPath(), errors, consoleConfiguration.getConfiguration().getEntityResolver() )
						.read( new InputSource( stream ) );
				if ( errors.size() != 0 ) {
	    			HibernateConsolePlugin.getDefault().logErrorMessage("invalid configuration", (Throwable)null);
				}
			}
			catch (DocumentException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("Could not parse configuration", e);
			}
			finally {
				try {
					stream.close();
				}
				catch (IOException ioe) {
	    			HibernateConsolePlugin.getDefault().logErrorMessage("could not close input stream for", ioe);
				}
			}
		}
		return doc;
	}

	public static IResource getResource(ConsoleConfiguration consoleConfiguration, IJavaProject proj, java.io.File configXMLFile, Object element) {
		Document doc = getDocument(consoleConfiguration, configXMLFile);
    	IResource resource = null;
    	if (consoleConfiguration != null && proj != null && doc != null) {
        	Element sfNode = doc.getRootElement().element( HIBERNATE_TAG_SESSION_FACTORY );
    		Iterator elements = sfNode.elements(HIBERNATE_TAG_MAPPING).iterator();
    		while ( elements.hasNext() ) {
    			Element subelement = (Element) elements.next();
				Attribute file = subelement.attribute( HIBERNATE_TAG_RESOURCE );
				if (file != null) {
					IPackageFragmentRoot[] packageFragmentRoots;
					try {
						packageFragmentRoots = proj.getAllPackageFragmentRoots();						
						for (int i = 0; i < packageFragmentRoots.length && resource == null; i++) {
							//search in source folders.
							if (packageFragmentRoots[i].getClass() == PackageFragmentRoot.class) {
								IPackageFragmentRoot packageFragmentRoot = packageFragmentRoots[i];
								IPath path = packageFragmentRoot.getPath().append(file.getValue());
								resource = ResourcesPlugin.getWorkspace().getRoot().getFile(path);							
							}
						}
						if (resource != null &&
								elementInResource(consoleConfiguration, resource, element)) return resource;
					} catch (JavaModelException e) {
						HibernateConsolePlugin.getDefault().logErrorMessage("Problems while getting project package fragment roots", e);						
					}
				}
    		}

        	java.io.File[] files = consoleConfiguration.getPreferences().getMappingFiles();
    		for (int i = 0; i < files.length; i++) {
    			java.io.File file = files[i];
				if (file != null) {
					resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(file.getPath()));
					if (resource != null &&
							OpenFileActionUtils.elementInResource(consoleConfiguration, resource, element)) return resource;
				}
			}
    	}
    	return null;
	}

	
}
