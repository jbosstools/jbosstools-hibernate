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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.console.ConsoleMessages;
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

		List<BaseNode> parents = new ArrayList<BaseNode>();

        BaseNode currentParent = this;
          while (currentParent != null && !(currentParent instanceof ConfigurationEntitiesNode) ) {
                      parents.add(currentParent);
                      currentParent = currentParent.parent;
             }

        if(currentParent instanceof ConfigurationEntitiesNode) {
            currentParent = parents.get(parents.size()-1);
          }

        // currentParent is the root
        String cname = ( (ClassNode)currentParent).md.getMappedClass(EntityMode.POJO).getName();

		if (cname.lastIndexOf(".") != -1) { //$NON-NLS-1$
			cname = cname.substring(cname.lastIndexOf(".") + 1); //$NON-NLS-1$
		}
		String alias = cname.toLowerCase();

		String path = ""; //$NON-NLS-1$
		for (int i = parents.size()-2; i >= 0; i--) {
			path += "." +  parents.get(i).getName(); //$NON-NLS-1$
		}

		return "select " + alias + path + " from " + cname + " as " + alias; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public ClassMetadata getClassMetadata() {
		return md;
	}

	public String renderLabel(boolean fullyQualifiedNames) {
		if(objectGraph) {
			Object o = getValue();
			if(Hibernate.isInitialized(o) ) {
				return super.renderLabel(fullyQualifiedNames) + " = " + o;	 //$NON-NLS-1$
			} else {
				return super.renderLabel(fullyQualifiedNames) + " = " + ConsoleMessages.ClassNode_uninitialized_proxy; //$NON-NLS-1$
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
