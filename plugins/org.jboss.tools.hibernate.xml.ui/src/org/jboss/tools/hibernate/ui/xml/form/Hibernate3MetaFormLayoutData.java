/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.ui.xml.form;

import org.jboss.tools.common.model.ui.forms.FormAttributeData;
import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.IFormData;

/**
 * @author glory
 */
public class Hibernate3MetaFormLayoutData {
	static String META_ENTITY = "Hibernate3Meta"; //$NON-NLS-1$
	static String TUPLIZER_ENTITY = "Hibernate3Tuplizer"; //$NON-NLS-1$
	
	final static IFormData META_LIST_DEFINITION = new FormData(
		Messages.Hibernate3MetaFormLayoutData_List,
		"", //Description //$NON-NLS-1$
		"Meta", //$NON-NLS-1$
		new FormAttributeData[]{
				new FormAttributeData("attribute", 30, Messages.Hibernate3MetaFormLayoutData_Attr),  //$NON-NLS-1$
				new FormAttributeData("value", 70, Messages.Hibernate3MetaFormLayoutData_Value)}, //$NON-NLS-1$
		new String[]{META_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddMeta") //$NON-NLS-1$
	);

	final static IFormData META_FOLDER_DEFINITION = new FormData(
		Messages.Hibernate3MetaFormLayoutData_Folder,
		"", //"Description //$NON-NLS-1$
		"Hibernate3MetaFolder", //$NON-NLS-1$
		new FormAttributeData[]{
				new FormAttributeData("attribute", 30, Messages.Hibernate3MetaFormLayoutData_Attr),  //$NON-NLS-1$
				new FormAttributeData("value", 70, Messages.Hibernate3MetaFormLayoutData_Value)}, //$NON-NLS-1$
		new String[]{META_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddMeta") //$NON-NLS-1$
	);

	final static IFormData TUPLIZER_LIST_DEFINITION = new FormData(
		Messages.Hibernate3MetaFormLayoutData_TuplizersList,
		"", //"Description //$NON-NLS-1$
		"Tuplizers", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("attribute", 100, Messages.Hibernate3MetaFormLayoutData_Class)}, //$NON-NLS-1$
		new String[]{TUPLIZER_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddTuplizer") //$NON-NLS-1$
	);
	final static IFormData TUPLIZER_FOLDER_DEFINITION = new FormData(
		Messages.Hibernate3MetaFormLayoutData_TuplizersFolder,
		"", //"Description //$NON-NLS-1$
		"Hibernate3TuplizerFolder", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("attribute", 100, Messages.Hibernate3MetaFormLayoutData_Class)}, //$NON-NLS-1$
		new String[]{TUPLIZER_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddTuplizer") //$NON-NLS-1$
	);
}
