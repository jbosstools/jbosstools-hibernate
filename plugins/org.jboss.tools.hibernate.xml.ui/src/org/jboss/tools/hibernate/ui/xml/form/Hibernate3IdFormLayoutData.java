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
public class Hibernate3IdFormLayoutData {
	static String COMPOSITE_ID_ENTITY = "Hibernate3CompositeId"; //$NON-NLS-1$
	static String COMPOSITE_INDEX_ENTITY = "Hibernate3CompositeIndex"; //$NON-NLS-1$
	static String COLLECTION_ID_ENTITY = "Hibernate3CollectionId"; //$NON-NLS-1$
	
	final static IFormData[] COMPOSITE_ID_DEFINITIONS =	new IFormData[] {
		new FormData(
			"Composite ID",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(COMPOSITE_ID_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createChildrenFormData(
				"Key Properties", null, null, 
				"name",  //$NON-NLS-1$
				new String[]{"Hibernate3KeyProperty", "Hibernate3KeyManyToOne"}, "CreateActions.AddKeys.AddKey"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(COMPOSITE_ID_ENTITY)
		),
	};

	static IFormData COMPOSITE_ID_DEFINITION = new FormData(
		COMPOSITE_ID_ENTITY, new String[]{null}, COMPOSITE_ID_DEFINITIONS
	);

	final static IFormData[] COLLECTION_ID_DEFINITIONS =	new IFormData[] {
		new FormData(
			"Collection ID",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(COLLECTION_ID_ENTITY)
		),
		Hibernate3ColumnFormLayoutData.COLUMN_LIST_DEFINITION,
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(COLLECTION_ID_ENTITY)
		),
	};

	static IFormData COLLECTION_ID_DEFINITION = new FormData(
		COLLECTION_ID_ENTITY, new String[]{null}, COLLECTION_ID_DEFINITIONS
	);

	final static IFormData[] COMPOSITE_INDEX_DEFINITIONS =	new IFormData[] {
		new FormData(
			"Composite Index",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(COMPOSITE_INDEX_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createChildrenFormData(
				"Key Properties", null, null, 
				"name",  //$NON-NLS-1$
				new String[]{"Hibernate3KeyProperty", "Hibernate3KeyManyToOne"}, "CreateActions.AddKeys.AddKey"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	};

	static IFormData COMPOSITE_INDEX_DEFINITION = new FormData(
		COMPOSITE_INDEX_ENTITY, new String[]{null}, COMPOSITE_INDEX_DEFINITIONS
	);

}
