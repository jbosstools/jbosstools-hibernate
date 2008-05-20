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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IJoinMapping;
import org.jboss.tools.hibernate.core.hibernate.IMetaAttribute;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.internal.core.CompoundIterator;
import org.jboss.tools.hibernate.internal.core.PersistentClass;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.ComponentMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.PropertyMappingForComponentMapingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * The mapping for a component, composite element,
 * composite identifier, etc.
 */
public class ComponentMapping extends SimpleValueMapping implements IComponentMapping {
	private static final long serialVersionUID = 1L;
	private IPersistentClass componentClass;
	private ArrayList<IPropertyMapping> properties = new ArrayList<IPropertyMapping>();
	private String componentClassName;
	private boolean embedded;
	private String parentProperty;
	private IHibernateClassMapping owner;
	private boolean dynamic;
	private Map metaAttributes;
	
// added by yk 13.07.2005
	private boolean properties_component;		// is component properties;
// added by yk 13.07.2005 stop

	
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping#clear()
	 */
	public void clear() {
		super.clear();
		//all properties should be cleaned by super.clear()
		properties.clear();
	}
	public int getPropertySpan() {
		return properties.size();
	}
	public Iterator<IPropertyMapping> getPropertyIterator() {
		return properties.iterator();
	}
	public void addProperty(IPropertyMapping p) {
		if (p == null)
            return ;
        
        properties.add(p);
        p.setPropertyMappingHolder(this);
    }
//akuzmin 17.05.2005	
	public void removeProperty(IPropertyMapping p) {
		if (properties.remove(p))
		{
            if (p != null)
                p.setPropertyMappingHolder(null);
        }
        else
        {
            Iterator props = getPropertyIterator();
            
            while (props.hasNext())
            {
                Object o = props.next();
                if (o instanceof IPropertyMapping) {
                    IPropertyMapping pm = (IPropertyMapping) o;
                    if (pm.getPersistentValueMapping() instanceof ComponentMapping) {
                        ComponentMapping cmp = (ComponentMapping) pm.getPersistentValueMapping();
                        cmp.removeProperty(p);
                    }
                }
            }
        }
    }

	public void renameProperty(IPropertyMapping prop, String newName) {
		((PropertyMapping)prop).setName(newName);
    }
	
	public void addColumn(Column column) {
		throw new UnsupportedOperationException("Cant add a column to a component");
	}
//akuzmin 14.04.2005	
	public void removeColumn(IDatabaseColumn column) {
//		throw new UnsupportedOperationException("Cant remove a column from a component");
		Iterator propiterator = getPropertyIterator();
		SimpleValueMapping maping=null;
		while (propiterator.hasNext())
		{
			PropertyMapping pm=(PropertyMapping) propiterator.next();
			if (pm.getValue() instanceof SimpleValueMapping)
			{
				if (((SimpleValueMapping)pm.getValue()).getConstraintColumns().contains(column))
				{
					maping=(SimpleValueMapping)pm.getValue();
					break;
				}
			}
		}
			if (maping!=null) maping.removeColumn(column);
	}

	//akuzmin 05.05.2005
	public String getMappingColumn() {
		return "";
	}
	
	//akuzmin 05.05.2005
	public void setMappingColumn(String keyColumn)
	{
		throw new UnsupportedOperationException("Cant add a column to a component");
	}
	
	
	public Iterator<IDatabaseColumn> getColumnIterator() {
		ArrayList<Iterator<IDatabaseColumn>> list = new ArrayList<Iterator<IDatabaseColumn>>(getPropertySpan());
		Iterator iter = getPropertyIterator();
		while ( iter.hasNext() ) {
			//akuzmin 13.04.05
			PropertyMapping prop=(PropertyMapping) iter.next();
			if (prop.getValue()!=null)
			list.add(prop.getColumnIterator());
		}
		return new CompoundIterator<IDatabaseColumn>(list);
	}

	
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping#getColumnSpan()
	 * akuzmin@exadel.com
	 * 04.07.2005
	 */
	public int getColumnSpan() {
		Iterator iter = getPropertyIterator();
		int i=0;
		while ( iter.hasNext() ) {
			PropertyMapping prop=(PropertyMapping) iter.next();
			if (prop.getValue()!=null)
			i+= prop.getColumnSpan();
		}
		return i;
	}
	public ComponentMapping(IHibernateClassMapping owner) {
		super( owner.getDatabaseTable() );
		this.owner = owner;
	}

