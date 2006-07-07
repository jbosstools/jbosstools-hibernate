/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.console;


/**
 * Common class to avoid duplicate registration of image registry entries.
 * @author max
 *
 */
public abstract class ImageMap implements ImageConstants {

		
	

	protected void declareImages() {
		declareRegistryImage(MAPPEDCLASS, MAPPEDCLASS_PATH);
		declareRegistryImage(UNMAPPEDCLASS, UNMAPPEDCLASS_PATH);
		declareRegistryImage(PROPERTY, PROPERTY_PATH);
		declareRegistryImage(ONETOMANY, ONETOMANY_PATH);
		declareRegistryImage(MANYTOONE, MANYTOONE_PATH);
		declareRegistryImage(ONETOONE, ONETOONE_PATH);
		declareRegistryImage(ANY, ANY_PATH);
		declareRegistryImage(COMPONENT, COMPONENT_PATH);
		declareRegistryImage(IDPROPERTY, IDPROPERTY_PATH);
		declareRegistryImage(TYPES, TYPES_PATH);
		declareRegistryImage(UNKNOWNPROPERTY, UNKNOWNPROPERTY_PATH);
		declareRegistryImage(EXECUTE, EXECUTE_PATH);
		declareRegistryImage(CLOSE, CLOSE_PATH);
		declareRegistryImage(CLOSE_DISABLED, CLOSE_DISABLED_PATH);
		declareRegistryImage(CLEAR, CLEAR_PATH);
		declareRegistryImage(CONFIGURATION, "images/hibernate_config.gif");
		declareRegistryImage(ADD, "images/treeplus.gif");
		declareRegistryImage(REMOVE, "images/treeminus.gif");
        declareRegistryImage(NEW_WIZARD, "images/newhibernate_wiz.gif");
        declareRegistryImage(TABLE, "images/table.gif");
        declareRegistryImage(COLUMN, "images/columns.gif");
        declareRegistryImage(SCHEMA, "images/schema.gif");
        declareRegistryImage(CATALOG, "images/catalog.gif");
        declareRegistryImage(DATABASE, "images/database.gif");
        declareRegistryImage(FORMAT_QL, "images/format.gif");
        declareRegistryImage(HQL_EDITOR, "images/hql_editor.gif");
        declareRegistryImage(CRITERIA_EDITOR, "images/criteria_editor.gif");
        declareRegistryImage(PARAMETER, "images/parameter.gif");
        declareRegistryImage(NEW_PARAMETER, "images/new_param.gif");
        declareRegistryImage(IGNORE_PARAMETER, "images/ignoreparameter.gif");
        declareRegistryImage(LAYOUT, "images/layout.gif");
        declareRegistryImage(LAYOUT_DISABLED, "images/layout_disabled.gif");
        declareRegistryImage(MINI_HIBERNATE, "images/hicon.gif");
        declareRegistryImage(HIBERNATE_LOGO, "images/hibernate.gif");
        declareRegistryImage(JBOSS_LOGO, "images/jboss.gif");
        declareRegistryImage(FUNCTION, "images/function.gif");
		
	}

	protected abstract void declareRegistryImage(String key, String path);
}
