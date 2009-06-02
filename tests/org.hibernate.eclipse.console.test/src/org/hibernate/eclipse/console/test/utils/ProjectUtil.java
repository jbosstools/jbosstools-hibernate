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
package org.hibernate.eclipse.console.test.utils;

import java.lang.reflect.Field;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.ErrorEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.utils.OpenMappingUtils;
import org.hibernate.mapping.PersistentClass;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class ProjectUtil {


	public static String getPersistentClassName(PersistentClass persClass) {
		if (persClass == null) {
			return ""; //$NON-NLS-1$
		}
		return persClass.getEntityName() != null ? persClass.getEntityName() : persClass.getClassName();
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
		ITextEditor[] tEditors = OpenMappingUtils.getTextEditors(editor);
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
}
