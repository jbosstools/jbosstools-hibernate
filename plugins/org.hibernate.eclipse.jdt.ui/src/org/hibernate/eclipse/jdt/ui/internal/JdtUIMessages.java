/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitry Geraskov
 *
 */
public class JdtUIMessages extends NLS {
	
	private static final String BUNDLE_NAME= "org.hibernate.eclipse.jdt.ui.internal.JdtUIMessages";//$NON-NLS-1$

	public static String SaveQueryEditorListener_hql_editor;
	
	public static String SaveQueryEditorListener_cri_editor;
	
	public static String SaveQueryEditorListener_Composite_Change_Name;

	public static String SaveQueryEditorListener_Change_Name;
	
	public static String SaveQueryEditorListener_refactoringTitle;

	public static String SaveQueryEditorListener_replaceQuestion;
	
	public static String SaveQueryEditorListener_replaceTitle;
	
	public static String SaveQueryEditorListener_replaceQuestion_confirm;
	
	public static String SaveQueryEditorListener_replaceTitle_info;
	
	public static String CriteriaQuickAssistProcessor_errorMessage;
	
	public static String DebugJavaCompletionProposalComputer_displayString;
	
	public static String HQLJavaCompletionProposalComputer_errorMessage;
	
	public static String SaveQueryEditorListener_errorMessage;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, JdtUIMessages.class);
	}
	
	private JdtUIMessages(){
		// Do not instantiate
	}

}
