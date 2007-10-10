package org.jboss.tools.hibernate.ui.veditor.editors.actions;

import java.util.Iterator;
import java.util.Set;

import org.dom4j.Document;
import org.eclipse.core.resources.IResource;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.ui.veditor.VisualEditorPlugin;
import org.jboss.tools.hibernate.ui.veditor.editors.VisualEditor;
import org.jboss.tools.hibernate.ui.veditor.editors.model.OrmDiagram;
import org.jboss.tools.hibernate.ui.veditor.editors.model.SpecialRootClass;
import org.jboss.tools.hibernate.ui.view.views.HibernateUtils;
import org.jboss.tools.hibernate.ui.view.views.ObjectEditorInput;
import org.jboss.tools.hibernate.ui.view.views.OpenFileActionUtils;

public class OpenMappingAction extends SelectionAction {
	public static String ACTION_ID = "org.jboss.tools.hibernate.ui.veditor.editors.actions.open.mapping";

	public OpenMappingAction(IWorkbenchPart part) {
		super(part);
		setId(ACTION_ID);
		setText("Open Mapping File");
	}

	public void run() {
		ObjectEditorInput objectEditorInput = (ObjectEditorInput)((VisualEditor)getWorkbenchPart()).getEditorInput();
		ConsoleConfiguration consoleConfiguration = objectEditorInput.getConfiguration();
		java.io.File configXMLFile = consoleConfiguration.getPreferences().getConfigXMLFile();
		IJavaProject proj = objectEditorInput.getJavaProject();

		VisualEditor part = (VisualEditor)getWorkbenchPart();
		Set selectedElements = part.getSelectedElements();

		Iterator iterator = selectedElements.iterator();
		while (iterator.hasNext()) {
			Object selectedElement = iterator.next();

	    	IResource resource = null;
			if (selectedElement instanceof RootClass) {
				RootClass rootClass = (RootClass)selectedElement;

		    	resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, configXMLFile, rootClass);

		        if (resource == null) {
		        	if (OpenFileActionUtils.rootClassHasAnnotations(consoleConfiguration, configXMLFile, rootClass)) {
			    		String fullyQualifiedName = HibernateUtils.getPersistentClassName(rootClass);
			    		try {
			    			resource = proj.findType(fullyQualifiedName).getResource();
			    		} catch (JavaModelException e) {
			    			VisualEditorPlugin.getDefault().logInfo("Can't find mapping file", e);
			    		}
		        	} else {
		        		if (rootClass instanceof SpecialRootClass) {
					    	resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, configXMLFile, 
					    			((SpecialRootClass)rootClass).getProperty().getPersistentClass()
					    			);
		        		}
		        	}
		        }
			} else if (selectedElement instanceof Table) {
				Table table = (Table)selectedElement;
				resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, configXMLFile, table);

				if (resource == null) {
					Iterator classMappingsIterator = consoleConfiguration.getConfiguration().getClassMappings();
					while (classMappingsIterator.hasNext()) {
						PersistentClass elem = (PersistentClass) classMappingsIterator.next();
						if (HibernateUtils.getTableName(elem.getTable()).equals(HibernateUtils.getTableName(table))) {
				    		String fullyQualifiedName = HibernateUtils.getPersistentClassName(elem);
				    		try {
				    			resource = proj.findType(fullyQualifiedName).getResource();
				    		} catch (JavaModelException e) {
				    			VisualEditorPlugin.getDefault().logInfo("Can't find mapping file", e);
				    		}
						}
					}
				}
			} else if (selectedElement instanceof Subclass) {
				Subclass rootClass = (Subclass)selectedElement;
		    	resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, configXMLFile, rootClass);
			}

			if (resource != null) {
				try {
					OpenFileActionUtils.openEditor(VisualEditorPlugin.getPage(), resource);
				} catch (PartInitException e) {
	    			VisualEditorPlugin.getDefault().logInfo("Can't open mapping file", e);
				}
			}
		} 
	}

	protected boolean calculateEnabled() {
		VisualEditor part = (VisualEditor)getWorkbenchPart();
		return part.getSelectedElements().size() > 0;
	}
}
