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
	static String CLASS_ENTITY = "Hibernate3Class"; //$NON-NLS-1$
	static String SUBCLASS_ENTITY = "Hibernate3Subclass"; //$NON-NLS-1$
	static String JOIN_ENTITY = "Hibernate3Join"; //$NON-NLS-1$
	static String JOINED_SUBCLASS_ENTITY = "Hibernate3JoinedSubclass"; //$NON-NLS-1$
	static String ALL_SUBCLASSES_ENTITY = "Hibernate3AllSubclassFolder"; //$NON-NLS-1$
	static String SUBCLASSES_ENTITY = "Hibernate3SubclassFolder"; //$NON-NLS-1$
	static String JOINED_SUBCLASSES_ENTITY = "Hibernate3JoinedSubclassFolder"; //$NON-NLS-1$
	
	static IFormData ALL_SUBCLASSES_LIST_DEFINITION = new FormData(
		Messages.Hibernate3ClassFormLayoutData_Subclasses,
		"", //Description //$NON-NLS-1$
		"Subclasses", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("details", 100, Messages.Hibernate3ClassFormLayoutData_SubclassInfo)}, //$NON-NLS-1$
		Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(ALL_SUBCLASSES_ENTITY, Messages.Hibernate3ClassFormLayoutData_Details),
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyClass") //$NON-NLS-1$
	);

	static IFormData ALL_SUBCLASSES_FOLDER_DEFINITION = new FormData(
		Messages.Hibernate3ClassFormLayoutData_Subclasses,
		"", //Description //$NON-NLS-1$
		ALL_SUBCLASSES_ENTITY,
		new FormAttributeData[]{new FormAttributeData("details", 100, Messages.Hibernate3ClassFormLayoutData_SubclassInfo)}, //$NON-NLS-1$
		Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(ALL_SUBCLASSES_ENTITY, Messages.Hibernate3ClassFormLayoutData_Details),
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyClass") //$NON-NLS-1$
	);

	static IFormData SUBCLASSES_LIST_DEFINITION = new FormData(
		Messages.Hibernate3ClassFormLayoutData_Subclasses,
		"", //Description //$NON-NLS-1$
		"Subclasses", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("details", 100, Messages.Hibernate3ClassFormLayoutData_SubclassInfo)}, //$NON-NLS-1$
		Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(SUBCLASSES_ENTITY, Messages.Hibernate3ClassFormLayoutData_Details),
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyClass") //$NON-NLS-1$
	);

	static IFormData SUBCLASSES_FOLDER_DEFINITION = new FormData(
		Messages.Hibernate3ClassFormLayoutData_Subclasses,
		"", //Description //$NON-NLS-1$
		SUBCLASSES_ENTITY,
		new FormAttributeData[]{new FormAttributeData("details", 100, Messages.Hibernate3ClassFormLayoutData_SubclassInfo)}, //$NON-NLS-1$
		Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(SUBCLASSES_ENTITY, Messages.Hibernate3ClassFormLayoutData_Details),
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyClass") //$NON-NLS-1$
	);

	static IFormData JOINED_SUBCLASSES_LIST_DEFINITION = new FormData(
		Messages.Hibernate3ClassFormLayoutData_Subclasses,
		"", //Description //$NON-NLS-1$
		"Subclasses", //$NON-NLS-1$
		new FormAttributeData[]{new FormAttributeData("name", 100, Messages.Hibernate3ClassFormLayoutData_ClassName)}, //$NON-NLS-1$
		Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(JOINED_SUBCLASSES_ENTITY, Messages.Hibernate3ClassFormLayoutData_Name),
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddJoinedSubclass") //$NON-NLS-1$
	);

	static IFormData JOINED_SUBCLASSES_FOLDER_DEFINITION = new FormData(
		Messages.Hibernate3ClassFormLayoutData_Subclasses,
		"", //Description //$NON-NLS-1$
		JOINED_SUBCLASSES_ENTITY,
		new FormAttributeData[]{new FormAttributeData("name", 100, Messages.Hibernate3ClassFormLayoutData_ClassName)}, //$NON-NLS-1$
		Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(JOINED_SUBCLASSES_ENTITY, Messages.Hibernate3ClassFormLayoutData_Name),
		Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddJoinedSubclass") //$NON-NLS-1$
	);

	private final static IFormData[] CLASS_DEFINITIONS = new IFormData[] {
		new FormData(
			Messages.Hibernate3ClassFormLayoutData_Class,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(CLASS_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createAllChildrenFormData(Messages.Hibernate3ClassFormLayoutData_Properties, 
				"Hibernate3AttributesCFolder", "Properties", "name", "CreateActions.AddAttribute"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ALL_SUBCLASSES_LIST_DEFINITION,
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3FilterFormLayoutData.FILTER_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3ClassFormLayoutData_Advanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(CLASS_ENTITY)
		),
	};

	final static IFormData CLASS_DEFINITION = new FormData(
		CLASS_ENTITY, new String[]{null}, CLASS_DEFINITIONS
	);

	private final static IFormData[] SUBCLASS_DEFINITIONS =	new IFormData[] {
		new FormData(
			Messages.Hibernate3ClassFormLayoutData_Subclass,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(SUBCLASS_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createAllChildrenFormData(Messages.Hibernate3ClassFormLayoutData_Properties, 
				"Hibernate3AttributesCFolder", "Properties", "name", "CreateActions.AddAttribute"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		SUBCLASSES_LIST_DEFINITION,
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_FOLDER_DEFINITION,
		new FormData(
			Messages.Hibernate3ClassFormLayoutData_Advanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(SUBCLASS_ENTITY)
		),
	};

	final static IFormData SUBCLASS_DEFINITION = new FormData(
		SUBCLASS_ENTITY, new String[]{null}, SUBCLASS_DEFINITIONS
	);

	private final static IFormData[] JOIN_DEFINITIONS =	new IFormData[] {
		new FormData(
			Messages.Hibernate3ClassFormLayoutData_Join,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(JOIN_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createAllChildrenFormData(Messages.Hibernate3ClassFormLayoutData_Properties, 
				"Hibernate3AttributesJFolder", "Properties", "name", "CreateActions.AddAttribute"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3ClassFormLayoutData_Advanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(JOIN_ENTITY)
		),
	};

	final static IFormData JOIN_DEFINITION = new FormData(
		JOIN_ENTITY, new String[]{null}, JOIN_DEFINITIONS
	);

	private final static IFormData[] JOINED_SUBCLASS_DEFINITIONS =	new IFormData[] {
		new FormData(
			Messages.Hibernate3ClassFormLayoutData_JoinedSubclass,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createGeneralFormAttributeData(JOINED_SUBCLASS_ENTITY)
		),
		Hibernate3FormLayoutDataUtil.createAllChildrenFormData(Messages.Hibernate3ClassFormLayoutData_Properties, 
				"Hibernate3AttributesCFolder", "Properties", "name", "CreateActions.AddAttribute"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JOINED_SUBCLASSES_LIST_DEFINITION,
		Hibernate3MetaFormLayoutData.META_LIST_DEFINITION,
		Hibernate3SQLQueryFormLayoutData.SYNCHRONIZES_LIST_DEFINITION,
		new FormData(
			Messages.Hibernate3ClassFormLayoutData_Advanced,
			"", //"Description //$NON-NLS-1$
			Hibernate3FormLayoutDataUtil.createAdvancedFormAttributeData(JOINED_SUBCLASS_ENTITY)
		),
	};

	final static IFormData JOINED_SUBCLASS_DEFINITION = new FormData(
		JOINED_SUBCLASS_ENTITY, new String[]{null}, JOINED_SUBCLASS_DEFINITIONS
	);

}
