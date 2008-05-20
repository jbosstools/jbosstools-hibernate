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
package org.jboss.tools.hibernate.view.views;

import java.sql.Types;
import java.util.ResourceBundle;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.viewers.ContentViewer;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseConstraint;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.INamedQueryMapping;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.ArrayMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMappingHelper;

/**
 * @author Tau from Minsk
 *
 */
public class OrmModelNameVisitor implements IOrmModelVisitor {
	
	private ContentViewer viewer;

	static private String SPACE = " ";

	static private String POINTER = " -> ";

	// add 16.03.2005 tau
	private ResourceBundle BUNDLE = ResourceBundle
			.getBundle(OrmModelNameVisitor.class.getPackage().getName()
					+ ".views");

	public OrmModelNameVisitor(ContentViewer viewer) {
		super();
		this.viewer = viewer;
	}

	/**
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitOrmModel(org.jboss.tools.hibernate.core.IOrmProject, java.lang.Object)
	 */
	public Object visitOrmProject(IOrmProject schema, Object argument) {
		return schema.getName();
	}

	/**
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitDatabaseSchema(org.jboss.tools.hibernate.core.IDatabaseSchema, java.lang.Object)
	 */
	public Object visitDatabaseSchema(IDatabaseSchema schema, Object argument) {
		if (schema.getName() == null || schema.getName().trim().length() == 0) {
			return BUNDLE
					.getString("OrmModelNameVisitor.DefaultDatabaseSchema");
		} else {
			return schema.getName();
		}
	}

	/**
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitDatabaseTable(org.jboss.tools.hibernate.core.IDatabaseTable, java.lang.Object)
	 */
	public Object visitDatabaseTable(IDatabaseTable table, Object argument) {
		// Table name (Class1, Class2 ...)
		// add tau 13.04.2005
		// edit tau 28.04.2005 -> vs ()
		StringBuffer name = new StringBuffer();
		name.append(table.getName());

		IPersistentClassMapping[] classMappings = table
				.getPersistentClassMappings();
		if (classMappings.length != 0) {
			name.append(POINTER);
			for (int i = 0; i < classMappings.length; i++) {
				IPersistentClass persistentClass = classMappings[i]
						.getPersistentClass();
				if (persistentClass != null) {
					name.append(persistentClass.getName());
					name.append(BUNDLE.getString("OrmModelNameVisitor.Comma"));
					name.append(" ");
				}
			}
			name.delete(name.length() - 2, name.length());
		}
		return name.toString();

	}

