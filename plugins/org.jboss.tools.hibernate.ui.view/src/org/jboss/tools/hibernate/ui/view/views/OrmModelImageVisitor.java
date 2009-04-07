/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.view.views;

import java.util.ResourceBundle;

import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClassVisitor;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UnionSubclass;
import org.jboss.tools.hibernate.ui.view.ImageBundle;
import org.jboss.tools.hibernate.ui.view.ViewPlugin;

/**
 * @author Tau
 * 
 */
public class OrmModelImageVisitor implements PersistentClassVisitor {

	public Object visitDatabaseTable(Table table) {
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.DatabaseTable")); //$NON-NLS-1$
	}

	public Object visitDatabaseColumn(Column column) {
		if (column.isUnique()) {
			return ViewPlugin.getImageDescriptor(ImageBundle
					.getString("OrmModelImageVisitor.DatabaseUniqueKeyColumn")); //$NON-NLS-1$
		} else if (HibernateUtils.isPrimaryKey(column)
				&& HibernateUtils.getTable(column) != null
				&& HibernateUtils.isForeignKey(column)) {
			return ViewPlugin
					.getImageDescriptor(ImageBundle
							.getString("OrmModelImageVisitor.DatabasePrimaryForeignKeysColumn")); //$NON-NLS-1$
		} else if (HibernateUtils.isPrimaryKey(column)) {
			return ViewPlugin
					.getImageDescriptor(ImageBundle
							.getString("OrmModelImageVisitor.DatabasePrimaryKeyColumn")); //$NON-NLS-1$
		} else if (HibernateUtils.getTable(column) != null
				&& HibernateUtils.isForeignKey(column)) {
			return ViewPlugin
					.getImageDescriptor(ImageBundle
							.getString("OrmModelImageVisitor.DatabaseForeignKeyColumn")); //$NON-NLS-1$
		} else
			return ViewPlugin.getImageDescriptor(ImageBundle
					.getString("OrmModelImageVisitor.DatabaseColumn")); //$NON-NLS-1$

	}

