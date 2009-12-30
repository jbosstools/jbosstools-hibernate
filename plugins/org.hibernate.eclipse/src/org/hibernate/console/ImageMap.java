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
		declareRegistryImage(CLOSE_ALL, CLOSE_ALL_PATH);
		declareRegistryImage(CLOSE_ALL_DISABLED, CLOSE_ALL_DISABLED_PATH);
		declareRegistryImage(CLEAR, CLEAR_PATH);
		declareRegistryImage(CONFIGURATION, "images/hibernate_config.gif"); //$NON-NLS-1$
		declareRegistryImage(ADD, "images/treeplus.gif"); //$NON-NLS-1$
		declareRegistryImage(REMOVE, "images/treeminus.gif"); //$NON-NLS-1$
        declareRegistryImage(NEW_WIZARD, "images/newhibernate_wiz.gif"); //$NON-NLS-1$
        declareRegistryImage(TABLE, "images/table.gif"); //$NON-NLS-1$
        declareRegistryImage(COLUMN, "images/columns.gif"); //$NON-NLS-1$
        declareRegistryImage(SCHEMA, "images/schema.gif"); //$NON-NLS-1$
        declareRegistryImage(CATALOG, "images/catalog.gif"); //$NON-NLS-1$
        declareRegistryImage(DATABASE, "images/database.gif"); //$NON-NLS-1$
        declareRegistryImage(FORMAT_QL, "images/format.gif"); //$NON-NLS-1$
        declareRegistryImage(HQL_EDITOR, "images/hql_editor.gif"); //$NON-NLS-1$
        declareRegistryImage(CRITERIA_EDITOR, "images/criteria_editor.gif"); //$NON-NLS-1$
        declareRegistryImage(PARAMETER, "images/parameter.gif"); //$NON-NLS-1$
        declareRegistryImage(NEW_PARAMETER, "images/new_param.gif"); //$NON-NLS-1$
        declareRegistryImage(IGNORE_PARAMETER, "images/ignoreparameter.gif"); //$NON-NLS-1$
        declareRegistryImage(LAYOUT, "images/layout.gif"); //$NON-NLS-1$
        declareRegistryImage(LAYOUT_DISABLED, "images/layout_disabled.gif"); //$NON-NLS-1$
        declareRegistryImage(MINI_HIBERNATE, "images/hicon.gif"); //$NON-NLS-1$
        declareRegistryImage(HIBERNATE_LOGO, "images/hibernate.gif"); //$NON-NLS-1$
        declareRegistryImage(JBOSS_LOGO, "images/jboss.gif"); //$NON-NLS-1$
        declareRegistryImage(FUNCTION, "images/function.gif"); //$NON-NLS-1$
        declareRegistryImage(CHECKBOX_EMPTY, "images/xpl/incomplete_tsk.gif"); //$NON-NLS-1$
        declareRegistryImage(CHECKBOX_FULL, "images/xpl/complete_tsk.gif"); //$NON-NLS-1$
        declareRegistryImage(RELOAD, "images/reload.gif"); //$NON-NLS-1$
        declareRegistryImage(ERROR, "images/error.gif"); //$NON-NLS-1$
		declareRegistryImage(PINUP, PINUP_PATH);
		declareRegistryImage(PINDOWN, PINDOWN_PATH);

	}

	protected abstract void declareRegistryImage(String key, String path);
}
