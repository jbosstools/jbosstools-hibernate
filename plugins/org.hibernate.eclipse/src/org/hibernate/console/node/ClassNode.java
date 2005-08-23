/*
 * Created on 03-08-2003
 *
 */
package org.hibernate.console.node;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.console.ImageConstants;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

/**
 * @author MAX
 *
 */
public class ClassNode extends BaseNode {

	ClassMetadata md;

	boolean objectGraph;
	
	Object baseObject;
	boolean childrenCreated = false;
	public ClassNode(NodeFactory factory, BaseNode parent, String name, ClassMetadata metadata, Object baseObject, boolean objectGraph) {	
		
		super(factory, parent);
        this.name = name;
        this.baseObject = baseObject;
        this.objectGraph  = objectGraph;
    
		md = metadata;
			if (md != null) { // Don't have any hibernate related info about this one...
				iconName = ImageConstants.MAPPEDCLASS;
			} else {
				iconName = ImageConstants.UNMAPPEDCLASS;      
			}		
	}

	protected void checkChildren() {
		if(!childrenCreated) {
			createChildren();
			childrenCreated = true;
		}
	}
	
	protected void createChildren() {
		//System.out.println("Creating children for: " + this);
		
		if(objectGraph && getValue()==null || md == null) {
			return;
		}		
		        
		
        // Identifier
        if(md.getIdentifierPropertyName()!=null) {
            children.add(0, factory.createIdentifierNode(this, md) );            
        }
        
        String[] names = md.getPropertyNames();
        for (int i = 0; i < names.length; i++) {
            Type type = md.getPropertyTypes()[i];
			
            if(type.isCollectionType() ) {
                PersistentCollectionNode tn = factory.createPersistentCollectionNode(this, names[i], md, (CollectionType)type, getValue(), objectGraph);
                children.add(tn);   
            } else {
                children.add(factory.createPropertyNode(this, i, md, getValue(), objectGraph) );
            }            
        }        		
	}	

	public String getHQL() {

		List parents = new ArrayList();
        
        BaseNode currentParent;
          currentParent = this;
          while (currentParent != null && !(currentParent instanceof ConfigurationEntitiesNode) ) {
                      parents.add(currentParent);
                      currentParent = currentParent.parent;
             }
		
        if(currentParent instanceof ConfigurationEntitiesNode) {
            currentParent = (BaseNode) parents.get(parents.size()-1);
          }
        
        // currentParent is the root
        String cname = ( (ClassNode)currentParent).md.getMappedClass(EntityMode.POJO).getName();
        
		if (cname.lastIndexOf(".") != -1) {
			cname = cname.substring(cname.lastIndexOf(".") + 1);
		}
		String alias = cname.toLowerCase();

		String path = "";
		for (int i = parents.size()-2; i >= 0; i--) {
			path += "." + ( (BaseNode) parents.get(i) ).getName();
		}

		return "select " + alias + path + " from " + cname + " as " + alias;
	}
	
	public ClassMetadata getClassMetadata() {
		return md;
	}

	public String renderLabel(boolean fullyQualifiedNames) {
		if(objectGraph) {
			Object o = getValue();
			if(Hibernate.isInitialized(o) ) {
				return super.renderLabel(fullyQualifiedNames) + " = " + o;	
			} else {
				return super.renderLabel(fullyQualifiedNames) + " = " + "(uninitialized proxy)";
			}
			  
		} else {
			return super.renderLabel(fullyQualifiedNames);
		}
	}
	
	public Object getValue() {
		if(objectGraph) {
			return baseObject;
		} else {
			return null;
		}
	}
}