	public ComponentMapping(IJoinMapping join) {
		super( join.getTable() );
		this.owner = join.getPersistentClass();
	}

	public ComponentMapping(IDatabaseTable table) {
		super(table);
		this.owner = null;
	}
	public ComponentMapping(CollectionMapping collection)  {
		super( collection.getCollectionTable() );
		this.owner = collection.getOwner();
	}

	public boolean isEmbedded() {
		return embedded;
	}

	public String getComponentClassName() {
		return componentClassName;
	}


	public IHibernateClassMapping getOwner() {
		return owner;
	}

	public String getParentProperty() {
		return parentProperty;
	}

	public void setComponentClassName(String componentClass) {
 		// added by Nick 29.08.2005
	    boolean isInitialNull = this.componentClassName == null;
        // by Nick
       
        this.componentClassName = componentClass;
// added by yk 29.06.2005    --298--
		if( (owner.getStorage() != null)  && (this.componentClassName != null) 

                // added by Nick 29.08.2005
		        && !isInitialNull
                // by Nick
        )
		{// reload class of component;
			this.componentClass = null;
			// #deleted# by Konstantin Mishin on 19.12.2005 fixed for ESORM-436
			//this.properties.clear();
			// #deleted#
			getComponentClass();
		}
// added by yk 29.06.2005 stop
	}

	public void setEmbedded(boolean embedded) {
		this.embedded = embedded;
	}

	public void setOwner(ClassMapping owner) {
		this.owner = owner;
	}

	public void setParentProperty(String parentProperty) {
		this.parentProperty = parentProperty;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public java.util.Map getMetaAttributes() {
		return metaAttributes;
	}
	public IMetaAttribute getMetaAttribute(String attributeName) {
		return (IMetaAttribute) metaAttributes.get(attributeName);
	}

	public void setMetaAttributes(java.util.Map metas) {
		this.metaAttributes = metas;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitComponentMapping(this,argument);
		return visitor.visitPersistentValueMapping(this,argument);
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping#isSimpleValue()
	 */
	public boolean isSimpleValue() {
		return false;
	}
	
//	akuzmin 17/05/2005	
	public IPropertySource2 getPropertySource()
	{
		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
		bp.setPropertyDescriptors(getPropertyDescriptorHolder());		
		return bp;
	}
//	akuzmin 17/05/2005
	public PropertyDescriptorsHolder getPropertyDescriptorHolder() {
		return ComponentMappingDescriptorsHolder.getInstance(this.getOwner().getPersistentClass().getProjectMapping());
	}
//	akuzmin 09/06/2005
	public PropertyDescriptorsHolder getPropertyMappingDescriptorHolder() {
		return PropertyMappingForComponentMapingDescriptorsHolder.getInstance();
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.hibernate.IComponentMapping#getComponentClass()
	 */
	public IPersistentClass getComponentClass() {
		if(componentClass==null){
 			PersistentClass pc=new PersistentClass();
			pc.setName(componentClassName);
			pc.setProjectMapping(owner.getStorage().getProjectMapping());
			componentClass=pc;
            // added by Nick 15.07.2005
            componentClass.setPersistentClassMapping(owner);
            // by Nick
        }
		return componentClass;
	}
// added by yk 13.07.2005
	public boolean isProperties_component() {
		return properties_component;
	}
	public void setProperties_component(boolean properties_component) {
		this.properties_component = properties_component;
	}
// added by yk 13.07.2005 stop
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.hibernate.IPropertyMappingHolder#moveProperty(org.jboss.tools.hibernate.core.hibernate.IPropertyMapping, org.jboss.tools.hibernate.core.hibernate.IPropertyMapping)
     */
    
}
