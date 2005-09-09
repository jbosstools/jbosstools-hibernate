package org.hibernate.eclipse.mapper.factory;

import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.hibernate.eclipse.mapper.model.DOMReverseEngineeringDefinition;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ObserverAdapterFactory extends AbstractAdapterFactory {

	DOMReverseEngineeringDefinition revEngDefinition;
	
	public ObserverAdapterFactory(DOMReverseEngineeringDefinition revEngDefinition) {
		this.revEngDefinition = revEngDefinition;
    }
		
	protected INodeAdapter createAdapter(INodeNotifier target)
    {
		Node n = (Node) target;
		String nodeName = n.getNodeName();
		INodeAdapter result = null;
			
		if("hibernate-reverse-engineering".equals(nodeName)) {
			result = new UnknownNodeAdapter(this, revEngDefinition) {
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					observer.hibernateMappingChanged();
				}
			};
		} else if("table-filter".equals(nodeName)) {
			result = new UnknownNodeAdapter(this, revEngDefinition) {
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					observer.tableFilterChanged(notifier);
				}
			};
		} else if("type-mapping".equals(nodeName)) {
			result = new UnknownNodeAdapter(this, revEngDefinition) {
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					observer.typeMappingChanged(notifier);
				}
			};
		} else if("sql-type".equals(nodeName)) {
			result = new UnknownNodeAdapter(this, revEngDefinition) {
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					observer.sqlTypeChanged(notifier);
				}
			};
		}
		
		if(result==null) {
			result = new UnknownNodeAdapter(this, revEngDefinition);
		}
		
		if (result != null) {
			adaptChildren((Node)target);
		}
		
		return result;        
    }
	
	// adapting each child so we have listeners on them all.
	private void adaptChildren(Node node) {
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			INodeAdapter childAdapter = adapt((INodeNotifier)nodes.item(i));			
		}
	}

    protected UnknownNodeAdapter doAdapt(Object object)
    {
        UnknownNodeAdapter result = null;
        if(object instanceof INodeNotifier)
            result = (UnknownNodeAdapter) adapt((INodeNotifier)object);
        return result;
    }

    

    
}
