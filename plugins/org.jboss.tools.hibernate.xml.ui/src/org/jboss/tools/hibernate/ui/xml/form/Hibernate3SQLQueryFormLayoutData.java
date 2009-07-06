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
public class Hibernate3SQLQueryFormLayoutData {
	static String SQL_QUERY_ENTITY = "Hibernate3SQLQuery"; //$NON-NLS-1$
	static String SYNCHRONIZES_ENTITY = "Hibernate3Synchronize"; //$NON-NLS-1$
	static String RESULT_SET_ENTITY = "Hibernate3ResultSet"; //$NON-NLS-1$
	
	static IFormData SYNCHRONIZES_LIST_DEFINITION = new FormData(
		Messages.Hibernate3SQLQueryFormLayoutData_SyncList,
		"", //"Description //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("table", 100)}, //$NON-NLS-1$
		new String[]{SYNCHRONIZES_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddSynchronize") //$NON-NLS-1$
	);

	static IFormData SYNCHRONIZES_FOLDER_DEFINITION = new FormData(
		Messages.Hibernate3SQLQueryFormLayoutData_SyncFolder,
		"", //"Description //$NON-NLS-1$
		"Synchronize", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("table", 100)}, //$NON-NLS-1$
		new String[]{SYNCHRONIZES_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddSynchronize") //$NON-NLS-1$
	);

	final static IFormData[] SQL_QUERY_DEFINITIONS = new IFormData[] {
		new FormData(
			Messages.Hibernate3SQLQueryFormLayoutData_SQLQuery,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(SQL_QUERY_ENTITY)
		),
		new FormData(
			Messages.Hibernate3SQLQueryFormLayoutData_QueryReturns,
			"", //"Description //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("alias", 100)}, //$NON-NLS-1$
			new String[]{"Hibernate3Return", "Hibernate3ReturnJoin", "Hibernate3LoadCollection"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.Returns.AddAnyReturn") //$NON-NLS-1$
		),
		new FormData(
			Messages.Hibernate3SQLQueryFormLayoutData_QueryReturnScalars,
			"", //"Description //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("column", 40), new FormAttributeData("type", 60)}, //$NON-NLS-1$ //$NON-NLS-2$
			new String[]{"Hibernate3ReturnScalar"}, //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddReturnScalar") //$NON-NLS-1$
		),
		SYNCHRONIZES_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3SQLQueryFormLayoutData_QueryAdvanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(SQL_QUERY_ENTITY)
		),
	};

	final static IFormData[] RESULT_SET_DEFINITIONS = new IFormData[] {
		new FormData(
			Messages.Hibernate3SQLQueryFormLayoutData_ResultSet,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(RESULT_SET_ENTITY)
		),
		new FormData(
			Messages.Hibernate3SQLQueryFormLayoutData_ResultSetReturns,
			"", //"Description //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("alias", 100)}, //$NON-NLS-1$
			new String[]{"Hibernate3Return", "Hibernate3ReturnJoin", "Hibernate3LoadCollection"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.Returns.AddAnyReturn") //$NON-NLS-1$
		),
		new FormData(
			Messages.Hibernate3SQLQueryFormLayoutData_ResultSetReturnScalars,
			"", //"Description //$NON-NLS-1$
			new FormAttributeData[]{new FormAttributeData("column", 40), new FormAttributeData("type", 60)}, //$NON-NLS-1$ //$NON-NLS-2$
			new String[]{"Hibernate3ReturnScalar"}, //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddReturnScalar") //$NON-NLS-1$
		),
		new FormData(
			Messages.Hibernate3SQLQueryFormLayoutData_ResultSetAdvanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(RESULT_SET_ENTITY)
		),
	};

	final static IFormData SQL_QUERY_DEFINITION = new FormData(
		SQL_QUERY_ENTITY, new String[]{null}, SQL_QUERY_DEFINITIONS
	);

	final static IFormData RESULT_SET_DEFINITION = new FormData(
		RESULT_SET_ENTITY, new String[]{null}, RESULT_SET_DEFINITIONS
	);

}
