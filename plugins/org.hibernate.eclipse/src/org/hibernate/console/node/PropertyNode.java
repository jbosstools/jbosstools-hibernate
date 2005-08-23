
package org.hibernate.console.node;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.metadata.ClassMetadata;

public class PropertyNode extends TypeNode {

	ClassMetadata baseMetaData;
	public PropertyNode(NodeFactory factory, BaseNode parent, int idx, ClassMetadata metadata,Object baseObject, boolean objectGraph) {
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
					return baseMetaData.getPropertyValue(baseObject, getName(), EntityMode.POJO);
				} catch (HibernateException e) {
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
