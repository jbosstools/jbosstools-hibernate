/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.OpenMappingUtils;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IService;

/**
 * Open Mapping File action
 * 
 * @author Dmitry Geraskov
 * @author Vitali Yemialyanchyk
 */
public class OpenMappingAction extends SelectionListenerAction {

	public static final String OPENMAPPING_ACTIONID = "actionid.openmapping"; //$NON-NLS-1$

	private final String imageFilePath =  "icons/images/mapping.gif"; //$NON-NLS-1$

	public OpenMappingAction() {
		super(HibernateConsoleMessages.OpenMappingAction_open_mapping_file);
		setToolTipText(HibernateConsoleMessages.OpenMappingAction_open_mapping_file);
		setEnabled(true);
		setImageDescriptor(HibernateConsolePlugin.getImageDescriptor(imageFilePath ));
		setId(OPENMAPPING_ACTIONID);
	}

	public void run() {
		IStructuredSelection sel = getStructuredSelection();
		if (!(sel instanceof TreeSelection)) {
			return;
		}
		TreePath[] paths = ((TreeSelection)sel).getPaths();
		for (int i = 0; i < paths.length; i++) {
			TreePath path = paths[i];
			ConsoleConfiguration consoleConfig = (ConsoleConfiguration)(path.getSegment(0));
			try {
				run(consoleConfig, path);
			} catch (JavaModelException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.OpenMappingAction_cannot_find_mapping_file, e);
			} catch (PartInitException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.OpenMappingAction_cannot_open_mapping_file, e);
			} catch (FileNotFoundException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.OpenMappingAction_cannot_find_mapping_file, e);
			}
		}
	}

	/**
	 * @param path
	 * @param consoleConfig
	 * @return
	 * @throws PartInitException
	 * @throws JavaModelException
	 * @throws FileNotFoundException
	 */
	public static IEditorPart run(ConsoleConfiguration consoleConfig, TreePath path) 
			throws PartInitException, JavaModelException, FileNotFoundException {
		boolean isPropertySel = (path.getLastSegment() instanceof IProperty && ((IProperty)path.getLastSegment()).classIsPropertyClass());
		if (isPropertySel) {
			IProperty propertySel = (IProperty)path.getLastSegment();
			IPersistentClass persClass = propertySel.getPersistentClass();
			if (persClass == null
					|| (persClass.isAssignableToRootClass()
					&& !persClass.isRootClass())) {
				IProperty parentProp = (IProperty)path.getParentPath().getLastSegment();
				return run(consoleConfig, propertySel, parentProp);
			}
		}
		return run(consoleConfig, path.getLastSegment(), null);
	}

	/**
	 * @param consoleConfig
	 * @param selection
	 * @param selectionParent
	 * @throws JavaModelException
	 * @throws PartInitException
	 * @throws PresistanceClassNotFoundException
	 * @throws FileNotFoundException
	 */
	public static IEditorPart run(ConsoleConfiguration consoleConfig, Object selection, Object selectionParent) throws PartInitException, JavaModelException, FileNotFoundException {
		IEditorPart editorPart = null;
		IFile file = null;
		if (selection instanceof IProperty) {
			IProperty p = (IProperty)selection;
			if (p.getPersistentClass() != null) {
				//use PersistentClass to open editor
				file = OpenMappingUtils.searchFileToOpen(consoleConfig, p.getPersistentClass());
			}
		}
		else {
			if (selectionParent != null) {
				file = OpenMappingUtils.searchFileToOpen(consoleConfig, selectionParent);
			} else {
				file = OpenMappingUtils.searchFileToOpen(consoleConfig, selection);
			}
		}
		if (file != null) {
			editorPart = OpenMappingUtils.openFileInEditor(file);
			IService service = consoleConfig.getHibernateExtension().getHibernateService();
			boolean updateRes = updateEditorSelection(editorPart, selection, service);
			if (!updateRes && selectionParent != null) {
				// if it is not possible to select object, try to select it's parent
				updateRes = updateEditorSelection(editorPart, selectionParent, service);
			}
		}
		if (editorPart == null) {
			//try to find hibernate-annotations
			IPersistentClass rootClass = null;
			if (selection instanceof IPersistentClass) {
				rootClass = (IPersistentClass)selection;
		    }
			else if (selection instanceof IProperty) {
	    		IProperty p = (IProperty)selection;
	    		if (p.getPersistentClass() != null) {
	    			rootClass = p.getPersistentClass();
	    		}
		    }
			if (rootClass != null){
				if (OpenMappingUtils.hasConfigXMLMappingClassAnnotation(consoleConfig, rootClass)) {
					String fullyQualifiedName = rootClass.getClassName();
					editorPart = OpenSourceAction.run(consoleConfig, selection, fullyQualifiedName);
				}
			}
		}
   		if (editorPart == null) {
			final String title = HibernateConsoleMessages.OpenMappingAction_open_mapping_file;
			final String msg = NLS.bind(HibernateConsoleMessages.OpenMappingAction_mapping_for_not_found, selection);
			MessageDialog.openError(null, title, msg);
			throw new FileNotFoundException(msg);
		}
		return editorPart;
	}

	/**
	 * @param consoleConfig
	 * @param compositeProperty
	 * @param parentProperty
	 * @throws JavaModelException
	 * @throws PartInitException
	 * @throws FileNotFoundException
	 * @throws BadLocationException
	 */
	public static IEditorPart run(ConsoleConfiguration consoleConfig, IProperty compositeProperty, IProperty parentProperty) 
			throws PartInitException, JavaModelException, FileNotFoundException {
		IPersistentClass rootClass = parentProperty.getPersistentClass();
		IFile file = OpenMappingUtils.searchFileToOpen(consoleConfig, rootClass);
		IEditorPart editorPart = null;
		if (file != null){
			editorPart = OpenMappingUtils.openFileInEditor(file);
			IService service = consoleConfig.getHibernateExtension().getHibernateService();
			updateEditorSelection(editorPart, compositeProperty, parentProperty, service);
		}
   		if (editorPart == null && parentProperty.isComposite()) {
			if (OpenMappingUtils.hasConfigXMLMappingClassAnnotation(consoleConfig, rootClass)) {
				String fullyQualifiedName =parentProperty.getValue().getComponentClassName();
				editorPart = OpenSourceAction.run(consoleConfig, compositeProperty, fullyQualifiedName);
			}
	    }
   		if (editorPart == null) {
			final String title = HibernateConsoleMessages.OpenMappingAction_open_mapping_file;
			final String msg = NLS.bind(HibernateConsoleMessages.OpenMappingAction_mapping_file_for_property_not_found, compositeProperty.getName());
			MessageDialog.openError(null, title, msg);
   			throw new FileNotFoundException(msg);
   		}
   		return editorPart;
	}

	/**
	 * @param editorPart
	 * @param selection
	 */
	public static boolean updateEditorSelection(IEditorPart editorPart, Object selection, IService service) {
		ITextEditor[] textEditors = OpenMappingUtils.getTextEditors(editorPart);
		if (textEditors.length == 0) {
			return false;
		}
		textEditors[0].selectAndReveal(0, 0);
		FindReplaceDocumentAdapter findAdapter = null;
		ITextEditor textEditor = null;
		for (int i = 0; i < textEditors.length && findAdapter == null; i++) {
			textEditor = textEditors[i];
			findAdapter = OpenMappingUtils.createFindDocAdapter(textEditor);
		}
		if (findAdapter == null) {
			return false;
		}
		IJavaProject proj = ProjectUtils.findJavaProject(editorPart);
		IRegion selectRegion = OpenMappingUtils.findSelectRegion(proj, findAdapter, selection, service);
		if (selectRegion != null) {
			if (editorPart instanceof MultiPageEditorPart) {
				((MultiPageEditorPart)editorPart).setActiveEditor(textEditor);
			}
			textEditor.selectAndReveal(selectRegion.getOffset(), selectRegion.getLength());
			return true;
		}
		return false;
	}

	/**
	 * @param editorPart
	 * @param compositeProperty
	 * @param parentProperty
	 */
	public static boolean updateEditorSelection(IEditorPart editorPart, IProperty compositeProperty, IProperty parentProperty, IService service) {
		ITextEditor[] textEditors = OpenMappingUtils.getTextEditors(editorPart);
		if (textEditors.length == 0) {
			return false;
		}
		textEditors[0].selectAndReveal(0, 0);
		FindReplaceDocumentAdapter findAdapter = null;
		ITextEditor textEditor = null;
		for (int i = 0; i < textEditors.length && findAdapter == null; i++) {
			textEditor = textEditors[i];
			findAdapter = OpenMappingUtils.createFindDocAdapter(textEditor);
		}
		if (findAdapter == null) {
			return false;
		}
		IJavaProject proj = ProjectUtils.findJavaProject(editorPart);
		IRegion parentRegion = OpenMappingUtils.findSelectRegion(proj, findAdapter, parentProperty, service);
		if (parentRegion == null) {
			return false;
		}
		int startOffset = parentRegion.getOffset() + parentRegion.getLength();
		IRegion propRegion = null;
		try {
			final String hbmPropertyPattern = OpenMappingUtils.generateHbmPropertyPattern(compositeProperty, service);
			propRegion = findAdapter.find(startOffset, hbmPropertyPattern, true, true, false, true);
			IPersistentClass rootClass = parentProperty.getPersistentClass();
			if (propRegion == null && parentProperty.isComposite()
					&& (parentProperty.equals(rootClass.getIdentifierProperty()) ||
						!rootClass.hasIdentifierProperty())) {
				// try to use key-property
				String pattern = hbmPropertyPattern.replaceFirst("<property", "<key-property"); //$NON-NLS-1$ //$NON-NLS-2$
				propRegion = findAdapter.find(startOffset, pattern, true, true, false, true);
				if (propRegion == null) {
					// try to use key-many-to-one
					pattern = hbmPropertyPattern.replaceFirst("<many-to-one", "<key-many-to-one"); //$NON-NLS-1$ //$NON-NLS-2$
					propRegion = findAdapter.find(startOffset, pattern, true, true, false, true);
				}
			}
		} catch (BadLocationException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.OpenMappingAction_selection_not_found, e);
		}
		if (propRegion == null && parentProperty.isComposite()){
			String[] componentPatterns = new String[]{
					OpenMappingUtils.createPattern("embeddable", "class", parentProperty.getValue().getComponentClassName()), //$NON-NLS-1$ //$NON-NLS-2$
					OpenMappingUtils.createPattern("embeddable", "class", OpenMappingUtils.getShortClassName(  //$NON-NLS-1$//$NON-NLS-2$
							parentProperty.getValue().getComponentClassName()))
			};
			IRegion componentRegion = null;
			for (int i = 0; i < componentPatterns.length && componentRegion == null; i++) {
				try {
					componentRegion = findAdapter.find(0, componentPatterns[i], true, true, false, true);
				} catch (BadLocationException e) {
					//ignore
				}
			}
			if (componentRegion != null){
				try {
					propRegion = findAdapter.find(parentRegion.getOffset() + parentRegion.getLength(),
							OpenMappingUtils.generateOrmEmbeddablePropertyPattern(compositeProperty), true, true, false, true);
				} catch (BadLocationException e) {
					//ignore
				}
			}
		}
		if (propRegion == null) {
			return false;
		}
		int length = compositeProperty.getName().length();
		int offset = propRegion.getOffset() + propRegion.getLength() - length - 1;
		propRegion = new Region(offset, length);
		if (editorPart instanceof MultiPageEditorPart) {
			((MultiPageEditorPart)editorPart).setActiveEditor(textEditor);
		}
		textEditor.selectAndReveal(propRegion.getOffset(), propRegion.getLength());
		return true;
	}
}
