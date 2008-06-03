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
package org.jboss.tools.hibernate.internal.core.hibernate;

import java.util.Iterator;

import org.dom4j.Element;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;


public class XMLReadWriteHelper
{
	private HibernateConfiguration config;
	
	
	public XMLReadWriteHelper(){}
	
	public XMLReadWriteHelper(HibernateConfiguration config)
	{
		this.config = config;
	}
	
	public boolean isColumnCompatibleForEasyWriting(Element node, IDatabaseColumn column)
	{
		boolean compatible = false;
		if( (compatible = isCompatibleColumnParam(node, column)))
			compatible = isCompatibleColumnType(column);
		return compatible;
	}
//	 added by yk 29.08.2005.

	private String getAssociatedClassName(IPersistentFieldMapping fieldmapping)
	{
		String name = null;
		if(fieldmapping != null)
		{
			IPersistentField pf = fieldmapping.getPersistentField();
			if(pf != null)
				{name = pf.getType();}
		}
		return name;
	}
	
	private boolean isSimpleValueColumnEasyForWriting(IDatabaseColumn column, SimpleValueMapping mapping)
	{
		boolean compatible = false;
		if(mapping == null) return compatible;
		Type thetype = mapping.getType();
		String name;
		if(thetype != null)
		{
			name = TypeUtils.SQLTypeToName(thetype.getSqlType());
			if(name != null)
				compatible = name.equals(column.getSqlTypeName());
		}
		else
			compatible = true;
		return compatible;
	}
	
	private boolean isManyToOneColumnEasyForWriting(IDatabaseColumn column, ManyToOneMapping mapping)
	{
		boolean compatible = false;
		String name;
		if(mapping == null) return compatible;
		if(column.getPersistentValueMapping() != null && column.getPersistentValueMapping().getTable() != null)
		{
			IDatabaseTableForeignKey fk = column.getPersistentValueMapping().getTable().getForeignKey( mapping.getForeignKeyName() );
			if(fk != null)
			{
				Iterator fkcolumns = fk.getColumnIterator();
				if(fkcolumns.hasNext())
				{
					name = TypeUtils.SQLTypeToName( ((IDatabaseColumn)fkcolumns.next()).getSqlTypeCode() );
					if(name != null)
						compatible = name.equals(column.getSqlTypeName());
				}
			}
			if(column.getPersistentValueMapping() instanceof ManyToManyMapping)
			{
				ManyToManyMapping manytomany = (ManyToManyMapping)mapping; 
				String className = manytomany.getReferencedEntityName();
				if(className==null)
				{		className = getAssociatedClassName(manytomany.getFieldMapping());			}
				if(className!=null){
					IPersistentClassMapping assocclassmapping = config.getClassMapping(className );
					if ( assocclassmapping != null ) 
					{
						IHibernateKeyMapping 	kmap = assocclassmapping.getIdentifier();
						if(kmap != null)
						{
							Iterator itassoc 	= kmap.getColumnIterator();
							if(itassoc.hasNext())
							{
								name = TypeUtils.SQLTypeToName( ((IDatabaseColumn)itassoc.next()).getSqlTypeCode() );
								if(name != null)
									compatible = name.equals(column.getSqlTypeName());
							}
						}
					}
				}
			}
		}
		return compatible;
	}
	private boolean isCompatibleColumnType(IDatabaseColumn column)
	{
		boolean compatible = false;
		if(column == null) return compatible;
		if(column.getPersistentValueMapping() instanceof SimpleValueMapping)
		{
			if(!(compatible = isSimpleValueColumnEasyForWriting(column, (SimpleValueMapping)column.getPersistentValueMapping())))
			{
				if(column.getPersistentValueMapping() instanceof ManyToOneMapping)
				{
					compatible = isManyToOneColumnEasyForWriting(column, (ManyToOneMapping)column.getPersistentValueMapping());
				}
			}
		}
		return compatible;
	}
	
	private boolean isColumnParamCompatibleForManyToOne(IDatabaseColumn column)
	{		return (column.isLengthPrecisionScaleDefaultValues() && !column.isUnique());		}
	
