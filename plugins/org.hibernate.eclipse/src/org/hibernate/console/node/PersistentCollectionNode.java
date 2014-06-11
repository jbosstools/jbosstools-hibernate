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

import org.eclipse.osgi.util.NLS;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleMessages;
import org.hibernate.type.CollectionType;
import org.jboss.tools.hibernate.proxy.TypeProxy;
import org.jboss.tools.hibernate.spi.IClassMetadata;
import org.jboss.tools.hibernate.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.spi.IType;

/**
 * @author MAX
 *
 */
class PersistentCollectionNode extends BaseNode implements TypedNode{

	BaseNode virtualNode;
	CollectionType type;
	IType elementType;
	private boolean objectGraph;
	private Object baseObject;
	private Object collectionObject;

	boolean childrenCreated = false;
	private IClassMetadata md;
	public PersistentCollectionNode(NodeFactory factory, BaseNode parent, String name, CollectionType type, IClassMetadata md, ICollectionMetadata metadata, Object baseObject, boolean objectGraph) {
		super(factory, parent);
		this.md = md;
		this.type = type;
		this.name = name;
		this.baseObject = baseObject;
		this.objectGraph = objectGraph;



		iconName = factory.getIconNameForType(new TypeProxy(type));
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
			collectionObject = md.getPropertyValue(baseObject, name);
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
	
	@Override
	public String getCriteria() {
		final String criteria = ".createCriteria({0})"; //$NON-NLS-1$
		final String alias = "\n.createCriteria(\"{0}\", \"{1}\")"; //$NON-NLS-1$
		final String sess = "session"; //$NON-NLS-1$
		String enName = "";//$NON-NLS-1$
		String propCriteria = "";//$NON-NLS-1$
		if (getName() != null){
			if (getParent() instanceof BaseNode) {
				BaseNode baseNodeParent = (BaseNode)getParent();
				if (baseNodeParent instanceof TypedNode) {
					TypedNode typedNodeParent = (TypedNode)baseNodeParent;
					enName = typedNodeParent.getType().getName();
				} else {
					enName = baseNodeParent.getName();
				}
				enName = enName.substring(enName.lastIndexOf('.') + 1);
				propCriteria = NLS.bind(alias, getName(), getName().charAt(0));
			}
		}
		if ("".equals(enName)) { //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
		String enCriteria = NLS.bind(criteria, enName + ".class"); //$NON-NLS-1$
		return sess + enCriteria + propCriteria;
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

	private BaseNode createNode(int idx, Object element, IType type) { // TODO: use a common way to create these darn nodes!
		return new ClassNode(factory, this,type.getReturnedClass().getName(), factory.getMetaData(type.getReturnedClass() ),element,objectGraph);
	}

	public String renderLabel(boolean b) {
		return getLabel(getName(),b) + " : " + getLabel(type.getReturnedClass().getName(),b) + "<" + getLabel(elementType.getReturnedClass().getName(),b) + ">";  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}

	public IType getType() {
		return new TypeProxy(type);
	}
}
