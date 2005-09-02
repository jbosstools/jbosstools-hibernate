package org.hibernate.eclipse.mapper.factory;

import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.hibernate.eclipse.hqleditor.HibernateResultCollector;
import org.hibernate.eclipse.mapper.model.HibernateElement;
import org.hibernate.eclipse.mapper.model.ReverseEngineeringDefinitionElement;
import org.hibernate.eclipse.mapper.model.TableFilterElement;
import org.w3c.dom.NodeList;

public class ElementAdapterFactory extends AbstractAdapterFactory {

	private static ElementAdapterFactory instance;

	protected INodeAdapter createAdapter(INodeNotifier target) {
		HibernateElement result = getNewAdapter((IDOMNode)target);
		if (result != null) {
			adaptChildren(result);
		}
		return result;
	}

	
	private void adaptChildren(HibernateElement adapter) {
		NodeList nodes = adapter.getNode().getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			HibernateElement childAdapter = 
				(HibernateElement)adapt((INodeNotifier)nodes.item(i));
			if (childAdapter != null) {
				adapter.add(childAdapter);
			}
		}
	}

	private HibernateElement getNewAdapter(IDOMNode node) {
		if(node.getNodeName().equals("hibernate-reverse-engineering")) {
			return new ReverseEngineeringDefinitionElement(node);
		}
		if(node.getNodeName().equals("table-filter")) {
			return new TableFilterElement(node);
		}
		return null;
	}
	
	public static synchronized AbstractAdapterFactory getDefault() {
		if(instance==null) {
			instance = new ElementAdapterFactory();
		}
		return instance;
	}

}
