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

import java.util.Collections;
import java.util.Map;

import org.jboss.tools.common.model.ui.forms.ArrayToMap;
import org.jboss.tools.common.model.ui.forms.FormAttributeData;
import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.IFormData;
import org.jboss.tools.common.model.ui.forms.IFormLayoutData;
import org.jboss.tools.common.model.util.ClassLoaderUtil;

public class Hibernate3FormLayoutData implements IFormLayoutData {
	static {
		ClassLoaderUtil.init();
	}

	private final static IFormData[] FORM_LAYOUT_DEFINITIONS =
		new IFormData[] {
			Hibernate3FileFormLayoutData.FILE_FORM_DEFINITION,

			Hibernate3ClassFormLayoutData.CLASS_DEFINITION,
			Hibernate3ClassFormLayoutData.SUBCLASS_DEFINITION,
			Hibernate3ClassFormLayoutData.JOIN_DEFINITION,
			Hibernate3ClassFormLayoutData.JOINED_SUBCLASS_DEFINITION,
			Hibernate3ClassFormLayoutData.SUBCLASSES_FOLDER_DEFINITION,
			Hibernate3ClassFormLayoutData.ALL_SUBCLASSES_FOLDER_DEFINITION,
			Hibernate3ClassFormLayoutData.JOINED_SUBCLASSES_FOLDER_DEFINITION,
			
			Hibernate3MapFormLayoutData.MAP_DEFINITION,
			Hibernate3ListFormLayoutData.LIST_DEFINITION,
			Hibernate3ListFormLayoutData.ARRAY_DEFINITION,
			Hibernate3SetFormLayoutData.SET_DEFINITION,
			Hibernate3SetFormLayoutData.BAG_DEFINITION,

			Hibernate3AnyFormLayoutData.ANY_DEFINITION,
			Hibernate3PropertyFormLayoutData.PROPERTY_DEFINITION,
			Hibernate3ManyToOneFormLayoutData.MANY_TO_ONE_DEFINITION,
			Hibernate3OneToOneFormLayoutData.ONE_TO_ONE_DEFINITION,
			Hibernate3MetaFormLayoutData.META_FOLDER_DEFINITION,
			Hibernate3MetaFormLayoutData.TUPLIZER_FOLDER_DEFINITION,
			Hibernate3ComponentFormLayoutData.COMPONENT_DEFINITION,
			Hibernate3ComponentFormLayoutData.DYNAMIC_COMPONENT_DEFINITION,
			
			Hibernate3ElementFormLayoutData.ELEMENT_DEFINITION,
			Hibernate3ElementFormLayoutData.MANY_TO_MANY_DEFINITION,
			Hibernate3ElementFormLayoutData.MANY_TO_ANY_DEFINITION,
			Hibernate3CompositeElementFormLayoutData.ELEMENT_DEFINITION,
			Hibernate3CompositeElementFormLayoutData.NESTED_ELEMENT_DEFINITION,
			
			Hibernate3IdFormLayoutData.COMPOSITE_ID_DEFINITION,
			Hibernate3IdFormLayoutData.COMPOSITE_INDEX_DEFINITION,
			Hibernate3IdFormLayoutData.COLLECTION_ID_DEFINITION,
			Hibernate3KeyFormLayoutData.KEY_DEFINITION,
			Hibernate3KeyFormLayoutData.KEY_MANY_TO_ONE_DEFINITION,
			Hibernate3KeyFormLayoutData.KEY_PROPERTY_DEFINITION,
			Hibernate3KeyFormLayoutData.MAP_KEY_DEFINITION,
			Hibernate3KeyFormLayoutData.MAP_KEY_MANY_TO_MANY_DEFINITION,
			Hibernate3KeyFormLayoutData.COMPOSITE_MAP_KEY_DEFINITION,
			Hibernate3KeyFormLayoutData.INDEX_DEFINITION,
			Hibernate3KeyFormLayoutData.LIST_INDEX_DEFINITION,
			
			Hibernate3FormulaFormLayoutData.FORMULA_DEFINITION,
						
			new FormData(
				"Types",
				"", //"Description
				"Hibernate3TypedefFolder",
				new FormAttributeData[]{new FormAttributeData("name", 30, "name"), new FormAttributeData("class", 70, "class")},
				new String[]{"Hibernate3Typedef"},
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddTypedef")
			),
			new FormData(
				"Imports",
				"", //"Description
				"Hibernate3ImportFolder",
				new FormAttributeData[]{new FormAttributeData("class", 60, "class"), new FormAttributeData("rename", 40, "rename")},
				new String[]{"Hibernate3Import"},
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddTypedef")
			),
			new FormData(
				"Classes",
				"", //"Description
				"Hibernate3ClassFolder",
				new FormAttributeData[]{new FormAttributeData("name", 100, "class name")},
				Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute("Hibernate3ClassFolder", "name"),
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyClass")
			),
			new FormData(
				"Queries",
				"", //"Description
				"Hibernate3QueryFolder",
				new FormAttributeData[]{new FormAttributeData("name", 30, "name"), new FormAttributeData("query", 70, "query")},
				new String[]{"Hibernate3Query", "Hibernate3SQLQuery"},
				Hibernate3FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyQuery")
			),
			
			Hibernate3FilterFormLayoutData.FILTER_FOLDER_DEFINITION,
			Hibernate3FilterFormLayoutData.FILTERDEF_FOLDER_DEFINITION,

			Hibernate3SQLQueryFormLayoutData.SQL_QUERY_DEFINITION,
			Hibernate3SQLQueryFormLayoutData.RESULT_SET_DEFINITION,
			Hibernate3FilterFormLayoutData.FILTERDEF_DEFINITION,
			Hibernate3FilterFormLayoutData.TYPEDEF_DEFINITION,

			Hibernate3FormLayoutDataUtil.createAllChildrenFormData("Properties", "Hibernate3AttributesCFolder", null, "name", "CreateActions.AddAttribute"),
			Hibernate3FormLayoutDataUtil.createAllChildrenFormData("Properties", "Hibernate3AttributesFolder",  null, "name", "CreateActions.AddAttribute"),
			Hibernate3FormLayoutDataUtil.createAllChildrenFormData("Properties", "Hibernate3AttributesJFolder", null, "name", "CreateActions.AddAttribute"),
			Hibernate3FormLayoutDataUtil.createAllChildrenFormData("Properties", "Hibernate3AttributesPFolder", null, "name", "CreateActions.AddAttribute"),
			Hibernate3FormLayoutDataUtil.createAllChildrenFormData("Properties", "Hibernate3AttributesNestedFolder", null, "name", "CreateActions.AddAttribute"),
			
			Hibernate3DatabaseObjectFormLayoutData.DATABASE_OBJECT_CD_DEFINITION,
			Hibernate3DatabaseObjectFormLayoutData.DATABASE_OBJECT_DEF_DEFINITION,
			Hibernate3DatabaseObjectFormLayoutData.DATABASE_FOLDER_DEFINITION,
			
			
			HibConfig3FileFormLayoutData.FILE_FORM_DEFINITION,
			HibConfig3SessionFormLayoutData.SESSION_FACTORY_FORM_DEFINITION,
			HibConfig3PropertyFormLayoutData.PROPERTY_FOLDER_DEFINITION,
			HibConfig3MappingFormLayoutData.MAPPING_FOLDER_DEFINITION,
			HibConfig3CacheFormLayoutData.CACHE_FOLDER_DEFINITION,
			HibConfig3EventFormLayoutData.EVENT_FOLDER_DEFINITION,
			HibConfig3EventFormLayoutData.EVENT_DEFINITION,
			HibConfig3EventFormLayoutData.LISTENER_FOLDER_DEFINITION,
			
	};
	
	private static Map FORM_LAYOUT_DEFINITION_MAP = Collections.unmodifiableMap(new ArrayToMap(FORM_LAYOUT_DEFINITIONS));
	
	static Hibernate3FormLayoutData INSTANCE = new Hibernate3FormLayoutData();
	
	public static IFormLayoutData getInstance() {
		return INSTANCE;
	}
	
	public Hibernate3FormLayoutData() {}

	public IFormData getFormData(String entityName) {
		return (IFormData)FORM_LAYOUT_DEFINITION_MAP.get(entityName);
	}

}
