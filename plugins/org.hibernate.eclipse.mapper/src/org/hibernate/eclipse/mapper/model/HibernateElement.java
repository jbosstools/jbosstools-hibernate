package org.hibernate.eclipse.mapper.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.hibernate.eclipse.mapper.factory.ElementAdapterFactory;

public abstract class HibernateElement implements INodeAdapter, HibernateModelEvents {

	private IDOMNode node;
	public static final String padding = "   ";

	final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);		
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);		
	}


	public HibernateElement(IDOMNode node2) {
		this.node = node2;
	}

	public boolean isAdapterForType(Object type) {
		return type == HibernateElement.class;
	}

	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		System.out.println("" + eventType + changedFeature + oldValue + newValue + pos);
		IDOMNode n = (IDOMNode)notifier;
		
		if (INodeNotifier.ADD == eventType) {
			if (!(newValue instanceof IDOMNode)) return;
			HibernateElement adapter = 
				(HibernateElement)ElementAdapterFactory.getDefault().adapt((IDOMNode)newValue);
			if (adapter != null) {
				((HibernateElement)n.getAdapterFor(HibernateElement.class)).add(adapter);
			}
		}
		if (INodeNotifier.REMOVE == eventType) {
			if (!(oldValue instanceof IDOMNode)) return;
			HibernateElement adapter = 
				(HibernateElement)ElementAdapterFactory.getDefault().adapt((IDOMNode)oldValue);
			if (adapter != null) {
				((HibernateElement)n.getAdapterFor(HibernateElement.class)).remove(adapter);
			}
		}		
	}

	public void remove(HibernateElement adapter) {
		
	}

	public void add(HibernateElement adapter) {
		
	}

	public IDOMNode getNode() {
		return node;
	}

	protected void setAttribute(String attributeName, String value, String defaultValue) {
		org.w3c.dom.Node attribNode = getNode().getAttributes().getNamedItem(attributeName);
		if(attribNode!=null && value.equals(defaultValue)) { 
			getNode().removeChild(attribNode);
		} else if (attribNode!=null) {
			attribNode.setNodeValue(value);
		} else if (attribNode == null && !value.equals(defaultValue)) {
			attribNode = getNode().getOwnerDocument().createAttribute(attributeName);
			getNode().getAttributes().setNamedItem(attribNode);
			attribNode.setNodeValue(value);
		} 
	}
	
	public int getLevel() {
		if (getNode().getParentNode() == getNode().getOwnerDocument()) {
			return 1;
		} else {
			HibernateElement graphElemnt = 
				(HibernateElement)((IDOMNode)getNode().getParentNode()).getAdapterFor(HibernateElement.class);
			return graphElemnt.getLevel() + 1;
		}
	}
	
	public String getPaddingString(int amount) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < amount; i++) {
			result.append(padding);
		}
		return result.toString();
	}
}