	public Object visitPersistentField(Property field) {
		if (field != null) {
			if (field.getPersistentClass() != null && field.getPersistentClass().getVersion() == field) {
				return ViewPlugin
						.getImageDescriptor(ImageBundle
								.getString("OrmModelImageVisitor.PersistentFieldSimple_version")); //$NON-NLS-1$
			}
			if (field.getPersistentClass() != null && field.getPersistentClass().getIdentifierProperty() == field) {
				return ViewPlugin
						.getImageDescriptor(ImageBundle
								.getString("OrmModelImageVisitor.PersistentFieldSimple_id")); //$NON-NLS-1$
			}
			if (field.getValue() != null) {
				if (field.getValue() instanceof OneToMany)
					return ViewPlugin
							.getImageDescriptor(ImageBundle
									.getString("OrmModelImageVisitor.PersistentFieldOne-to-many")); //$NON-NLS-1$
				else if (field.getValue() instanceof OneToOne)
					return ViewPlugin
							.getImageDescriptor(ImageBundle
									.getString("OrmModelImageVisitor.PersistentFieldOne-to-one")); //$NON-NLS-1$
				else if (field.getValue() instanceof ManyToOne)
					return ViewPlugin
							.getImageDescriptor(ImageBundle
									.getString("OrmModelImageVisitor.PersistentFieldMany-to-one")); //$NON-NLS-1$
				else if (field.getValue() instanceof Any)
					return ViewPlugin
							.getImageDescriptor(ImageBundle
									.getString("OrmModelImageVisitor.PersistentFieldAny")); //$NON-NLS-1$

				try {
					if (field.getType() != null
							&& field.getType().isCollectionType()) {
						if (field.getValue() instanceof PrimitiveArray)
							return ViewPlugin
									.getImageDescriptor(ImageBundle
											.getString("OrmModelImageVisitor.Collection_primitive_array")); //$NON-NLS-1$
						else if (field.getValue() instanceof Array)
							return ViewPlugin
									.getImageDescriptor(ImageBundle
											.getString("OrmModelImageVisitor.Collection_array")); //$NON-NLS-1$
						else if (field.getValue() instanceof List)
							return ViewPlugin
									.getImageDescriptor(ImageBundle
											.getString("OrmModelImageVisitor.Collection_list")); //$NON-NLS-1$
						else if (field.getValue() instanceof Set)
							return ViewPlugin
									.getImageDescriptor(ImageBundle
											.getString("OrmModelImageVisitor.Collection_set")); //$NON-NLS-1$
						else if (field.getValue() instanceof Map)
							return ViewPlugin
									.getImageDescriptor(ImageBundle
											.getString("OrmModelImageVisitor.Collection_map")); //$NON-NLS-1$
						else if (field.getValue() instanceof Bag)
							return ViewPlugin
									.getImageDescriptor(ImageBundle
											.getString("OrmModelImageVisitor.Collection_bag")); //$NON-NLS-1$
						else if (field.getValue() instanceof IdentifierBag)
							return ViewPlugin
									.getImageDescriptor(ImageBundle
											.getString("OrmModelImageVisitor.Collection_idbag")); //$NON-NLS-1$
						else
							return ViewPlugin.getImageDescriptor(ImageBundle
									.getString("OrmModelImageVisitor.Collection")); //$NON-NLS-1$
					}
				} catch (Exception e) {
					return ViewPlugin.getImageDescriptor(ImageBundle
							.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$
				}
			}
			if("parent".equals(field.getName())) //$NON-NLS-1$
				return ViewPlugin.getImageDescriptor(ImageBundle
						.getString("OrmModelImageVisitor.PersistentFieldParent")); //$NON-NLS-1$
			
		}
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.PersistentFieldSimple")); //$NON-NLS-1$
	}

	public Object visitManyToOneMapping(ManyToOne field) {
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.PersistentFieldMany-to-many")); //$NON-NLS-1$
	}

	public Object visitOneToManyMapping(OneToMany field) {
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.PersistentFieldOne-to-many")); //$NON-NLS-1$
	}

	public Object visitSimpleValueMapping(SimpleValue field) {
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.PersistentFieldSimple")); //$NON-NLS-1$
	}

	public Object visitAnyMapping(Any field) {
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.PersistentFieldMany-to-any")); //$NON-NLS-1$
	}

	public Object visitComponentMapping(Component mapping) {
		if (mapping != null) {
			return ViewPlugin
					.getImageDescriptor(ImageBundle
							.getString("OrmModelImageVisitor.PersistentFieldComponent")); //$NON-NLS-1$
		}
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.PersistentFieldNot_mapped")); //$NON-NLS-1$
	}

	public Object visitComponentKeyMapping(DependantValue mapping) {
		if (mapping.getTable().getIdentifierValue() == mapping) {
			return ViewPlugin
					.getImageDescriptor(ImageBundle
							.getString("OrmModelImageVisitor.PersistentFieldComponent_id")); //$NON-NLS-1$				
		}
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.PersistentFieldSimple")); //$NON-NLS-1$
	}

	public Object accept(RootClass arg0) {
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.PersistentClass")); //$NON-NLS-1$
	}

	public Object accept(UnionSubclass arg0) {
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.PersistentClass")); //$NON-NLS-1$
	}

	public Object accept(SingleTableSubclass arg0) {
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.PersistentClass")); //$NON-NLS-1$
	}

	public Object accept(JoinedSubclass arg0) {
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.PersistentClass")); //$NON-NLS-1$
	}

	public Object accept(Subclass arg0) {
		return ViewPlugin.getImageDescriptor(ImageBundle
				.getString("OrmModelImageVisitor.PersistentClass")); //$NON-NLS-1$
	}

}
