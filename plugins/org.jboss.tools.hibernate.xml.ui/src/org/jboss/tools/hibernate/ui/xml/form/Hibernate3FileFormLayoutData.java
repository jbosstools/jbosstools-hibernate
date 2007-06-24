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
				"File Hibernate 3.0",
				"", //"Description
				Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData("FileHibernate3")
			),
			new FormData(
				"Advanced",
				"", //"Description
				Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData("FileHibernate3")
			),
			Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
			new FormData(
				"Types",
				"", //Description
				"Types",
				new FormAttributeData[]{new FormAttributeData("name", 30, "name"), new FormAttributeData("class", 70, "class")},
				new String[]{"Hibernate3Typedef"},
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddTypedef")
			),
			new FormData(
				"Imports",
				"", //Description
				"Imports",
				new FormAttributeData[]{new FormAttributeData("class", 60, "class"), new FormAttributeData("rename", 40, "rename")},
				new String[]{"Hibernate3Import"},
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddImport")
			),
			new FormData(
				"Classes",
				"", //Description
				"Classes",
				new FormAttributeData[]{new FormAttributeData("name", 100, "class name")},
				new String[]{"Hibernate3Class", "Hibernate3Subclass", "Hibernate3JoinedSubclass", "Hibernate3UnionSubclass"},
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyClass")
			),
			new FormData(
				"Queries",
				"", //"Description
				"Queries",
				new FormAttributeData[]{new FormAttributeData("name", 30, "name"), new FormAttributeData("query", 70, "query")},
				new String[]{"Hibernate3Query", "Hibernate3SQLQuery"},
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyQuery")
			),
			new FormData(
				"Filters",
				"", //"Description
				"Filters",
				new FormAttributeData[]{new FormAttributeData("name", 30, "name"), new FormAttributeData("value", 70, "value")},
				new String[]{"Hibernate3Filterdef"},
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddFilterdef")
			),
		};

	final static IFormData FILE_FORM_DEFINITION = new FormData(
		"FileHibernate3", new String[]{null}, FILE_DEFINITIONS);
}
