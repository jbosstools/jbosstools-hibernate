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

public class Hibernate3ListFormLayoutData {
	static String LIST_ENTITY = "Hibernate3List";
	static String ARRAY_ENTITY = "Hibernate3Array";

	final static IFormData[] LIST_DEFINITIONS = new IFormData[] {
		new FormData(
			"List",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(LIST_ENTITY)
		),
		new FormData(
			"Key",
			"",
			"key",
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData("Hibernate3Key")
		),
		new FormData(
			"Index",
			"", //"Description
			new IFormAttributeData[]{
				new FormAttributeData("index", 100, "Index Kind")
			}
		),
		new FormData(
			"Element",
			"", //"Description
			new IFormAttributeData[]{
				new FormAttributeData("element", 100, "Element Kind")
			}
		),
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(LIST_ENTITY)
		),
	};

	final static IFormData[] ARRAY_DEFINITIONS = new IFormData[] {
		new FormData(
			"Array",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(ARRAY_ENTITY)
		),
		new FormData(
			"Key",
			"",
			"key",
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData("Hibernate3Key")
		),
		new FormData(
			"Index",
			"", //"Description
			new IFormAttributeData[]{
				new FormAttributeData("index", 100, "Index Kind")
			}
		),
		new FormData(
			"Element",
			"", //"Description
			new IFormAttributeData[]{
				new FormAttributeData("element", 100, "Element Kind")
			}
		),
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(ARRAY_ENTITY)
		),
	};

	static IFormData LIST_DEFINITION = new FormData(
		LIST_ENTITY, new String[]{null}, LIST_DEFINITIONS
	);

	static IFormData ARRAY_DEFINITION = new FormData(
		ARRAY_ENTITY, new String[]{null}, ARRAY_DEFINITIONS
	);

}
