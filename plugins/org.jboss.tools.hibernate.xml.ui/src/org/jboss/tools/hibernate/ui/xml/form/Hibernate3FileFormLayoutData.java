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
public class Hibernate3FileFormLayoutData {

	private final static IFormData[] FILE_DEFINITIONS =
		new IFormData[] {
			new FormData(
				Messages.Hibernate3FileFormLayoutData_FileDef,
				"", //"Description //$NON-NLS-1$
				Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData("FileHibernate3") //$NON-NLS-1$
			),
			new FormData(
				Messages.Hibernate3FileFormLayoutData_Advanced,
				"", //"Description //$NON-NLS-1$
				Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData("FileHibernate3") //$NON-NLS-1$
			),
			Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
			new FormData(
				Messages.Hibernate3FileFormLayoutData_Types,
				"", //Description //$NON-NLS-1$
				"Types", //$NON-NLS-1$
				new FormAttributeData[]{
						new FormAttributeData("name", 30, Messages.Hibernate3FileFormLayoutData_Name),  //$NON-NLS-1$
						new FormAttributeData("class", 70, Messages.Hibernate3FileFormLayoutData_Class)}, //$NON-NLS-1$
				new String[]{"Hibernate3Typedef"}, //$NON-NLS-1$
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddTypedef") //$NON-NLS-1$
			),
			new FormData(
				Messages.Hibernate3FileFormLayoutData_Imports,
				"", //Description //$NON-NLS-1$
				"Imports", //$NON-NLS-1$
				new FormAttributeData[]{
						new FormAttributeData("class", 60, Messages.Hibernate3FileFormLayoutData_Class),  //$NON-NLS-1$
						new FormAttributeData("rename", 40, Messages.Hibernate3FileFormLayoutData_Rename)}, //$NON-NLS-1$
				new String[]{"Hibernate3Import"}, //$NON-NLS-1$
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddImport") //$NON-NLS-1$
			),
			new FormData(
				Messages.Hibernate3FileFormLayoutData_Classes,
				"", //Description //$NON-NLS-1$
				"Classes", //$NON-NLS-1$
				new FormAttributeData[]{new FormAttributeData("name", 100, Messages.Hibernate3FileFormLayoutData_ClassName)}, //$NON-NLS-1$
				new String[]{"Hibernate3Class", "Hibernate3Subclass", "Hibernate3JoinedSubclass", "Hibernate3UnionSubclass"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyClass") //$NON-NLS-1$
			),
			new FormData(
				Messages.Hibernate3FileFormLayoutData_Queries,
				"", //"Description //$NON-NLS-1$
				"Queries", //$NON-NLS-1$
				new FormAttributeData[]{
						new FormAttributeData("name", 30, Messages.Hibernate3FileFormLayoutData_Name),  //$NON-NLS-1$
						new FormAttributeData("query", 70, Messages.Hibernate3FileFormLayoutData_Query)}, //$NON-NLS-1$
				new String[]{"Hibernate3Query", "Hibernate3SQLQuery"}, //$NON-NLS-1$ //$NON-NLS-2$
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyQuery") //$NON-NLS-1$
			),
			new FormData(
				Messages.Hibernate3FileFormLayoutData_Filters,
				"", //"Description //$NON-NLS-1$
				"Filters", //$NON-NLS-1$
				new FormAttributeData[]{
						new FormAttributeData("name", 30, Messages.Hibernate3FileFormLayoutData_Name),  //$NON-NLS-1$
						new FormAttributeData("value", 70, Messages.Hibernate3FileFormLayoutData_Value)}, //$NON-NLS-1$
				new String[]{"Hibernate3Filterdef"}, //$NON-NLS-1$
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddFilterdef") //$NON-NLS-1$
			),
		};

	final static IFormData FILE_FORM_DEFINITION = new FormData(
		"FileHibernate3", new String[]{null}, FILE_DEFINITIONS); //$NON-NLS-1$
}
