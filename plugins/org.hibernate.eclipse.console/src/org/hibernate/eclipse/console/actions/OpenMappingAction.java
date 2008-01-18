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
package org.hibernate.eclipse.console.actions;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;

/**
 * @author Dmitry Geraskov
 */

public class OpenMappingAction extends SelectionListenerAction {
	
	private static final String HIBERNATE_TAG_NAME = "name"; 
	private static final String HIBERNATE_TAG_ENTITY_NAME = "entity-name";

	public OpenMappingAction() {
		super("Open Mapping File");
		setToolTipText("Open Mapping File");
		setEnabled( true );
	}

	public void run() {
		IStructuredSelection sel = getStructuredSelection();
		if (sel instanceof TreeSelection){
			TreePath path = ((TreeSelection)sel).getPaths()[0];
			ConsoleConfiguration consoleConfiguration = (ConsoleConfiguration)(path.getSegment(0));
			try {
				run(path, consoleConfiguration);
			} catch (JavaModelException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("Can't find mapping file.", e);
			} catch (PartInitException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("Can't open mapping file.", e);
			} catch (FileNotFoundException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("Can't find mapping file.", e);
			} 
		}
	}
	
	public static void run(TreePath path, ConsoleConfiguration consoleConfiguration) throws PartInitException, JavaModelException, FileNotFoundException {
		boolean isPropertySel = (path.getLastSegment().getClass() == Property.class);
		if (isPropertySel){
			Property propertySel = (Property)path.getLastSegment();
			PersistentClass persClass = propertySel.getPersistentClass();
			if ( persClass == null 
					|| (RootClass.class.isAssignableFrom(persClass.getClass())
					&& persClass.getClass() != RootClass.class)){
				Property parentProp = (Property)path.getParentPath().getLastSegment();
				run(propertySel, parentProp, consoleConfiguration);
				return;
			}
		}
		run(path.getLastSegment(), consoleConfiguration);
	}
	
	/**
	 * @param selection
	 * @param consoleConfiguration
	 * @throws JavaModelException 
	 * @throws PartInitException 
	 * @throws PresistanceClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public static IEditorPart run(Object selection, ConsoleConfiguration consoleConfiguration) throws PartInitException, JavaModelException, FileNotFoundException {
		IEditorPart editorPart = null;
		IJavaProject proj = ProjectUtils.findJavaProject(consoleConfiguration);
		java.io.File configXMLFile = consoleConfiguration.getPreferences().getConfigXMLFile();
		IResource resource = null;
		if (selection instanceof Property){
			Property p = (Property)selection;
			if (p.getPersistentClass() == null) return null;
			//use PersistentClass to open editor
			resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, configXMLFile, p.getPersistentClass());
			//editorPart = openMapping(p.getPersistentClass(), consoleConfiguration);
		} else {
			resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, configXMLFile, selection);
			//editorPart = openMapping(selection, consoleConfiguration);
		}
		if (resource != null){
			editorPart = openMapping(resource);
			if (editorPart != null){
				applySelectionToEditor(selection, editorPart);				
			}
			return editorPart;
		} 
		
		//try to find hibernate-annotations		
		PersistentClass rootClass = null;
		if (selection instanceof PersistentClass) {
			rootClass = (PersistentClass)selection;
	    } else if (selection instanceof Property) {
    		Property p = (Property)selection;
    		if (p.getPersistentClass() == null) return null;
    		rootClass = (PersistentClass)p.getPersistentClass();    			
	    }
		if (rootClass != null){
			if (OpenFileActionUtils.rootClassHasAnnotations(consoleConfiguration, configXMLFile, rootClass)) {
				String fullyQualifiedName = rootClass.getClassName();
				editorPart =  new OpenSourceAction().run(selection, proj, fullyQualifiedName);
				return editorPart;
			}
		} else {
			throw new FileNotFoundException("Mapping for " + selection + " not found.");
		}
		return null;		
	}
	
	/**
	 * @param compositeProperty
	 * @param parentProperty
	 * @param consoleConfiguration
	 * @throws JavaModelException 
	 * @throws PartInitException 
	 * @throws FileNotFoundException 
	 * @throws BadLocationException 
	 */
	public static IEditorPart run(Property compositeProperty, Property parentProperty, ConsoleConfiguration consoleConfiguration) throws PartInitException, JavaModelException, FileNotFoundException{
		if (parentProperty.getPersistentClass() == null) return null;
		IJavaProject proj = ProjectUtils.findJavaProject(consoleConfiguration);
		java.io.File configXMLFile = consoleConfiguration.getPreferences().getConfigXMLFile();
		IResource resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, configXMLFile, parentProperty.getPersistentClass());

