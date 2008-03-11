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
package org.hibernate.eclipse.mapper.factory;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.hibernate.eclipse.mapper.model.DOMReverseEngineeringDefinition;

public class UnknownNodeAdapter implements INodeAdapter {

	protected final DOMReverseEngineeringDefinition observer;
	private final Object key;

	public UnknownNodeAdapter(Object key, DOMReverseEngineeringDefinition revEngDefinition) {
		this.key = key;		
		this.observer = revEngDefinition;
	}
	
	public boolean isAdapterForType(Object type)
    {
        return type.equals(key);
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
