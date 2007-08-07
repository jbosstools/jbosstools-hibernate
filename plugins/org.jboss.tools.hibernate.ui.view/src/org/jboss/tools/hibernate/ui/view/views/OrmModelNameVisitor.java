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

import java.sql.Types;
import java.util.ResourceBundle;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.viewers.ContentViewer;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.type.EntityType;
import org.hibernate.type.ManyToOneType;

public class OrmModelNameVisitor /*implements IOrmModelVisitor*/ {
	
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

	public Object visitDatabaseColumn(Column column, Object argument) {
		// update tau 16.03.2005
		StringBuffer name = new StringBuffer();
		name.append(column.getName());
		//by Nick 22.04.2005
		
        int length = -1;
        int scale = -1;
        
//        if (!column.isNativeType())
//        {
//            int typeCode = column.getSqlTypeCode();
//            String typeName = column.getSqlTypeName();
//            
//            if (typeName == null) typeCode = Types.NULL;            
//            
//            //by Nick
//            
//            // (tau->tau) Column types should be shown in the following manner:
//                /* 
//    
//                Character types: 
//                VARCHAR(length) 
//                CHAR(length) 
//                
//                Numeric types: 
//                NUMBER(length, precision) 
//                NUMERIC(length, precision) 
//                
//                Other types: 
//                BIT 
//                INTEGER 
//                BIGNINT 
//                DATE 
//                FLOAT 
//                REAL 
//                CLOB 
//                BINARY 
//                etc. */
//            //by Nick 22.04.2005
//        /*  
//            switch (key) {
//            case value:
//                
//                break;
//
//            default:
//                break;
//            }
//            */
//            
//            // TODO (tau->tau)
//            // testing for ORACLE
//            
//            // edit tau 28.04.2005 -> vs ()
//            
//            switch (typeCode) {
//            case Types.VARCHAR:
//            case Types.CHAR:
//            case Types.NUMERIC:
//            case Types.DECIMAL: //8.07.2005 by Nick DECIMAL JDBC type denotes Oracle NUMBER type    
//                //changed by Nick 10.05.2005 - fixes "->" in SQL types
//                if (typeCode == Types.NUMERIC || typeCode == Types.DECIMAL) {
//                    length = column.getPrecision();
//                    scale = column.getScale();
//                }
//                else
//                {
//                    length = column.getLength();
//                }
//            
//            default:
//                break;
//            
//            }
//        }
//        else
//        {
//            if (column.getLength() > Column.DEFAULT_LENGTH)
//            {
//                length = column.getLength();
//            }
//            else if (column.getPrecision() > Column.DEFAULT_PRECISION || column.getScale() > Column.DEFAULT_SCALE)
//            {
//                length = column.getPrecision() > Column.DEFAULT_PRECISION ? (column.getPrecision()) : (column.getScale() > 0 ? column.getScale() : 1);
//                scale = column.getScale();
//            }
//        }
        
//		StringBuffer typeName = new StringBuffer(column.getSqlTypeName());
//		
//		//by Nick
//		
//		// (tau->tau) Column types should be shown in the following manner:
//			/* 
//
//			Character types: 
//			VARCHAR(length) 
//			CHAR(length) 
//			
//			Numeric types: 
//			NUMBER(length, precision) 
//			NUMERIC(length, precision) 
//			
//			Other types: 
//			BIT 
//			INTEGER 
//			BIGNINT 
//			DATE 
//			FLOAT 
//			REAL 
//			CLOB 
//			BINARY 
//			etc. */
//		//by Nick 22.04.2005
//	/*	
//		switch (key) {
//		case value:
//			
//			break;
//
//		default:
//			break;
//		}
//		*/
//		
//		// TODO (tau->tau)
//		// testing for ORACLE
//		
//		
//            StringBuffer lpBuffer = new StringBuffer();
//            //name.append(POINTER);
//            
//            if (length > Column.DEFAULT_LENGTH)
//            {
//                lpBuffer.append(SPACE);
//                lpBuffer.append(BUNDLE.getString("OrmModelNameVisitor.OpenBrackets"));
//                lpBuffer.append(length);
//                if (scale > Column.DEFAULT_SCALE) 
//                {
//                    lpBuffer.append(BUNDLE.getString("OrmModelNameVisitor.Comma"));
//                    lpBuffer.append(SPACE);
//                    lpBuffer.append(scale);
//                }
//                lpBuffer.append(BUNDLE.getString("OrmModelNameVisitor.CloseBrackets"));
//            }
//            //by Nick
//        
//            if (typeName.length() != 0)
//            {
//                // edit tau 28.04.2005 -> vs ()
//                //8.07.2005 by Nick DECIMAL JDBC type denotes Oracle NUMBER type    
//                //changed by Nick 10.05.2005 - fixes "->" in SQL types
//                name.append(BUNDLE.getString("OrmModelNameVisitor.Colon"));
//                name.append(SPACE);             
//                typeName.append(lpBuffer);
//            }
//            
//            name.append(typeName);              
////            name.append(SPACE);
//            
//		// by Nick
		return name.toString();

	}

	public Object visitPersistentClass(RootClass clazz, Object argument) {

		StringBuffer name = new StringBuffer();
//		if (((OrmContentProvider) viewer.getContentProvider()).getTip() == OrmContentProvider.PACKAGE_CLASS_FIELD_CONTENT_PROVIDER) {
			name.append(clazz.getEntityName() != null ? clazz.getEntityName() : clazz.getClassName());
//		} else {
//			name.append(clazz.getEntityName());
//		}

		//edit tau 24.04.2006
		Table table = clazz.getTable(); // upd tau 06.06.2005
		//IDatabaseTable table = HibernateAutoMappingHelper.getPrivateTable(classMapping); // upd tau 18.04.2005
		if (table != null) {
			String tableName = HibernateUtils.getTableName(table);
			if (tableName != null) {
				//name.append(" (");
				name.append(POINTER);
				name.append(tableName);
				//name.append(")");						
			}
		}

		return name.toString();
	}

	public Object visitTable(Table table, Object argument) {
		StringBuffer name = new StringBuffer();
		name.append(HibernateUtils.getTableName(table));
		return name.toString();
	}

	public Object visitPersistentClass(Subclass clazz, Object argument) {

		StringBuffer name = new StringBuffer();
		name.append(clazz.getEntityName());

		Table table = clazz.getTable();
		if (table != null) {
			String tableName = HibernateUtils.getTableName(table);
			if (tableName != null) {
				name.append(POINTER);
				name.append(tableName);
			}
		}

		return name.toString();
	}

	public Object visitPersistentField(Property field, Object argument) {
		StringBuffer name = new StringBuffer();
		name.append(field.getName());
		name.append(BUNDLE.getString("OrmModelNameVisitor.Colon"));
		String typeString = null;
		if (field.getType().isEntityType()) {
			typeString =  ((EntityType)field.getType()).getAssociatedEntityName();
		} else {
			typeString = field.getType().getReturnedClass().getName();
		}
		if (typeString != null) {
			name.append(typeString);
		}
		
		return name.toString();

	}

	public Object visitCollectionKeyMapping(DependantValue mapping,	Object argument) {
		return "key";
	}

	public Object visitComponentMapping(Component mapping,	Object argument) {
		return "element";
	}
}