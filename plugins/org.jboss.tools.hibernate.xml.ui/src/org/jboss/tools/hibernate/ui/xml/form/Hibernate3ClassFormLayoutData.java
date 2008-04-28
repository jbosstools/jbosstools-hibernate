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
public class Hibernate3ClassFormLayoutData {
	static String CLASS_ENTITY = "Hibernate3Class";
	static String SUBCLASS_ENTITY = "Hibernate3Subclass";
	static String JOIN_ENTITY = "Hibernate3Join";
	static String JOINED_SUBCLASS_ENTITY = "Hibernate3JoinedSubclass";
	static String ALL_SUBCLASSES_ENTITY = "Hibernate3AllSubclassFolder";
	static String SUBCLASSES_ENTITY = "Hibernate3SubclassFolder";
	static String JOINED_SUBCLASSES_ENTITY = "Hibernate3JoinedSubclassFolder";
	
	static IFormData ALL_SUBCLASSES_LIST_DEFINITION = new FormData(
		"Subclasses",
		"", //Description
		"Subclasses",
		new FormAttributeData[]{new FormAttributeData("details", 100, "subclass info")},
		Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(ALL_SUBCLASSES_ENTITY, "details"),
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyClass")
	);

	static IFormData ALL_SUBCLASSES_FOLDER_DEFINITION = new FormData(
		"Subclasses",
		"", //Description
		ALL_SUBCLASSES_ENTITY,
		new FormAttributeData[]{new FormAttributeData("details", 100, "subclass info")},
		Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(ALL_SUBCLASSES_ENTITY, "details"),
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyClass")
	);

	static IFormData SUBCLASSES_LIST_DEFINITION = new FormData(
		"Subclasses",
		"", //Description
		"Subclasses",
		new FormAttributeData[]{new FormAttributeData("details", 100, "subclass info")},
		Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(SUBCLASSES_ENTITY, "details"),
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyClass")
	);

	static IFormData SUBCLASSES_FOLDER_DEFINITION = new FormData(
		"Subclasses",
		"", //Description
		SUBCLASSES_ENTITY,
		new FormAttributeData[]{new FormAttributeData("details", 100, "subclass info")},
		Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(SUBCLASSES_ENTITY, "details"),
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyClass")
	);

	static IFormData JOINED_SUBCLASSES_LIST_DEFINITION = new FormData(
		"Subclasses",
		"", //Description
		"Subclasses",
		new FormAttributeData[]{new FormAttributeData("name", 100, "class name")},
		Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(JOINED_SUBCLASSES_ENTITY, "name"),
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddJoinedSubclass")
	);

	static IFormData JOINED_SUBCLASSES_FOLDER_DEFINITION = new FormData(
		"Subclasses",
		"", //Description
		JOINED_SUBCLASSES_ENTITY,
		new FormAttributeData[]{new FormAttributeData("name", 100, "class name")},
		Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(JOINED_SUBCLASSES_ENTITY, "name"),
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddJoinedSubclass")
	);

	private final static IFormData[] CLASS_DEFINITIONS = new IFormData[] {
		new FormData(
			"Class",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(CLASS_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createAllChildrenFormData("Properties", "Hibernate3AttributesCFolder", "Properties", "name", "CreateActions.AddAttribute"),
		ALL_SUBCLASSES_LIST_DEFINITION,
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3FilterFormLayoutData.FILTER_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(CLASS_ENTITY)
		),
	};

	final static IFormData CLASS_DEFINITION = new FormData(
		CLASS_ENTITY, new String[]{null}, CLASS_DEFINITIONS
	);

	private final static IFormData[] SUBCLASS_DEFINITIONS =	new IFormData[] {
		new FormData(
			"Subclass",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(SUBCLASS_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createAllChildrenFormData("Properties", "Hibernate3AttributesCFolder", "Properties", "name", "CreateActions.AddAttribute"),
		SUBCLASSES_LIST_DEFINITION,
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_FOLDER_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(SUBCLASS_ENTITY)
		),
	};

	final static IFormData SUBCLASS_DEFINITION = new FormData(
		SUBCLASS_ENTITY, new String[]{null}, SUBCLASS_DEFINITIONS
	);

	private final static IFormData[] JOIN_DEFINITIONS =	new IFormData[] {
		new FormData(
			"Join",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(JOIN_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createAllChildrenFormData("Properties", "Hibernate3AttributesJFolder", "Properties", "name", "CreateActions.AddAttribute"),
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(JOIN_ENTITY)
		),
	};

	final static IFormData JOIN_DEFINITION = new FormData(
		JOIN_ENTITY, new String[]{null}, JOIN_DEFINITIONS
	);

	private final static IFormData[] JOINED_SUBCLASS_DEFINITIONS =	new IFormData[] {
		new FormData(
			"Joined Subclass",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(JOINED_SUBCLASS_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createAllChildrenFormData("Properties", "Hibernate3AttributesCFolder", "Properties", "name", "CreateActions.AddAttribute"),
		JOINED_SUBCLASSES_LIST_DEFINITION,
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_LIST_DEFINITION,
		new FormData(
			"Advanced",
			"", //"Description
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(JOINED_SUBCLASS_ENTITY)
		),
	};

	final static IFormData JOINED_SUBCLASS_DEFINITION = new FormData(
		JOINED_SUBCLASS_ENTITY, new String[]{null}, JOINED_SUBCLASS_DEFINITIONS
	);

}
