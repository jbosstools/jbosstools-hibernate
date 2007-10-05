package org.jboss.tools.hibernate.ui.view.views;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Visitor;
import org.dom4j.VisitorSupport;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.util.StringHelper;
import org.hibernate.util.XMLHelper;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;
import org.xml.sax.InputSource;

public class OpenFileActionUtils {
	private static XMLHelper helper = new XMLHelper();

	public static void openEditor(IWorkbenchPage page, IResource resource) throws PartInitException {
        IDE.openEditor(page, (IFile) resource);
	}

	public static IJavaProject findJavaProject(ConsoleConfiguration consoleConfiguration) {
		IJavaProject proj = null;
		if (consoleConfiguration != null) {
			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType( "org.hibernate.eclipse.launch.ConsoleConfigurationLaunchConfigurationType" );
			ILaunchConfiguration[] launchConfigurations;
			try {
				launchConfigurations = launchManager.getLaunchConfigurations( launchConfigurationType );
				for (int i = 0; i < launchConfigurations.length; i++) { // can't believe there is no look up by name API
					ILaunchConfiguration launchConfiguration = launchConfigurations[i];
					if(launchConfiguration.getName().equals(consoleConfiguration.getName())) {
						proj = ProjectUtils.findJavaProject(launchConfiguration.getAttribute(IConsoleConfigurationLaunchConstants.PROJECT_NAME, ""));
					}
				}								
			} catch (CoreException e1) {
				ViewPlugin.getDefault().logError("Can't find java project.", e1);
			}
		}
		return proj;
	}

	private static boolean elementInResource(ConsoleConfiguration consoleConfiguration, IResource resource, Object element) {
		if (element instanceof RootClass) {
			return classInResource(consoleConfiguration, resource, (RootClass)element);
		} else if (element instanceof Table) {
			return tableInResource(consoleConfiguration, resource, (Table)element);
		} else {
			return false;
		}
	}

	public static boolean classInResource(ConsoleConfiguration consoleConfiguration, IResource resource, RootClass rootClass) {
		Document doc = getDocument(consoleConfiguration, resource.getLocation().toFile());
		Element hmNode = doc.getRootElement();

		Iterator rootChildren = hmNode.elementIterator();
		while ( rootChildren.hasNext() ) {
			Element element = (Element) rootChildren.next();
			String elementName = element.getName();

			if ( "class".equals( elementName ) ) {
				Attribute classAttr = element.attribute( "name" );
				if (classAttr != null) {
					if (classAttr.getValue().equals(rootClass.getClassName())) {
						return true;
					} else {
						Attribute packNode = hmNode.attribute( "package" );
						String packageName = null;
						if ( packNode != null ) {
							packageName = packNode.getValue();
							String className = packageName + "." + classAttr.getValue();
							if (className.equals(rootClass.getClassName())) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean tableInResource(ConsoleConfiguration consoleConfiguration, IResource resource, Table table) {
		Document doc = getDocument(consoleConfiguration, resource.getLocation().toFile());

		if (getElements(doc, "table", HibernateUtils.getTableName(table)).hasNext()) {
			return true;
		}
		
		Iterator classes = getElements(doc, "class");
		while (classes.hasNext()) {
			Element element = (Element) classes.next();
			Attribute classNameAttr = element.attribute( "name" );
			String physicalTableName = consoleConfiguration.getConfiguration().getNamingStrategy().classToTableName(classNameAttr.getValue());
			if (table.getName().equals(physicalTableName)) {
				return true;
			}
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

		private boolean attrIsCorrect(Element element, String attrName, String attrValue) {
			Attribute attr = element.attribute(attrName);
			if (attr != null && attrValue.equals(attr.getValue())) {
				return attrValue.equals(attr.getValue());
			}
			return false;
		}

		public Iterator iterator() {
			return ret.iterator();
		}
	}

	public static Document getDocument(ConsoleConfiguration consoleConfiguration, java.io.File configXMLFile) {
		Document doc = null;
		if (consoleConfiguration != null && configXMLFile != null) {
			InputStream stream = null;
			try {
				stream = new FileInputStream( configXMLFile );
			} catch (FileNotFoundException e) {
				ViewPlugin.getDefault().logError("Configuration file not found", e);
			}
			try {
				List errors = new ArrayList();
				doc = helper.createSAXReader( configXMLFile.getPath(), errors, consoleConfiguration.getConfiguration().getEntityResolver() )
						.read( new InputSource( stream ) );
				if ( errors.size() != 0 ) {
	    			ViewPlugin.getDefault().logError("invalid configuration");
				}
			}
			catch (DocumentException e) {
				ViewPlugin.getDefault().logError("Could not parse configuration", e);
			}
			finally {
				try {
					stream.close();
				}
				catch (IOException ioe) {
	    			ViewPlugin.getDefault().logError("could not close input stream for", ioe);
				}
			}
		}
		return doc;
	}

	public static IResource getResource(ConsoleConfiguration consoleConfiguration, IJavaProject proj, Document doc, java.io.File configXMLFile, Object element) {
    	IResource resource = null;
    	if (consoleConfiguration != null && proj != null && doc != null) {
        	Element sfNode = doc.getRootElement().element( "session-factory" );
    		Iterator elements = sfNode.elementIterator();
    		while ( elements.hasNext() ) {
    			Element subelement = (Element) elements.next();
    			String subelementName = subelement.getName();
    			if ( "mapping".equals( subelementName ) ) {
    				Attribute file = subelement.attribute( "resource" );
    				if (file != null) {
    					resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(configXMLFile.getParent()).append(file.getValue()));
    					if (elementInResource(consoleConfiguration, resource, element)) return resource;
    				}
    			}
    		}
    		java.io.File[] files = consoleConfiguration.getPreferences().getMappingFiles();
    		for (int i = 0; i < files.length; i++) {
    			java.io.File file = files[i];
				if (file != null) {
					resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(file.getPath()));
					if (OpenFileActionUtils.elementInResource(consoleConfiguration, resource, element)) return resource;
				}
			}
    	}
    	return null;
	}
}
