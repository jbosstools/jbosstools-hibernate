/*
 * Created on 2004-11-01 by max
 * 
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
        declareRegistryImage(PARAMETER, "images/parameter.gif");
        declareRegistryImage(NEW_PARAMETER, "images/new_param.gif");
        declareRegistryImage(IGNORE_PARAMETER, "images/ignoreparameter.gif");
        declareRegistryImage(LAYOUT, "images/layout.gif");
        declareRegistryImage(LAYOUT_DISABLED, "images/layout_disabled.gif");
        declareRegistryImage(MINI_HIBERNATE, "images/hicon.gif");
        declareRegistryImage(HIBERNATE_LOGO, "images/hibernate.gif");
        declareRegistryImage(JBOSS_LOGO, "images/jboss.gif");
		
	}

	protected abstract void declareRegistryImage(String key, String path);
}
