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

/**
 * @author glory
 */
public class Hibernate3KeyFormLayoutData {
	static String KEY_ENTITY = "Hibernate3Key";	 //$NON-NLS-1$
	static String KEY_MANY_TO_ONE_ENTITY = "Hibernate3KeyManyToOne"; //$NON-NLS-1$
	static String KEY_PROPERTY_ENTITY = "Hibernate3KeyProperty"; //$NON-NLS-1$
	
	static String MAP_KEY_ENTITY = "Hibernate3MapKey"; //$NON-NLS-1$
	static String MAP_KEY_MANY_TO_MANY_ENTITY = "Hibernate3MapKeyManyToMany"; //$NON-NLS-1$
	static String COMPOSITE_MAP_KEY_ENTITY = "Hibernate3CompositeMapKey"; //$NON-NLS-1$
	
	static String INDEX_ENTITY = "Hibernate3Index"; //$NON-NLS-1$
	static String LIST_INDEX_ENTITY = "Hibernate3ListIndex"; //$NON-NLS-1$

	final static IFormData[] KEY_DEFINITIONS =	new IFormData[] {
		new FormData(
			"Key",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(KEY_ENTITY)
		),
		Hibernate3ColumnFormLayoutData.COLUMN_LIST_DEFINITION,
//		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(KEY_ENTITY)
		),
	};

	static IFormData KEY_DEFINITION = new FormData(
		KEY_ENTITY, new String[]{null}, KEY_DEFINITIONS
	);

	final static IFormData[] KEY_MANY_TO_ONE_DEFINITIONS =	new IFormData[] {
		new FormData(
			"Key Many To One",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(KEY_MANY_TO_ONE_ENTITY)
		),
		Hibernate3ColumnFormLayoutData.COLUMN_LIST_DEFINITION,
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(KEY_MANY_TO_ONE_ENTITY)
		),
	};

	static IFormData KEY_MANY_TO_ONE_DEFINITION = new FormData(
		KEY_MANY_TO_ONE_ENTITY, new String[]{null}, KEY_MANY_TO_ONE_DEFINITIONS
	);

	final static IFormData[] KEY_PROPERTY_DEFINITIONS =	new IFormData[] {
		new FormData(
			"Key Property",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(KEY_PROPERTY_ENTITY)
		),
		Hibernate3ColumnFormLayoutData.COLUMN_LIST_DEFINITION,
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(KEY_PROPERTY_ENTITY)
		),
	};

	static IFormData KEY_PROPERTY_DEFINITION = new FormData(
		KEY_PROPERTY_ENTITY, new String[]{null}, KEY_PROPERTY_DEFINITIONS
	);

	final static IFormData[] MAP_KEY_DEFINITIONS =	new IFormData[] {
		new FormData(
			"Map Key",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(MAP_KEY_ENTITY)
		),
		Hibernate3ColumnFormLayoutData.COLUMN_LIST_DEFINITION,
		Hibernate3FormulaFormLayoutData.FORMULA_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(MAP_KEY_ENTITY)
		),
	};

	static IFormData MAP_KEY_DEFINITION = new FormData(
		MAP_KEY_ENTITY, new String[]{null}, MAP_KEY_DEFINITIONS
	);

	final static IFormData[] MAP_KEY_MANY_TO_MANY_DEFINITIONS =	new IFormData[] {
		new FormData(
			"Map Key Many To Many",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(MAP_KEY_MANY_TO_MANY_ENTITY)
		),
		Hibernate3ColumnFormLayoutData.COLUMN_LIST_DEFINITION,
		Hibernate3FormulaFormLayoutData.FORMULA_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(MAP_KEY_MANY_TO_MANY_ENTITY)
		),
	};

	static IFormData MAP_KEY_MANY_TO_MANY_DEFINITION = new FormData(
		MAP_KEY_MANY_TO_MANY_ENTITY, new String[]{null}, MAP_KEY_MANY_TO_MANY_DEFINITIONS
	);

	final static IFormData[] COMPOSITE_MAP_KEY_DEFINITIONS =	new IFormData[] {
		new FormData(
			"Composite Map key",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(COMPOSITE_MAP_KEY_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createChildrenFormData("Key Properties", 
				null, null, "name", new String[]{"Hibernate3KeyProperty", "Hibernate3KeyManyToOne"}, "CreateActions.AddKeys.AddKey"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	};

	static IFormData COMPOSITE_MAP_KEY_DEFINITION = new FormData(
		COMPOSITE_MAP_KEY_ENTITY, new String[]{null}, COMPOSITE_MAP_KEY_DEFINITIONS
	);

	final static IFormData[] INDEX_DEFINITIONS = new IFormData[] {
		new FormData(
			"Index",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(INDEX_ENTITY)
		),
		Hibernate3ColumnFormLayoutData.COLUMN_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(INDEX_ENTITY)
		),
	};

	static IFormData INDEX_DEFINITION = new FormData(
		INDEX_ENTITY, new String[]{null}, INDEX_DEFINITIONS
	);

	final static IFormData[] LIST_INDEX_DEFINITIONS = new IFormData[] {
		new FormData(
			"List Index",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(LIST_INDEX_ENTITY)
		),
		Hibernate3ColumnFormLayoutData.COLUMN_LIST_DEFINITION
	};

	static IFormData LIST_INDEX_DEFINITION = new FormData(
		LIST_INDEX_ENTITY, new String[]{null}, LIST_INDEX_DEFINITIONS
	);

}
