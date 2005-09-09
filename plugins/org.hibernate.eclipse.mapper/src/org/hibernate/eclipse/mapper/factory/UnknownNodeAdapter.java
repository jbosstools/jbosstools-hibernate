package org.hibernate.eclipse.mapper.factory;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.hibernate.eclipse.mapper.model.DOMReverseEngineeringDefinition;

public class UnknownNodeAdapter implements INodeAdapter {

	private INodeAdapterFactory factory;
	protected final DOMReverseEngineeringDefinition observer;

	UnknownNodeAdapter(INodeAdapterFactory factory, DOMReverseEngineeringDefinition revEngDefinition) {
		this.factory = factory;
		this.observer = revEngDefinition;
	}
	
	public boolean isAdapterForType(Object type)
    {
        return type.equals(factory);
    }

    public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos)
    {
    	observer.unknownNotifyChanged(notifier, eventType, changedFeature, oldValue, newValue, pos);
    	/*
        switch(eventType)
        {
        case INodeNotifier.CHANGE:
        case INodeNotifier.STRUCTURE_CHANGED:
        case INodeNotifier.CONTENT_CHANGED: // TODO: should we not react to more stuff ? or will we get all notificaitons at some point anyhow ?
            Node node = (Node)notifier;
            if(node.getNodeType() == Node.ELEMENT_NODE || node.getNodeType() == Node.DOCUMENT_NODE)
                observer.update(null, node);
        default:
            return;
        }*/
    }

}