	/**
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitDatabaseColumn(org.jboss.tools.hibernate.core.IDatabaseColumn, java.lang.Object)
	 */
	public Object visitDatabaseColumn(IDatabaseColumn column, Object argument) {
		// update tau 16.03.2005
		StringBuffer name = new StringBuffer();
		name.append(column.getName());
		//by Nick 22.04.2005

		int length = -1;
		int scale = -1;

		if (!column.isNativeType()) {
			int typeCode = column.getSqlTypeCode();
			String typeName = column.getSqlTypeName();

			if (typeName == null)
				typeCode = Types.NULL;

			//by Nick

			// (tau->tau) Column types should be shown in the following manner:
			/* 
			 
			 Character types: 
			 VARCHAR(length) 
			 CHAR(length) 
			 
			 Numeric types: 
			 NUMBER(length, precision) 
			 NUMERIC(length, precision) 
			 
			 Other types: 
			 BIT 
			 INTEGER 
			 BIGNINT 
			 DATE 
			 FLOAT 
			 REAL 
			 CLOB 
			 BINARY 
			 etc. */
			//by Nick 22.04.2005
			// TODO (tau->tau)
			// testing for ORACLE
			// edit tau 28.04.2005 -> vs ()
			switch (typeCode) {
			case Types.VARCHAR:
			case Types.CHAR:
			case Types.NUMERIC:
			case Types.DECIMAL: //8.07.2005 by Nick DECIMAL JDBC type denotes Oracle NUMBER type    
				//changed by Nick 10.05.2005 - fixes "->" in SQL types
				if (typeCode == Types.NUMERIC || typeCode == Types.DECIMAL) {
					length = column.getPrecision();
					scale = column.getScale();
				} else {
					length = column.getLength();
				}

			default:
				break;

			}
		} else {
			if (column.getLength() > Column.DEFAULT_LENGTH) {
				length = column.getLength();
			} else if (column.getPrecision() > Column.DEFAULT_PRECISION
					|| column.getScale() > Column.DEFAULT_SCALE) {
				length = column.getPrecision() > Column.DEFAULT_PRECISION ? (column
						.getPrecision())
						: (column.getScale() > 0 ? column.getScale() : 1);
				scale = column.getScale();
			}
		}

		StringBuffer typeName = new StringBuffer(column.getSqlTypeName());

		//by Nick

		// (tau->tau) Column types should be shown in the following manner:
		/* 
		 
		 Character types: 
		 VARCHAR(length) 
		 CHAR(length) 
		 
		 Numeric types: 
		 NUMBER(length, precision) 
		 NUMERIC(length, precision) 
		 
		 Other types: 
		 BIT 
		 INTEGER 
		 BIGNINT 
		 DATE 
		 FLOAT 
		 REAL 
		 CLOB 
		 BINARY 
		 etc. */
		//by Nick 22.04.2005
		/*	
		 switch (key) {
		 case value:
		 
		 break;

		 default:
		 break;
		 }
		 */

		// TODO (tau->tau)
		// testing for ORACLE

		StringBuffer lpBuffer = new StringBuffer();
		//name.append(POINTER);

		if (length > Column.DEFAULT_LENGTH) {
			lpBuffer.append(SPACE);
			lpBuffer.append(BUNDLE
					.getString("OrmModelNameVisitor.OpenBrackets"));
			lpBuffer.append(length);
			if (scale > Column.DEFAULT_SCALE) {
				lpBuffer.append(BUNDLE.getString("OrmModelNameVisitor.Comma"));
				lpBuffer.append(SPACE);
				lpBuffer.append(scale);
			}
			lpBuffer.append(BUNDLE
					.getString("OrmModelNameVisitor.CloseBrackets"));
		}
		//by Nick

		if (typeName.length() != 0) {
			// edit tau 28.04.2005 -> vs ()
			//8.07.2005 by Nick DECIMAL JDBC type denotes Oracle NUMBER type    
			//changed by Nick 10.05.2005 - fixes "->" in SQL types
			name.append(BUNDLE.getString("OrmModelNameVisitor.Colon"));
			name.append(SPACE);
			typeName.append(lpBuffer);
		}

		name.append(typeName);
		//                name.append(SPACE);

		// by Nick
		return name.toString();

	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitDatabaseConstraint(org.jboss.tools.hibernate.core.IDatabaseConstraint, java.lang.Object)
	 */
	public Object visitDatabaseConstraint(IDatabaseConstraint constraint,
			Object argument) {
		return constraint.getName();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitPackage(org.jboss.tools.hibernate.core.IPackage, java.lang.Object)
	 */
	public Object visitPackage(IPackage pakage, Object argument) {
		if (pakage.getName() == null || pakage.getName().trim().length() == 0) {
			return BUNDLE.getString("OrmModelNameVisitor.DefaultPackageName");
		} else {
			//return pakage.getName();
			return pakage.getProjectQualifiedName();
		}
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitMapping(org.jboss.tools.hibernate.core.IMapping, java.lang.Object)
	 */
	public Object visitMapping(IMapping mapping, Object argument) {
		return mapping.getName();
	}

	/**
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitMappingStorage(org.jboss.tools.hibernate.core.IMappingStorage, java.lang.Object)
	 */
	public Object visitMappingStorage(IMappingStorage storage, Object argument) {
		return storage.getName();
	}

	/**
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitPersistentClass(org.jboss.tools.hibernate.core.IPersistentClass, java.lang.Object)
	 */
	public Object visitPersistentClass(IPersistentClass clazz, Object argument) {

		StringBuffer name = new StringBuffer();
		if (((OrmContentProvider) viewer.getContentProvider()).getTip() == OrmContentProvider.PACKAGE_CLASS_FIELD_CONTENT_PROVIDER) {
			name.append(clazz.getShortName());
		} else {
			name.append(clazz.getName());
		}

		//edit tau 24.04.2006
		IDatabaseTable table = clazz.getDatabaseTable(); // upd tau 06.06.2005
		//IDatabaseTable table = HibernateAutoMappingHelper.getPrivateTable(classMapping); // upd tau 18.04.2005
		if (table != null) {
			String tableName = table.getName();
			if (tableName != null) {
				//name.append(" (");
				name.append(POINTER);
				name.append(tableName);
				//name.append(")");						
			}
		}

		return name.toString();
	}

	/**
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitPersistentField(org.jboss.tools.hibernate.core.IPersistentField, java.lang.Object)
	 */
	public Object visitPersistentField(IPersistentField field, Object argument) {
		StringBuffer name = new StringBuffer();
		name.append(field.getName());
		name.append(BUNDLE.getString("OrmModelNameVisitor.Colon"));
		String typeString = field.getType();

		if (typeString != null) {
			//added by Nick 31.03.2005
			IPersistentValueMapping value = null;
			if (field.getMapping() != null)
				value = field.getMapping().getPersistentValueMapping();

			//added by Nick 18.04.2005 - to handle BLOB mappings
			if (Signature.getArrayCount(typeString) != 0) {
				// changed by Nick 20.10.2005
				int depth = Signature.getArrayCount(typeString);
				typeString = Signature.getElementType(typeString);
				for (int i = 0; i < depth; i++) {
					typeString += "[]";
				}
				// by Nick
			}

			//by Nick
			if (value != null && value instanceof CollectionMapping
					&& !(value instanceof ArrayMapping)) {
				String elementsClsName = ((CollectionMapping) value)
						.getCollectionElementClassName();
				if (elementsClsName != null)
					typeString = field.getType() + "(" + elementsClsName + ")";
			}

			if (value != null && value instanceof CollectionMapping) {
				//added by Nick 12.05.2005 to show collection table in explorer
				IDatabaseTable collectionTable = ((CollectionMapping) value)
						.getCollectionTable();
				if (collectionTable != null
						&& collectionTable.getShortName() != null)
					typeString += POINTER + collectionTable.getShortName();
				//by Nick
			}
			//by Nick
			name.append(typeString);

			// added by Nick 29.07.2005
			if (field.getGenerifiedTypes() != null) {
				String[] types = field.getGenerifiedTypes();

				StringBuffer buf = new StringBuffer("<");

				for (int i = 0; i < types.length; i++) {
					String string = types[i];
					buf.append(string);

					if (i != types.length - 1)
						buf.append(", ");
				}

				buf.append(">");

				name.append(buf);
			}
			// by Nick
		}
		return name.toString();

	}

	/**
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitPersistentClassMapping(org.jboss.tools.hibernate.core.IPersistentClassMapping, java.lang.Object)
	 */
	public Object visitPersistentClassMapping(IPersistentClassMapping mapping,
			Object argument) {
		// add tau 22.04.2005
		// Class mapping should be shown as Class name -> table name

		StringBuffer name = new StringBuffer();
		name.append(mapping.getName());

		IDatabaseTable table = HibernateAutoMappingHelper
				.getPrivateTable(mapping); // upd tau 22.04.2005
		if (table != null) {
			String tableName = table.getName();
			if (tableName != null) {
				name.append(POINTER);
				name.append(tableName);
			}
		}

		return name.toString();

	}

	/**
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitPersistentFieldMapping(org.jboss.tools.hibernate.core.IPersistentFieldMapping, java.lang.Object)
	 */
	public Object visitPersistentFieldMapping(IPersistentFieldMapping mapping,
			Object argument) {
		return mapping.getName();
	}

	/**
	 * @see org.jboss.tools.hibernate.core.IOrmModelVisitor#visitPersistentValueMapping(org.jboss.tools.hibernate.core.IPersistentValueMapping, java.lang.Object)
	 */
	public Object visitPersistentValueMapping(IPersistentValueMapping mapping,
			Object argument) {
		return mapping.getName();
	}

	// add tau 27.07.2005
	public Object visitNamedQueryMapping(INamedQueryMapping mapping,
			Object argument) {
		return mapping.getName();
	}

}