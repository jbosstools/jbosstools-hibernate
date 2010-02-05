/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.view;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.ui.diagram.UiPlugin;

/**
 * Map: ORM object -> Image descriptor 
 */
public class OrmImageMap {
	
	private OrmImageMap() {}

	public static ImageDescriptor getImageDescriptor(final Object obj) {
		String imageName = null;
		if (obj instanceof Table) {
			imageName = getImageName((Table)obj);
		} else if (obj instanceof Column) {
			imageName = getImageName((Column)obj);
		} else if (obj instanceof Property) {
			imageName = getImageName((Property)obj);
		} else if (obj instanceof OneToMany) {
			imageName = getImageName((OneToMany)obj);
		} else if (obj instanceof SimpleValue) {
			imageName = getImageName((SimpleValue)obj);
		} else if (obj instanceof PersistentClass) {
			imageName = getImageName((PersistentClass)obj);
		} else if (obj instanceof String) {
			imageName = "Image_Error"; //$NON-NLS-1$;
		}
		return UiPlugin.getImageDescriptor("images/" + ImageBundle.getString(imageName)); //$NON-NLS-1$
	}

	/**
	 * the image name for hierarchy:
	 * Table
	 * @param table
	 * @return
	 */
	public static String getImageName(Table table) {
		return "Image_DatabaseTable"; //$NON-NLS-1$
	}

	/**
	 * the image name for hierarchy:
	 * Column
	 * @param column
	 * @return
	 */
	public static String getImageName(Column column) {
		String str = "Image_DatabaseColumn"; //$NON-NLS-1$
		final boolean primaryKey = HibernateUtils.isPrimaryKey(column);
		final boolean foreignKey = HibernateUtils.isForeignKey(column);
		final Table table = HibernateUtils.getTable(column);
		if (column.isUnique()) {
			str = "Image_DatabaseUniqueKeyColumn"; //$NON-NLS-1$
		} else if (primaryKey && table != null && foreignKey) {
			str = "Image_DatabasePrimaryForeignKeysColumn"; //$NON-NLS-1$
		} else if (primaryKey) {
			str = "Image_DatabasePrimaryKeyColumn"; //$NON-NLS-1$
		} else if (table != null && foreignKey) {
			str = "Image_DatabaseForeignKeyColumn"; //$NON-NLS-1$
		}
		return str;

	}

	/**
	 * the image name for hierarchy:
	 * Property
	 * @param field
	 * @return
	 */
	public static String getImageName(Property field) {
		String str = "Image_PersistentFieldSimple"; //$NON-NLS-1$
		if (field == null) {
			return str;
		}
		final PersistentClass persistentClass = field.getPersistentClass(); 
		if (persistentClass != null && persistentClass.getVersion() == field) {
			str = "Image_PersistentFieldSimple_version"; //$NON-NLS-1$
		} else if (persistentClass != null && persistentClass.getIdentifierProperty() == field) {
			str = "Image_PersistentFieldSimple_id"; //$NON-NLS-1$
		} else if (field.getValue() != null) {
			final Value value = field.getValue();
			if (value instanceof OneToMany) {
				str = "Image_PersistentFieldOne-to-many"; //$NON-NLS-1$
			} else if (value instanceof OneToOne) {
				str = "Image_PersistentFieldOne-to-one"; //$NON-NLS-1$
			} else if (value instanceof ManyToOne) {
				str = "Image_PersistentFieldMany-to-one"; //$NON-NLS-1$
			} else if (value instanceof Any) {
				str = "Image_PersistentFieldAny"; //$NON-NLS-1$
			} else {
				Type type = null;
				try {
					type = field.getType();
				} catch (Exception ex) {
					// ignore it
				}
				if (type != null && type.isCollectionType()) {
					if (value instanceof PrimitiveArray) {
						str = "Image_Collection_primitive_array"; //$NON-NLS-1$
					} else if (value instanceof Array) {
						str = "Image_Collection_array"; //$NON-NLS-1$
					} else if (value instanceof List) {
						str = "Image_Collection_list"; //$NON-NLS-1$
					} else if (value instanceof Set) {
						str = "Image_Collection_set"; //$NON-NLS-1$
					} else if (value instanceof Map) {
						str = "Image_Collection_map"; //$NON-NLS-1$
					} else if (value instanceof Bag) {
						str = "Image_Collection_bag"; //$NON-NLS-1$
					} else if (value instanceof IdentifierBag) {
						str = "Image_Collection_idbag"; //$NON-NLS-1$
					} else {
						str = "Image_Collection"; //$NON-NLS-1$
					}
				}
			}
		} else if ("parent".equals(field.getName())) { //$NON-NLS-1$
			str = "Image_PersistentFieldParent"; //$NON-NLS-1$
		}
		return str;
	}

	/**
	 * the image name for hierarchy:
	 * OneToMany
	 * @param field
	 * @return
	 */
	public static String getImageName(OneToMany field) {
		return "Image_PersistentFieldOne-to-many"; //$NON-NLS-1$
	}

	/**
	 * the image name for hierarchy:
	 * SimpleValue
	 * |-- Any
	 * |-- Component 
	 * |-- DependantValue
	 * |-- ToOne
	 *     |-- ManyToOne
	 *     |-- OneToOne
	 * @param field
	 * @return
	 */
	public static String getImageName(SimpleValue field) {
		String res = "Image_PersistentFieldSimple"; //$NON-NLS-1$
		if (field instanceof Any) {
			res = "Image_PersistentFieldMany-to-any"; //$NON-NLS-1$
		} else if (field instanceof Component) {
			res = "Image_PersistentFieldComponent"; //$NON-NLS-1$
		} else if (field instanceof DependantValue) {
			DependantValue mapping = (DependantValue)field;
			if (mapping.getTable().getIdentifierValue() == mapping) {
				res = "Image_PersistentFieldComponent_id"; //$NON-NLS-1$				
			}
		} else if (field instanceof ManyToOne) {
			res = "Image_PersistentFieldMany-to-many"; //$NON-NLS-1$
		}
		return res;
	}

	/**
	 * the image name for hierarchy:
	 * PersistentClass
	 * |-- RootClass
	 * |   |-- SpecialRootClass
	 * |
	 * |-- Subclass 
	 *     |-- JoinedSubclass
	 *     |-- SingleTableSubclass
	 *     |-- UnionSubclass
	 * @param persistentClass
	 * @return
	 */
	public static String getImageName(PersistentClass persistentClass) {
		return "Image_PersistentClass"; //$NON-NLS-1$
	}

}
