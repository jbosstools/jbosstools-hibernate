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
	static String SQL_QUERY_ENTITY = "Hibernate3SQLQuery";
	static String SYNCHRONIZES_ENTITY = "Hibernate3Synchronize";
	static String RESULT_SET_ENTITY = "Hibernate3ResultSet";
	
	static IFormData SYNCHRONIZES_LIST_DEFINITION = new FormData(
		"Synchronize",
		"", //"Description
		new FormAttributeData[]{new FormAttributeData("table", 100)},
		new String[]{SYNCHRONIZES_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddSynchronize")
	);

	static IFormData SYNCHRONIZES_FOLDER_DEFINITION = new FormData(
		"Synchronize",
		"", //"Description
		"Synchronize",
		new FormAttributeData[]{new FormAttributeData("table", 100)},
		new String[]{SYNCHRONIZES_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddSynchronize")
	);

	final static IFormData[] SQL_QUERY_DEFINITIONS = new IFormData[] {
		new FormData(
			"SQL Query",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(SQL_QUERY_ENTITY)
		),
		new FormData(
			"Returns",
			"", //"Description
			new FormAttributeData[]{new FormAttributeData("alias", 100)},
			new String[]{"Hibernate3Return", "Hibernate3ReturnJoin", "Hibernate3LoadCollection"},
			Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.Returns.AddAnyReturn")
		),
		new FormData(
			"Return Scalars",
			"", //"Description
			new FormAttributeData[]{new FormAttributeData("column", 40), new FormAttributeData("type", 60)},
			new String[]{"Hibernate3ReturnScalar"},
			Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddReturnScalar")
		),
		SYNCHRONIZES_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(SQL_QUERY_ENTITY)
		),
	};

	final static IFormData[] RESULT_SET_DEFINITIONS = new IFormData[] {
		new FormData(
			"Result Set",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(RESULT_SET_ENTITY)
		),
		new FormData(
			"Returns",
			"", //"Description
			new FormAttributeData[]{new FormAttributeData("alias", 100)},
			new String[]{"Hibernate3Return", "Hibernate3ReturnJoin", "Hibernate3LoadCollection"},
			Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.Returns.AddAnyReturn")
		),
		new FormData(
			"Return Scalars",
			"", //"Description
			new FormAttributeData[]{new FormAttributeData("column", 40), new FormAttributeData("type", 60)},
			new String[]{"Hibernate3ReturnScalar"},
			Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddReturnScalar")
		),
		new FormData(
			"Advanced",
			"", //"Description
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
