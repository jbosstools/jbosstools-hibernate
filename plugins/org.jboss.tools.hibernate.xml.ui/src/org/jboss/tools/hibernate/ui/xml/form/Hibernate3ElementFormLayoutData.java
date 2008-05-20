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

import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.IFormData;

public class Hibernate3ElementFormLayoutData {
	static String ELEMENT_ENTITY = "Hibernate3Element";
	static String MANY_TO_MANY_ENTITY = "Hibernate3ManyToMany";
	static String MANY_TO_ANY_ENTITY = "Hibernate3ManyToAny";

	final static IFormData[] ELEMENT_DEFINITIONS = new IFormData[] {
		new FormData(
			"Element",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(ELEMENT_ENTITY)
		),
		Hibernate3ColumnFormLayoutData.COLUMN_LIST_DEFINITION,
		Hibernate3FormulaFormLayoutData.FORMULA_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(ELEMENT_ENTITY)
		)
	};

	static IFormData ELEMENT_DEFINITION = new FormData(
		ELEMENT_ENTITY, new String[]{null}, ELEMENT_DEFINITIONS
	);

	final static IFormData[] MANY_TO_MANY_DEFINITIONS = new IFormData[] {
		new FormData(
			"Many To Many",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(MANY_TO_MANY_ENTITY)
		),
		Hibernate3ColumnFormLayoutData.COLUMN_LIST_DEFINITION,
		Hibernate3FormulaFormLayoutData.FORMULA_LIST_DEFINITION,
		Hibernate3FilterFormLayoutData.FILTER_LIST_DEFINITION,
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(MANY_TO_MANY_ENTITY)
		)
	};

	static IFormData MANY_TO_MANY_DEFINITION = new FormData(
		MANY_TO_MANY_ENTITY, new String[]{null}, MANY_TO_MANY_DEFINITIONS
	);

	final static IFormData[] MANY_TO_ANY_DEFINITIONS = new IFormData[] {
		new FormData(
			"Many To Any",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(MANY_TO_ANY_ENTITY)
		),
		Hibernate3AnyFormLayoutData.META_VALUE_LIST_DEFINITION,
		Hibernate3ColumnFormLayoutData.COLUMN_LIST_DEFINITION,
	};

	static IFormData MANY_TO_ANY_DEFINITION = new FormData(
		MANY_TO_ANY_ENTITY, new String[]{null}, MANY_TO_ANY_DEFINITIONS
	);

}
