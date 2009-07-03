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

public class Hibernate3MapFormLayoutData {
	static String MAP_ENTITY = "Hibernate3Map";

	final static IFormData[] MAP_DEFINITIONS = new IFormData[] {
		new FormData(
			"Map",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(MAP_ENTITY)
		),
		new FormData(
			"Key",
			"", //$NON-NLS-1$
			"key", //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData("Hibernate3Key")
		),
		new FormData(
			"Index",
			"", //"Description //$NON-NLS-1$
			new IFormAttributeData[]{
				new FormAttributeData("index", 100, "Index Kind") //$NON-NLS-1$
			}
		),
		new FormData(
			"Element",
			"", //"Description //$NON-NLS-1$
			new IFormAttributeData[]{
				new FormAttributeData("element", 100, "Element Kind") //$NON-NLS-1$
			}
		),
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_LIST_DEFINITION,
		Hibernate3FilterFormLayoutData.FILTER_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(MAP_ENTITY)
		),
	};

	static IFormData MAP_DEFINITION = new FormData(
		MAP_ENTITY, new String[]{null}, MAP_DEFINITIONS
	);

}
