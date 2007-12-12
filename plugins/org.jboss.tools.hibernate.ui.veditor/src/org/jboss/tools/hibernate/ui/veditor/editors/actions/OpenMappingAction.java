package org.jboss.tools.hibernate.ui.veditor.editors.actions;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.IWorkbenchPart;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.ui.veditor.editors.VisualEditor;
import org.jboss.tools.hibernate.ui.veditor.editors.model.SpecialRootClass;
import org.jboss.tools.hibernate.ui.view.views.ObjectEditorInput;

/**
 * @author Dmitry Geraskov
 *
 */
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
		//java.io.File configXMLFile = consoleConfiguration.getPreferences().getConfigXMLFile();
		//IJavaProject proj = objectEditorInput.getJavaProject();

		VisualEditor part = (VisualEditor)getWorkbenchPart();
		Set selectedElements = part.getSelectedElements();

		Iterator iterator = selectedElements.iterator();
		while (iterator.hasNext()) {
			Object selection = iterator.next();
			if (selection instanceof Property
					&& ((Property)selection).getPersistentClass() instanceof SpecialRootClass){
				Property compositSel = ((Property)selection);
				Property parentProperty = ((SpecialRootClass)((Property)selection).getPersistentClass()).getProperty();
				org.hibernate.eclipse.console.actions.OpenMappingAction.run(compositSel, parentProperty, consoleConfiguration);
				continue;
			}
			if (selection instanceof SpecialRootClass) {
    			selection = ((SpecialRootClass)selection).getProperty();
			}
			org.hibernate.eclipse.console.actions.OpenMappingAction.run(selection, consoleConfiguration);
						
			
	    	/*IResource resource = null;
	    	Object selectedElement = selection;
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
		        			PersistentClass src = ((SpecialRootClass)rootClass).getProperty().getPersistentClass();
		        			while (src instanceof SpecialRootClass) {
			        			src = ((SpecialRootClass)src).getProperty().getPersistentClass();
		        			}
					    	resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, configXMLFile, src);
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

				if (resource == null) {
					Iterator collectionMappingsIterator = consoleConfiguration.getConfiguration().getCollectionMappings();
					while (collectionMappingsIterator.hasNext()) {
						Collection elem = (Collection)collectionMappingsIterator.next();
						Table collectionTable = elem.getCollectionTable();
						if (HibernateUtils.getTableName(collectionTable).equals(HibernateUtils.getTableName(table))) {
							PersistentClass owner = elem.getOwner();
					    	resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, configXMLFile, owner);
						}
					}
				}
			} else if (selectedElement instanceof Subclass) {
				Subclass rootClass = (Subclass)selectedElement;
		    	resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, configXMLFile, rootClass);
			}

			if (resource != null) {
				try {
					IEditorPart editorPart = OpenFileActionUtils.openEditor(VisualEditorPlugin.getPage(), resource);
					if (selectedElement instanceof PersistentClass
							|| (selectedElement instanceof Property
									&& ((Property)selectedElement).getPersistentClass() != null)){
						org.hibernate.eclipse.console.actions.OpenMappingAction.applySelectionToEditor(selectedElement, editorPart);						
					}
					} catch (PartInitException e) {
	    			VisualEditorPlugin.getDefault().logInfo("Can't open mapping file", e);
				}
			}*/
		} 
	}

	protected boolean calculateEnabled() {
		VisualEditor part = (VisualEditor)getWorkbenchPart();
		return part.getSelectedElements().size() > 0;
	}
}
