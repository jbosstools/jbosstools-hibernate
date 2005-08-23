/*
 * Created on 30-12-2003
 *
 */
package org.hibernate.console.node;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.tree.TreeNode;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
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
			IllegalArgumentException iae = new IllegalArgumentException("Could not access property value");
			iae.initCause(e);
			throw iae;
		}
		return collectionObject;
	}

	public String getHQL() {
		return "";
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
				Iterator i = ( (Collection)collectionObject).iterator();
				
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

	private Object createNode(int idx, Object element, Type type) { // TODO: use a common way to create these darn nodes!
		return new ClassNode(factory, this,type.getReturnedClass().getName(), factory.getMetaData(type.getReturnedClass() ),element,objectGraph);
	}

	public String renderLabel(boolean b) {
		return getLabel(getName(),b) + " : " + getLabel(type.getReturnedClass().getName(),b) + "<" + getLabel(elementType.getReturnedClass().getName(),b) + ">";
	}
	
	public Type getType() {		
		return type;
	}
}