	private boolean isColumnParamCompatibleKeyProperty(IDatabaseColumn column)
	{
		return (column.getPrecision() > 0 	|| 
				column.getScale() > 0 		||
				!column.isNullable() 		|| 
				column.isUnique()) 			? false : true;
	}
	private boolean isColumnParamCompatibleKeyManyToOne(IDatabaseColumn column)
	{
		return (!column.isLengthPrecisionScaleDefaultValues() ||
				!column.isNullable() 		|| 
				column.isUnique()) ? false : true;
	}
	private boolean isColumnParamCompatibleManyToMany(IDatabaseColumn column)
	{
		return(!column.isLengthPrecisionScaleDefaultValues() ||
			  !column.isNullable()) ? false : true;
	}
	private boolean isColumnParamCompatibleKey(IDatabaseColumn column)
	{		return column.isLengthPrecisionScaleDefaultValues();		}
	
	private boolean isColumnParamCompatibleListIndex(IDatabaseColumn column)
	{
		return (!column.isLengthPrecisionScaleDefaultValues() ||
				!column.isNullable() || 
				column.isUnique()) ? false : true;
	}
	private boolean isColumnParamCompatibleMapKey(IDatabaseColumn column)
	{
		return (column.getPrecision() > 0 	|| 
				column.getScale() > 0 		||
				!column.isNullable() 		|| 
				column.isUnique()) 			? false : true;
	}
	private boolean isColumnParamCompatibleIndex(IDatabaseColumn column)
	{
		return (column.getPrecision() > 0 	|| 
				column.getScale() > 0 		||
				!column.isNullable() 		|| 
				column.isUnique()) 			? false : true;
	}
	private boolean isColumnParamCompatibleMapKeyManyToMany(IDatabaseColumn column)
	{
		return (!column.isLengthPrecisionScaleDefaultValues() ||
				!column.isNullable() || 
				column.isUnique()) ? false : true;
	}
	private boolean isColumnParamCompatibleIndexManyToMany(IDatabaseColumn column)
	{
		return (!column.isLengthPrecisionScaleDefaultValues() ||
				!column.isNullable() || 
				column.isUnique()) ? false : true;
	}
	private boolean isColumnParamCompatibleCollectionId(IDatabaseColumn column)
	{
		return (column.getPrecision() > 0 	|| 
				column.getScale() > 0 		||
				!column.isNullable() 		|| 
				column.isUnique()) 			? false : true;
	}
	private boolean isColumnParamCompatibleReturnProperty(IDatabaseColumn column)
	{		return false;		}
	private boolean isColumnParamCompatibleReturnDiscriminator(IDatabaseColumn column)
	{		return false;		}
	private boolean isColumnParamCompatibleReturnScalar(IDatabaseColumn column)
	{		return false;		}
	private boolean isColumnParamCompatibleId(IDatabaseColumn column)
	{
		return (column.getPrecision() > 0 	|| 
				column.getScale() > 0 		||
				column.isUnique()) 			? false : true;
	}
	
	private boolean isCompatibleColumnParam(Element node, IDatabaseColumn column)
	{
		boolean compatible = true;// false;
		String nodename = node.getName();
		if(nodename == null) return false;
		if("many-to-one".equals(nodename))
		{		compatible = isColumnParamCompatibleForManyToOne(column);		}
		if("key-property".equals(nodename))
		{	compatible = isColumnParamCompatibleKeyProperty(column);			}
		if("key-many-to-one".equals(nodename))
		{	compatible = isColumnParamCompatibleKeyManyToOne(column);			}
		if("many-to-many".equals(nodename))
		{	compatible = isColumnParamCompatibleManyToMany(column);			}
		if("key".equals(nodename))
		{	compatible = isColumnParamCompatibleKey(column);					}
		if("list-index".equals(nodename))
		{	compatible = isColumnParamCompatibleListIndex(column);				}
		if("map-key".equals(nodename))
		{	compatible = isColumnParamCompatibleMapKey(column);					}
		if("index".equals(nodename))
		{	compatible = isColumnParamCompatibleIndex(column);					}
		if("map-key-many-to-many".equals(nodename))
		{	compatible = isColumnParamCompatibleMapKeyManyToMany(column);		}
		if("index-many-to-many".equals(nodename))
		{	compatible = isColumnParamCompatibleIndexManyToMany(column);		}
		if("collection-id".equals(nodename))
		{	compatible = isColumnParamCompatibleCollectionId(column);			}
		if("return-property".equals(nodename))
		{	compatible = isColumnParamCompatibleReturnProperty(column);			}
		if("return-discriminator".equals(nodename))
		{	compatible = isColumnParamCompatibleReturnDiscriminator(column);	}
		if("return-scalar".equals(nodename))
		{	compatible = isColumnParamCompatibleReturnScalar(column);			}
		if("id".equals(nodename))
		{	compatible = isColumnParamCompatibleId(column);						}
		return compatible;
	}

}
