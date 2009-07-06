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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.ui.attribute.editor.TableStructuredEditor;
import org.jboss.tools.common.model.ui.forms.FormActionData;
import org.jboss.tools.common.model.ui.forms.FormAttributeData;
import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.IFormActionData;
import org.jboss.tools.common.model.ui.forms.IFormAttributeData;
import org.jboss.tools.common.model.ui.forms.InfoLayoutDataFactory;

/**
 * @author glory
 */
public class Hibernate3FormLayoutDataUtil {
	private final static String STBFE_CLASS_NAME = "org.jboss.tools.common.model.ui.attribute.editor.JavaHyperlinkLineFieldEditor"; //$NON-NLS-1$
	public final static String SBFEE_CLASS_NAME = "org.jboss.tools.common.model.ui.attribute.editor.StringButtonFieldEditorEx"; //$NON-NLS-1$

	private final static String SELECT_IT_ACTION = "%SelectIt%"; //$NON-NLS-1$
	private final static String INTERNAL_ACTION = "%internal%"; //$NON-NLS-1$
	private final static String DEFAULT_DELETE_ACTION = "DeleteActions.Delete"; //$NON-NLS-1$
//	private final static String DEFAULT_EDIT_ACTION = "Properties.Properties";

	/**
	 * 
	 * @param entityName (non-translatable)
	 * @return
	 */
	public static IFormAttributeData[] createGeneralFormAttributeData(String entityName) {
		return createFormAttributeData(entityName, "general"); //$NON-NLS-1$
	}

	/**
	 * 
	 * @param entityName (non-translatable)
	 * @return
	 */
	public static IFormAttributeData[] createAdvancedFormAttributeData(String entityName) {
		return createFormAttributeData(entityName, "advanced"); //$NON-NLS-1$
	}

	/**
	 * 
	 * @param entityName (non-translatable)
	 * @param categoryName
	 * @return
	 */
	public static IFormAttributeData[] createFormAttributeData(String entityName, String categoryName) {
		XModelEntity entity = PreferenceModelUtilities.getPreferenceModel().getMetaData().getEntity(entityName);
		if(entity == null) return new IFormAttributeData[0];
		List<FormAttributeData> list = new ArrayList<FormAttributeData>();
		XAttribute[] as = entity.getAttributes();
		for (int i = 0; i < as.length; i++) {
			if(!as[i].isVisible()) continue;
			String category = as[i].getProperty("category"); //$NON-NLS-1$
			if(category != null && category.equals(categoryName)) {
				String editorName = as[i].getEditor().getName();
				if("AccessibleJava".equals(editorName)) { //$NON-NLS-1$
					list.add(new FormAttributeData(as[i].getName(), null, STBFE_CLASS_NAME));
				} else if("Note".equals(editorName)) { //$NON-NLS-1$
					list.add(new FormAttributeData(as[i].getName(), InfoLayoutDataFactory.getInstance()));
				} else {
					list.add(new FormAttributeData(as[i].getName()));
				}
			}
		}		
		return (IFormAttributeData[])list.toArray(new IFormAttributeData[0]);
	}
	
	/**
	 * 
	 * @param entityName (non-translatable)
	 * @param attributeName (non-translatable)
	 * @return
	 */
	public static String[] getChildEntitiesWithAttribute(String entityName, String attributeName) {
		XModelEntity entity = PreferenceModelUtilities.getPreferenceModel().getMetaData().getEntity(entityName);
		if(entity == null) return new String[0];
		List<String> list = new ArrayList<String>();
		XChild[] cs = entity.getChildren();
		for (int i = 0; i < cs.length; i++) {
			XModelEntity c = entity.getMetaModel().getEntity(cs[i].getName());
			if(c != null && c.getAttribute(attributeName) != null) list.add(c.getName());
		}		
		return list.toArray(new String[0]);
	}

	/**
	 * 
	 * @param name (translatable)
	 * @param entityName (non-translatable)
	 * @param childName (non-translatable)
	 * @param attributeName (non-translatable)
	 * @param createAction (non-translatable)
	 * @return
	 */
	public static FormData createAllChildrenFormData(String name, String entityName, String childName, String attributeName, String createAction) {
		return createChildrenFormData(name, entityName, childName, attributeName,
				Hibernate3FormLayoutDataUtil.getChildEntitiesWithAttribute(entityName, attributeName), createAction);
	}

	/**
	 * 
	 * @param name (translatable)
	 * @param entityName (non-translatable)
	 * @param childName (non-translatable)
	 * @param attributeName (non-translatable)
	 * @param entityNames (non-translatable)
	 * @param createAction (non-translatable)
	 * @return
	 */
	public static FormData createChildrenFormData(String name, String entityName, String childName, String attributeName, String[] entityNames, String createAction) {
		return new FormData(
			name,
			"", //"Description //$NON-NLS-1$
			childName != null ? childName : entityName,
			new FormAttributeData[]{new FormAttributeData(attributeName, 100, attributeName)},
			entityNames,
			Hibernate3FormLayoutDataUtil.createDefaultFormActionData(createAction)
		);
	}

	/**
	 * 
	 * @param actionPath (non-translatable)
	 * @return
	 */
	public static IFormActionData[] createDefaultFormActionData(String actionPath) {
		return new IFormActionData[] {
			new FormActionData(TableStructuredEditor.ADD_ACTION, actionPath),
			new FormActionData(TableStructuredEditor.REMOVE_ACTION, DEFAULT_DELETE_ACTION),
			new FormActionData(TableStructuredEditor.EDIT_ACTION, SELECT_IT_ACTION),
			new FormActionData(TableStructuredEditor.UP_ACTION, INTERNAL_ACTION),
			new FormActionData(TableStructuredEditor.DOWN_ACTION, INTERNAL_ACTION)
		};
	}
	
}
