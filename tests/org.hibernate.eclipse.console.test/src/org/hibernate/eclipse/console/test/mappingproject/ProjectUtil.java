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
package org.hibernate.eclipse.console.test.mappingproject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.filters.StringInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ErrorEditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.wizards.ConsoleConfigurationCreationWizard;
import org.hibernate.eclipse.console.wizards.ConsoleConfigurationWizardPage;
import org.hibernate.eclipse.launch.ConsoleConfigurationMainTab;
import org.hibernate.mapping.PersistentClass;

/**
 * @author Dmitry Geraskov
 *
 */
public class ProjectUtil {

	private static final StringBuilder XML_HEADER = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n") //$NON-NLS-1$
													.append("<!DOCTYPE hibernate-configuration PUBLIC\n") //$NON-NLS-1$
													.append("\"-//Hibernate/Hibernate Configuration DTD 3.0//EN\"\n") //$NON-NLS-1$
													.append("\"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd\">\n"); //$NON-NLS-1$

	private static final StringBuilder XML_CFG_START = new StringBuilder("<hibernate-configuration>\n") //$NON-NLS-1$
													.append("<session-factory>\n") //$NON-NLS-1$
													.append("<property name=\"hibernate.dialect\">") //$NON-NLS-1$
													.append(Customization.HIBERNATE_DIALECT)
													.append("</property>"); //$NON-NLS-1$

	private static final StringBuilder XML_CFG_END = new StringBuilder("</session-factory>\n") //$NON-NLS-1$
													.append("</hibernate-configuration>\n");	 //$NON-NLS-1$


	public static final String CFG_FILE_NAME = "hibernate.cfg.xml"; //$NON-NLS-1$

	public static final String ConsoleCFGName = "testConfigName"; //$NON-NLS-1$

	public static void customizeCFGFileForPack(IPackageFragment pack) throws CoreException{
		IFolder srcFolder = (IFolder) pack.getParent().getResource();
		IFile iFile = srcFolder.getFile(CFG_FILE_NAME);
		if (iFile.exists()) {
			iFile.delete(true, null);
		}
		String file_body = XML_HEADER.toString() + XML_CFG_START.toString();
		if (pack.getNonJavaResources().length > 0){
			Object[] ress = pack.getNonJavaResources();
			for (int i = 0; i < ress.length; i++) {
				if (ress[i] instanceof IFile){
					IFile res = (IFile)ress[i];
					if (res.getName().endsWith(".hbm.xml")){ //$NON-NLS-1$
						file_body += "<mapping resource=\"" + pack.getElementName().replace('.', '/') + '/' + res.getName() + "\"/>\n";  //$NON-NLS-1$//$NON-NLS-2$
					}
				}
			}
		}
		/*if (pack.getCompilationUnits().length > 0){
			ICompilationUnit[] comps = pack.getCompilationUnits();
			for (int i = 0; i < comps.length; i++) {
				ICompilationUnit compilationUnit = comps[i];
				IType[] types = compilationUnit.getTypes();
				for (int j = 0; j < types.length; j++) {
					IType type = types[j];
					if (type.isAnnotation()){
						System.out.println(type);
					}
				}
			}
		}*/

		file_body += XML_CFG_END.toString();
		iFile.create(new StringInputStream(file_body),
				   true, null);
	}

	public static String getPersistentClassName(PersistentClass persClass) {
		if (persClass == null) {
			return ""; //$NON-NLS-1$
		} else {
			return persClass.getEntityName() != null ? persClass.getEntityName() : persClass.getClassName();
		}
	}

	public static void createConsoleCFG() throws CoreException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		ConsoleConfigurationCreationWizard2 wiz = new ConsoleConfigurationCreationWizard2();
		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		WizardDialog wdialog = new WizardDialog(win.getShell(), wiz);
		wdialog.create();
		wiz.run();
		wdialog.close();
	}

	private static class ConsoleConfigurationCreationWizard2 extends ConsoleConfigurationCreationWizard{

		public void run() throws CoreException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
			IPath cfgFilePath = new Path(MappingTestProject.PROJECT_NAME + "/" +  //$NON-NLS-1$
					TestUtilsCommon.SRC_FOLDER + "/" + ProjectUtil.CFG_FILE_NAME); //$NON-NLS-1$
			ConsoleConfigurationWizardPage page = ((ConsoleConfigurationWizardPage)getPages()[0]);			
			ILaunchConfigurationTab[] tabs = page.getTabs();
			ConsoleConfigurationMainTab main = (ConsoleConfigurationMainTab) tabs[0];
			Class clazz = main.getClass();
			Field projectName = clazz.getDeclaredField("projectNameText");
			projectName.setAccessible(true);
			Text text = (Text) projectName.get(main);
			text.setText(MappingTestProject.PROJECT_NAME);
			page.setConfigurationFilePath(cfgFilePath);
			page.setName(ConsoleCFGName);
			page.performFinish();
		}
	}
	
	/**
	 * Sometimes we have exceptions while opening editors.
	 * IDE catches this exceptions and opens ErrorEditorPart instead of
	 * our editor. To be sure that editor opened without exception use this method.
	 * It gets occurred exception from the editor if it was and passes it up.
	 *
	 * @param editor
	 * @return
	 * @throws Throwable
	 */
	public static Throwable getExceptionIfItOccured(IEditorPart editor){
		if (editor instanceof ErrorEditorPart){
			Class<ErrorEditorPart> clazz = ErrorEditorPart.class;
			Field field;
			try {
				field = clazz.getDeclaredField("error"); //$NON-NLS-1$

				field.setAccessible(true);

				Object error = field.get(editor);
				if (error instanceof IStatus) {
					IStatus err_status = (IStatus) error;
					if (err_status.getSeverity() == Status.ERROR){
						return err_status.getException();
					}
				}
			// catch close means that exception occurred but we can't get it
			} catch (SecurityException e) {
				return new RuntimeException(ConsoleTestMessages.ProjectUtil_cannot_get_exception_from_erroreditorpart + e.getMessage());
			} catch (NoSuchFieldException e) {
				return new RuntimeException(ConsoleTestMessages.ProjectUtil_cannot_get_error_field_from_erroreditorpart + e.getMessage());
			} catch (IllegalArgumentException e) {
				return new RuntimeException(ConsoleTestMessages.ProjectUtil_cannot_get_error_field_from_erroreditorpart + e.getMessage());
			} catch (IllegalAccessException e) {
				return new RuntimeException(ConsoleTestMessages.ProjectUtil_cannot_get_error_field_from_erroreditorpart + e.getMessage());
			}
		}
		return null;
	}

	public static boolean checkHighlighting(IEditorPart editor){
		ITextEditor[] tEditors = getTextEditors(editor);
		boolean highlighted = false;
		for (int i = 0; i < tEditors.length && !highlighted; i++) {
			ITextEditor textEditor = tEditors[i];
			ISelection selection = textEditor.getSelectionProvider().getSelection();
			if (selection instanceof TextSelection){
				TextSelection tSelection = (TextSelection)selection;
				highlighted = tSelection.getLength() > 0;
			}
		}
		return highlighted;
	}


	/**
	 * Should be identical with OpenMappingAction.getTextEditors()
	 * @param editorPart
	 * @return
	 */
	public static ITextEditor[] getTextEditors(IEditorPart editorPart) {
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