		IEditorPart editorPart = null;
		if (resource != null){
			editorPart = openMapping(resource);
			if (editorPart != null){
				ITextEditor[] textEditors = getTextEditors(editorPart);
				if (textEditors.length == 0) return editorPart;
				textEditors[0].selectAndReveal(0, 0);
				FindReplaceDocumentAdapter findAdapter = null;
				ITextEditor textEditor = null;
				for (int i = 0; i < textEditors.length && findAdapter == null; i++) {
					textEditor = textEditors[i];
					findAdapter = getFindDocAdapter(textEditor);					
				}
				if (findAdapter == null) return null;
				
				IRegion parentRegion = findSelection(parentProperty, findAdapter);
				if (parentRegion == null) return editorPart;
				IRegion propRegion = null;
				try {
					propRegion = findAdapter.find(parentRegion.getOffset()+parentRegion.getLength(), generatePattern(compositeProperty), true, true, false, true);
					if (propRegion == null){
						// try to use key-property
						String pattern = generatePattern(compositeProperty).replaceFirst("property", "key-property");	
						propRegion = findAdapter.find(parentRegion.getOffset()+parentRegion.getLength(), pattern, true, true, false, true);
					}
					/*if (propRegion == null){
						// try to find name = "<name>"
						String pattern = HIBERNATE_TAG_NAME + "[\\s]*=[\\s]*\"" + property.getNodeName() + '\"';	
						propRegion = findAdapter.find(parentRegion.getOffset()+parentRegion.getLength(), pattern, true, true, false, true);
					}*/
				} catch (BadLocationException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage("Selection not found.", e);
				}				
				
				if (propRegion != null){
					int length = compositeProperty.getNodeName().length();
					int offset = propRegion.getOffset() + propRegion.getLength() - length - 1;			
					propRegion = new Region(offset, length);
					textEditor.selectAndReveal(propRegion.getOffset(), propRegion.getLength());
					return editorPart;
				}
			}
			return editorPart;
		} 
				
