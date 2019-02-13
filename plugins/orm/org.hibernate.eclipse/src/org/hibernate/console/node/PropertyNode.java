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
package org.hibernate.console.node;

import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;

class PropertyNode extends TypeNode {

	IClassMetadata baseMetaData;
	public PropertyNode(NodeFactory factory, BaseNode parent, int idx, IClassMetadata metadata,Object baseObject, boolean objectGraph) {
        super(factory, parent, metadata.getPropertyTypes()[idx], factory.getMetaData(metadata.getPropertyTypes()[idx].getReturnedClass() ), baseObject, objectGraph);
        name = metadata.getPropertyNames()[idx];
        baseMetaData = metadata;
	}
	
	public Object getValue() {
		if(objectGraph) {
				try {
					if(baseObject==null) {
						return null;
					}
					return baseMetaData.getPropertyValue(baseObject, getName());
				} catch (RuntimeException e) {
					e.printStackTrace();
					return null;
				}
			
		} else {
			return null;
		}
	}
/*
	private void createChildren() {
		System.out.println("Creating children for: " + this);
        int offset=0;
        // Identifier
        if(md.getIdentifierPropertyName()!=null) {
            children.set(0, factory.createIdentifierNode(this, md) );
            offset++;
        }
        
        String[] names = md.getPropertyNames();
        for (int i = 0; i < names.length; i++) {
            Type type = md.getPropertyTypes()[i];
			
            if(type.isPersistentCollectionType() ) {
                PersistentCollectionNode tn = factory.createPersistentCollectionNode(this, names[i], (PersistentCollectionType)type);
                children.set(i+offset, tn);   
            } else {
                children.set(i+offset, factory.createPropertyNode(this, i, md, baseObject, objectGraph) );
            }
            
        }        		
	}	
	

  */  
}
