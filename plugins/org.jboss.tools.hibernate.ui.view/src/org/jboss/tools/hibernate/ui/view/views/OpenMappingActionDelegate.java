/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.view.views;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.eclipse.core.internal.resources.File;
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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.EclipseConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.hibernate.mapping.RootClass;
import org.hibernate.util.XMLHelper;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;
import org.xml.sax.InputSource;

public class OpenMappingActionDelegate extends OpenActionDelegate {
	private static XMLHelper helper = new XMLHelper();
	private HashMap map = new HashMap();

	public void run(IAction action) {
    	ObjectPluginAction objectPluginAction = (ObjectPluginAction)action;
    	RootClass rootClass = (RootClass)((TreeSelection)objectPluginAction.getSelection()).getFirstElement();
		ConsoleConfiguration consoleConfiguration = (ConsoleConfiguration)(((TreeSelection)objectPluginAction.getSelection()).getPaths()[0]).getSegment(0);
		IJavaProject proj = findJavaProject(consoleConfiguration);
		java.io.File configXMLFile = consoleConfiguration.getPreferences().getConfigXMLFile();
		Document doc = getDocument(consoleConfiguration, configXMLFile);
    	IResource resource = getResource(consoleConfiguration, proj, doc, configXMLFile, rootClass);

        if (resource == null) {
    		String fullyQualifiedName = rootClass.getClassName();
    		try {
    			resource = proj.findType(fullyQualifiedName).getResource();
    		} catch (JavaModelException e1) {
    			e1.printStackTrace();
    		}
        }

    	if (resource != null && resource instanceof IFile){
            try {
                IDE.openEditor(ViewPlugin.getPage(), (IFile) resource);
            } catch (PartInitException e) {
    			ViewPlugin.getDefault().logError("Can't open mapping or source file.", e);
            }               
        }
	}

	private boolean classInResource(ConsoleConfiguration consoleConfiguration, IResource resource, RootClass rootClass) {
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
						if (map.get(rootClass.getClassName()) == null) map.put(rootClass.getClassName(), resource);
						return true;
					} else {
						Attribute packNode = hmNode.attribute( "package" );
						String packageName = null;
						if ( packNode != null ) {
							packageName = packNode.getValue();
							String className = packageName + "." + classAttr.getValue();
							if (className.equals(rootClass.getClassName())) {
								if (map.get(rootClass.getClassName()) == null) map.put(rootClass.getClassName(), resource);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private IResource getResource(ConsoleConfiguration consoleConfiguration, IJavaProject proj, Document doc, java.io.File configXMLFile, RootClass rootClass) {
		if (map.get(rootClass.getClassName()) != null) {
			return (IResource)map.get(rootClass.getClassName());
		} else {
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
	    					if (classInResource(consoleConfiguration, resource, rootClass)) return resource;
	    				}
	    			}
	    		}
	    		java.io.File[] files = consoleConfiguration.getPreferences().getMappingFiles();
	    		for (int i = 0; i < files.length; i++) {
	    			java.io.File file = files[i];
					if (file != null) {
						resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(file.getPath()));
						if (classInResource(consoleConfiguration, resource, rootClass)) return resource;
					}
				}
	    	}
	    	return null;
		}
	}

	private Document getDocument(ConsoleConfiguration consoleConfiguration, java.io.File configXMLFile) {
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
}