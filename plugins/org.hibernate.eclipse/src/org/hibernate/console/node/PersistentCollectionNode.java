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

import java.util.Collection;
import java.util.Iterator;

import javax.swing.tree.TreeNode;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleMessages;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

/**
 * @author MAX
 *
 */
public class PersistentCollectionNode extends BaseNode {

	BaseNode virtualNode;
	CollectionType type;
	Type elementType;
	private boolean objectGraph;
	private Object baseObject;
	private Object collectionObject;

	boolean childrenCreated = false;
	private ClassMetadata md;
	public PersistentCollectionNode(NodeFactory factory, BaseNode parent, String name, CollectionType type, ClassMetadata md, CollectionMetadata metadata, Object baseObject, boolean objectGraph) {
		super(factory, parent);
		this.md = md;
		this.type = type;
		this.name = name;
		this.baseObject = baseObject;
		this.objectGraph = objectGraph;



		iconName = factory.getIconNameForType(type);
		this.elementType = metadata.getElementType();
		if(objectGraph) {
			//
		} else {
			virtualNode = factory.createNode(null, elementType.getReturnedClass() );
		}
	}

	Object initCollectionObject() {
		if(collectionObject!=null) return collectionObject;
		try {
			collectionObject = md.getPropertyValue(baseObject, name, EntityMode.POJO);
		} catch (HibernateException e) {
			IllegalArgumentException iae = new IllegalArgumentException(ConsoleMessages.PersistentCollectionNode_could_not_access_property_value);
			iae.initCause(e);
			throw iae;
		}
		return collectionObject;
	}

	public String getHQL() {
		return ""; //$NON-NLS-1$
	}

	public TreeNode getChildAt(int childIndex) {
		checkChildren();
		if(objectGraph) {
			return super.getChildAt(childIndex);
		} else {
			return virtualNode.getChildAt(childIndex);
		}
	}

	public int getChildCount() {
		checkChildren();
		if(objectGraph) {
			return super.getChildCount();
		} else {
			return virtualNode.getChildCount();
		}
	}

	public int getIndex(TreeNode node) {
		checkChildren();
		if(objectGraph) {
			return super.getIndex(node);
		} else {
			return virtualNode.getIndex(node);
		}
	}

	public boolean isLeaf() {
		checkChildren();
		if(objectGraph) {
			return super.isLeaf();
		} else {
			return virtualNode.isLeaf();
		}
	}

	protected void checkChildren() {
		if(!childrenCreated && objectGraph) {
			initCollectionObject();
			int idx = 0;
			if(!type.isArrayType() ) {
				Iterator<?> i = ( (Collection<?>)collectionObject).iterator();

				while (i.hasNext() ) {
					Object element = i.next();

					children.add(createNode(idx++,element, elementType) );
				}
			} else {
				Object[] os = (Object[]) collectionObject;
				for (int i = 0; i < os.length; i++) {
					Object element = os[i];
					children.add(createNode(idx++, element, elementType) );
				}

			}

			childrenCreated = true;
		}

	}

	private BaseNode createNode(int idx, Object element, Type type) { // TODO: use a common way to create these darn nodes!
		return new ClassNode(factory, this,type.getReturnedClass().getName(), factory.getMetaData(type.getReturnedClass() ),element,objectGraph);
	}

	public String renderLabel(boolean b) {
		return getLabel(getName(),b) + " : " + getLabel(type.getReturnedClass().getName(),b) + "<" + getLabel(elementType.getReturnedClass().getName(),b) + ">";  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}

	public Type getType() {
		return type;
	}
}