   		if (parentProperty.getPersistentClass() != null && parentProperty.isComposite()){   			
   			PersistentClass rootClass = parentProperty.getPersistentClass();
			if (OpenFileActionUtils.rootClassHasAnnotations(consoleConfiguration, configXMLFile, rootClass)) {
				String fullyQualifiedName =((Component)((Property) parentProperty).getValue()).getComponentClassName();
				IEditorPart editor = new OpenSourceAction().run(compositeProperty, proj, fullyQualifiedName);
				return editor;
			}
	    }
   		if (editorPart == null) {
			throw new FileNotFoundException("Mapping file for property '" + compositeProperty.getNodeName() + "' not found.");
		}
   		return null;
	}

	/**
	 * @param selection
	 * @param editorPart
	 */
	static public boolean applySelectionToEditor(Object selection, IEditorPart editorPart) {
		ITextEditor[] textEditors = getTextEditors(editorPart);
		if (textEditors.length == 0) return false;
		textEditors[0].selectAndReveal(0, 0);
		FindReplaceDocumentAdapter findAdapter = null;
		ITextEditor textEditor = null;
		for (int i = 0; i < textEditors.length && findAdapter == null; i++) {
			textEditor = textEditors[i];
			findAdapter = getFindDocAdapter(textEditor);					
		}
		if (findAdapter == null) return false;		
		IRegion selectRegion = null;		

		if (selection instanceof RootClass
				|| selection instanceof Subclass){
			selectRegion = findSelection((PersistentClass)selection, findAdapter);
		} else if (selection instanceof Property){
			selectRegion = findSelection((Property)selection, findAdapter);
		}
		
		if (selectRegion != null){
			textEditor.selectAndReveal(selectRegion.getOffset(), selectRegion.getLength());
			return true;
		}
		return false;
	}

	/**
	 * @param textEditor
	 * @return
	 */
	private static FindReplaceDocumentAdapter getFindDocAdapter(
			ITextEditor textEditor) {
		IDocument document = null;
		if (textEditor.getDocumentProvider() != null){
			document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		}
		if (document == null) return null;
		FindReplaceDocumentAdapter findAdapter = new FindReplaceDocumentAdapter(document);
		return findAdapter;
	}
	
	static public IEditorPart openMapping(IResource resource) {
		if (resource != null && resource instanceof IFile){
            try {
            	return OpenFileActionUtils.openEditor(HibernateConsolePlugin.getDefault().getActiveWorkbenchWindow().getActivePage(), (IFile) resource);
            } catch (PartInitException e) {
            	
            }               
        } else {
        	HibernateConsolePlugin.getDefault().log("Can't open mapping file " + resource);
        }
		return null;
	}

	/*static public IEditorPart openMapping(Object selElement,
			ConsoleConfiguration consoleConfiguration) {
		IJavaProject proj = ProjectUtils.findJavaProject(consoleConfiguration);
		java.io.File configXMLFile = consoleConfiguration.getPreferences().getConfigXMLFile();
		IResource resource = OpenFileActionUtils.getResource(consoleConfiguration, proj, configXMLFile, selElement);

    	if (resource != null && resource instanceof IFile){
            try {
            	return OpenFileActionUtils.openEditor(HibernateConsolePlugin.getDefault().getActiveWorkbenchWindow().getActivePage(), (IFile) resource);
            } catch (PartInitException e) {
            	HibernateConsolePlugin.getDefault().logErrorMessage("Can't open mapping or source file.", e);
            }               
        } else {
        	HibernateConsolePlugin.getDefault().log("Can't open mapping file for " + selElement);
        }
		return null;
	}*/
	
	public static IRegion findSelection(Property property, FindReplaceDocumentAdapter findAdapter) {
		Assert.isNotNull(property.getPersistentClass());
		try {
			IRegion classRegion = findSelection(property.getPersistentClass(), findAdapter);
			if (classRegion == null) return null;
			IRegion finalRegion = findAdapter.find(classRegion.getOffset()+classRegion.getLength(), "</class", true, true, false, false);
			IRegion propRegion  = findAdapter.find(classRegion.getOffset()+classRegion.getLength(), generatePattern(property), true, true, false, true);
			if (propRegion == null) return null;
			if (finalRegion != null 
					&& propRegion.getOffset() > finalRegion.getOffset()){
				return null;
			} else {
					int length = property.getName().length();
					int offset = propRegion.getOffset() + propRegion.getLength() - length - 1;
					return new Region(offset, length);
			}
		} catch (BadLocationException e) {
			return null;
		}
		
	}
	public static IRegion findSelection(PersistentClass persClass,
			FindReplaceDocumentAdapter findAdapter) {
		try {
			String[] classPatterns = generatePatterns(persClass);
			IRegion classRegion = null;
			for (int i = 0; (classRegion == null) && (i < classPatterns.length); i++){
				classRegion = findAdapter.find(0, classPatterns[i], true, true, false, true);
			}
			if (classRegion == null) return null;
			int length = persClass.getNodeName().length();
			int offset = classRegion.getOffset() + classRegion.getLength() - length - 1;			
			return new Region(offset, length);
		} catch (BadLocationException e) {
			return null;
		}
	}
	
	private static String[] generatePatterns(PersistentClass persClass){
		String fullClassName = null;
		if (persClass.getEntityName() != null){
			fullClassName = persClass.getEntityName();
		} else {
			fullClassName = persClass.getClassName();
		}
		
		Cfg2HbmTool tool = new Cfg2HbmTool();
		String[] patterns = new String[4];
		StringBuffer pattern = new StringBuffer("<");
		pattern.append(tool.getTag(persClass));
		pattern.append("[\\s]+[.[^>]]*");
		pattern.append(HIBERNATE_TAG_NAME);
		pattern.append("[\\s]*=[\\s]*\"");
		pattern.append(persClass.getNodeName());
		pattern.append('\"');
		patterns[0] = pattern.toString();
		
		pattern = new StringBuffer("<");
		pattern.append(tool.getTag(persClass));
		pattern.append("[\\s]+[.[^>]]*");
		pattern.append(HIBERNATE_TAG_NAME);
		pattern.append("[\\s]*=[\\s]*\"");
		pattern.append(fullClassName);
		pattern.append('\"');
		patterns[1] = pattern.toString();
		
		pattern = new StringBuffer("<");
		pattern.append(tool.getTag(persClass));
		pattern.append("[\\s]+[.[^>]]*");
		pattern.append(HIBERNATE_TAG_ENTITY_NAME);
		pattern.append("[\\s]*=[\\s]*\"");
		pattern.append(persClass.getNodeName());
		pattern.append('\"');
		patterns[2] = pattern.toString();
		
		pattern = new StringBuffer("<");
		pattern.append(tool.getTag(persClass));
		pattern.append("[\\s]+[.[^>]]*");
		pattern.append(HIBERNATE_TAG_ENTITY_NAME);
		pattern.append("[\\s]*=[\\s]*\"");
		pattern.append(fullClassName);
		pattern.append('\"');
		patterns[3] = pattern.toString();
		return patterns;
	}
	
	private static String generatePattern(Property property){
		Cfg2HbmTool tool = new Cfg2HbmTool();
		StringBuffer pattern = new StringBuffer("<");
		if(property.getPersistentClass() != null &&
				property.getPersistentClass().getIdentifierProperty()==property) {
			if (property.isComposite()){
				pattern.append("composite-id");
			} else {
				pattern.append("id");
			}
		} else{
			pattern.append(tool.getTag(property));
		}
		pattern.append("[\\s]+[.[^>]]*");
		pattern.append(HIBERNATE_TAG_NAME);
		pattern.append("[\\s]*=[\\s]*\"");
		pattern.append(property.getNodeName());
		pattern.append('\"');	
		return pattern.toString();
	}
	
	/**
	 * Method gets all ITextEditors from IEditorPart.
	 * Never returns null.
	 * @param editorPart
	 * @return
	 */	
	public static ITextEditor[] getTextEditors(IEditorPart editorPart) {
		/*
		 * if EditorPart is MultiPageEditorPart then get ITextEditor from it.
		 */
		if (editorPart instanceof MultiPageEditorPart) {
			List testEditors = new ArrayList();			
    		IEditorPart[] editors = ((MultiPageEditorPart) editorPart).findEditors(editorPart.getEditorInput());
    		for (int i = 0; i < editors.length; i++) {
				if (editors[i] instanceof ITextEditor){
					testEditors.add(editors[i]);
				}
			}
    		return (ITextEditor[])testEditors.toArray(new ITextEditor[0]);
		} else if (editorPart instanceof ITextEditor){
			return new ITextEditor[]{(ITextEditor) editorPart};
		}
		return new ITextEditor[0];
	}	
}
