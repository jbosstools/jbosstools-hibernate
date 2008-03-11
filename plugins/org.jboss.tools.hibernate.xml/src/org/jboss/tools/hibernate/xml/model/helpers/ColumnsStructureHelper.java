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
package org.jboss.tools.hibernate.xml.model.helpers;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;

public class ColumnsStructureHelper {
	public static String ENT_HIBERNATE_COLUMN = "Hibernate3Column";
	
	public static boolean isColumnObject(XModelObject object) {
		return ENT_HIBERNATE_COLUMN.equals(object.getModelEntity().getName());
	}
	
	public static XModelObject newColumn(XModel model, String name) {
		XModelObject c = model.createModelObject(ENT_HIBERNATE_COLUMN, null);
		c.setAttributeValue("name", name);
		return c;		
	}
	
	public static XModelObject newColumn(XModel model, Properties p) {
		return model.createModelObject(ENT_HIBERNATE_COLUMN, p);
	}
	
	/*
	 * Returns true if object has no children tags discribing columns.
	 */
	public static boolean hasNoChildColumns(XModelObject columnOwner) {
		return columnOwner.getChildren(ENT_HIBERNATE_COLUMN).length == 0;
	}
	
	/*
	 * Returns only columns described as children tags.
	 */
	public static XModelObject[] getChildColumns(XModelObject columnOwner) {
		return columnOwner.getChildren(ENT_HIBERNATE_COLUMN);
	}

	/*
	 * Returns column name if object may declare it and has no
	 * children columns, otherwise returns null.
	 */	
	public static String getDeclaredColumnName(XModelObject columnOwner) {
		if(!hasNoChildColumns(columnOwner)) return null;
		String column = columnOwner.getAttributeValue("column");
		if(column == null || column.length() > 0) return column;
		String name = columnOwner.getAttributeValue("name");
		if(name != null && name.length() == 0 && IdStructureHelper.isId(columnOwner)) return "id";
		return name;
	}

	/*
	* Returns null if columnOwner has one or more column children objects or
	* new column object created by column described in the columnOwner.
	*/
	public static XModelObject getDeclaredColumn(XModelObject columnOwner) {
		String name = getDeclaredColumnName(columnOwner);
		if(name == null) return null;
		XModelObject c = newColumn(columnOwner.getModel(), name);
		XAttribute[] as = c.getModelEntity().getAttributes();
		for (int i = 0; i < as.length; i++) {
			String n = as[i].getName();
			if("name".equals(n)) continue;
			String v = columnOwner.getAttributeValue(n);
			if(v == null || v.equals(as[i].getDefaultValue())) continue;
			c.setAttributeValue(n, v);
		}
		return c;
	}
	
	/*
	 * Returns either children columns of the columnOwner or 
	 * new column object created with column described in the columnOwner.
	 */	
	public static XModelObject[] getColumns(XModelObject columnOwner) {
		XModelObject declaredColumn = getDeclaredColumn(columnOwner);
		return (declaredColumn != null) 
		    ? new XModelObject[]{declaredColumn}
			: columnOwner.getChildren(ENT_HIBERNATE_COLUMN);
	}

	/*
	 * Returns true if entity attributes are not sufficient to save 
	 * non-default attributes of the column and entity may have column child.
	 */
	public static boolean isColumnChildRequired(XModelEntity attribute, XModelObject column) {
		if(column == null || attribute.getChild(ENT_HIBERNATE_COLUMN) == null) return false;
		XAttribute[] as = column.getModelEntity().getAttributes();
		for (int i = 0; i < as.length; i++) {
			String n = as[i].getName();
			if("name".equals(n) || "persistent".equals(n)) continue;
			String v = column.getAttributeValue(n);
			if(v == null || v.equals(as[i].getDefaultValue())) continue;
			if(attribute.getAttribute(n) == null) return true;
		}
		return false;
	}
	
	/*
	 * Creates new column with data provided by argument column,
	 * which may be diagram table column object or hibernate column object.
	 */
	public static XModelObject createAttributeColumn(XModelObject column) {
		XAttribute[] as = column.getModelEntity().getAttributes();
		XModelObject c = column.getModel().createModelObject(ENT_HIBERNATE_COLUMN, null);
		for (int i = 0; i < as.length; i++) {
			String n = as[i].getName();
			String v = column.getAttributeValue(n);
			if(v != null && !v.equals(as[i].getDefaultValue())) c.setAttributeValue(n, v);
		}
		return c;
	}
	
	public static void mergeColumnDataToAttribute(XModelObject attribute, XModelObject column) {
		if(column == null || attribute == null) return; 
		if(column.isActive() && attribute.isActive()) column = column.copy();
		XAttribute[] as = column.getModelEntity().getAttributes();
		boolean active = attribute.isActive();
		/// column name must be merged first! (now entity provides for that)
		for (int i = 0; i < as.length; i++) {
			String n = as[i].getName();
			String v = column.getAttributeValue(n);
			if("name".equals(n)) {
				n = "column";
				if(v.equals(attribute.getAttributeValue("name"))) v = "";
			}
			if(v == null) continue;
///			if(!"column".equals(n) && v.equals(as[i].getDefaultValue())) continue;
			if(!active) {
				attribute.setAttributeValue(n, v);
			} else {
				attribute.getModel().changeObjectAttribute(attribute, n, v);
			}				 
		}
	}
	
	public static Set getTableColumns(XModelObject table) {
		Set<String> set = new HashSet<String>();
		if(table == null) return set;
		XModelObject[] cs = table.getChildren();
		for (int i = 0; i < cs.length; i++) set.add(cs[i].getAttributeValue("name"));
		return set;
	}
	
	public static void provideUniqueName(XModelObject column, Set<String> columns) {
		String n = column.getAttributeValue("name");
		if(columns.contains(n)) {
			int i = 1;
			while(columns.contains(n + i)) ++i;
			n = n + i;
			column.setAttributeValue("name", n);
		}
		columns.add(n);
	}

//	public static OrmDiagramColumn findOrCreateDiagramColumn(XModelObject table, String name, XModelObject reference) {
//		XModelObject c = table.getChildByPath(name);
//		if(c == null) {
//			c = table.getModel().createModelObject("OrmDiagramColumn", null);
//			c.setAttributeValue("name", name);
//			table.addChild(c);
//		}
//		OrmDiagramColumn dc = (OrmDiagramColumn)c;
//		dc.setReference(reference);
//		return dc;
//	}
	
	public static void replaceColumnDeclarationWithChild(XModelObject attrReference, Properties p) {
		XModelObject c = newColumn(attrReference.getModel(), p);
		replaceColumnDeclarationWithChild(attrReference, c);
	}

	public static void replaceColumnDeclarationWithChild(XModelObject attrReference, XModelObject column) {
		DefaultCreateHandler.addCreatedObject(attrReference, column, -1);
		XModelObject c = newColumn(attrReference.getModel(), attrReference.getAttributeValue("name"));
		mergeColumnDataToAttribute(attrReference, c);
	}

}
