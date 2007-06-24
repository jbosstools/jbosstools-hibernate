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
import org.jboss.tools.common.model.ui.forms.IFormAttributeData;
import org.jboss.tools.common.model.ui.forms.IFormData;

public class Hibernate3DatabaseObjectFormLayoutData {
	static String DATABASE_OBJECT_DEF_ENTITY = "Hibernate3DatabaseObjectDef";
	static String DATABASE_OBJECT_CD_ENTITY = "Hibernate3DatabaseObjectCreateDrop";
	static String DIALECT_SCOPE_ENTITY = "Hibernate3DialectScope";
	static String DATABASE_FOLDER_ENTITY = "Hibernate3DatabaseObjectFolder";

	final static IFormData DIALECT_SCOPE_LIST_DEFINITION =	new FormData(
		"Dialect Scopes",
		"", //"Description
		new FormAttributeData[]{new FormAttributeData("name", 100)},
		new String[]{DIALECT_SCOPE_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddDialectScope")
	);

	final static IFormData DATABASE_OBJECT_LIST_DEFINITION = new FormData(
		"Database Objects",
		"", //"Description
		new FormAttributeData[]{new FormAttributeData("presentation", 100, "database object")},
		new String[]{DATABASE_OBJECT_DEF_ENTITY, DATABASE_OBJECT_CD_ENTITY},
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddDatabaseObject")
	);

	final static IFormData[] DATABASE_OBJECT_DEF_DEFINITIONS = new IFormData[] {
		new FormData(
			"Database Object",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(DATABASE_OBJECT_DEF_ENTITY)
		),
		DIALECT_SCOPE_LIST_DEFINITION,
	};

	final static IFormData[] DATABASE_OBJECT_CD_DEFINITIONS = new IFormData[] {
		new FormData(
			"Database Object",
			"", //"Description
//			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(DATABASE_OBJECT_CD_ENTITY)
			new IFormAttributeData[]{
				new FormAttributeData("create", null, Hibernate3FormLayoutDataUtil.SBFEE_CLASS_NAME),	
				new FormAttributeData("drop", null, Hibernate3FormLayoutDataUtil.SBFEE_CLASS_NAME),
			}
		),
		DIALECT_SCOPE_LIST_DEFINITION,
	};

	final static IFormData[] DATABASE_FOLDER_DEFINITIONS = new IFormData[] {
		DATABASE_OBJECT_LIST_DEFINITION,
	};

	static IFormData DATABASE_OBJECT_DEF_DEFINITION = new FormData(
		DATABASE_OBJECT_DEF_ENTITY, new String[]{null}, DATABASE_OBJECT_DEF_DEFINITIONS
	);

	static IFormData DATABASE_OBJECT_CD_DEFINITION = new FormData(
		DATABASE_OBJECT_CD_ENTITY, new String[]{null}, DATABASE_OBJECT_CD_DEFINITIONS
	);

	static IFormData DATABASE_FOLDER_DEFINITION = new FormData(
		DATABASE_FOLDER_ENTITY, new String[]{null}, DATABASE_FOLDER_DEFINITIONS
	);
}
