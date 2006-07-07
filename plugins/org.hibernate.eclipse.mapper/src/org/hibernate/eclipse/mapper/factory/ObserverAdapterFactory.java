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

import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.hibernate.eclipse.mapper.model.DOMAdapter;
import org.hibernate.eclipse.mapper.model.DOMReverseEngineeringDefinition;
import org.hibernate.eclipse.mapper.model.RevEngColumnAdapter;
import org.hibernate.eclipse.mapper.model.RevEngGeneratorAdapter;
import org.hibernate.eclipse.mapper.model.RevEngParamAdapter;
import org.hibernate.eclipse.mapper.model.RevEngPrimaryKeyAdapter;
import org.hibernate.eclipse.mapper.model.RevEngTableAdapter;
import org.hibernate.eclipse.mapper.model.TableFilterAdapter;
import org.hibernate.eclipse.mapper.model.TypeMappingAdapter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ObserverAdapterFactory extends AbstractAdapterFactory {

	DOMReverseEngineeringDefinition revEngDefinition;
	
	public ObserverAdapterFactory(DOMReverseEngineeringDefinition revEngDefinition) {
		super();
		setAdapterKey(DOMAdapter.class); //DOMAdapter.class, true
		setShouldRegisterAdapter(true);
		this.revEngDefinition = revEngDefinition;
    }
			
	protected INodeAdapter createAdapter(INodeNotifier target)
    {
		Node n = (Node) target;
		String nodeName = n.getNodeName();
		INodeAdapter result = null;
			
		Object key = DOMAdapter.class;
		
		if("hibernate-reverse-engineering".equals(nodeName)) {
			result = new UnknownNodeAdapter(key, revEngDefinition) {
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					observer.hibernateMappingChanged();
				}
			};
		} else if("table-filter".equals(nodeName)) {
			result = new TableFilterAdapter((Node) target, revEngDefinition);
		} else if("type-mapping".equals(nodeName)) {
			result = new UnknownNodeAdapter(key, revEngDefinition) {
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					observer.typeMappingChanged(notifier);
				}
			};
		} else if("sql-type".equals(nodeName)) {
			result = new TypeMappingAdapter((Node) target, revEngDefinition);
		} else if("table".equals(nodeName)) {
			result = new RevEngTableAdapter((Node) target, revEngDefinition);
		} else if("column".equals(nodeName) || "key-column".equals(nodeName)) {
			result = new RevEngColumnAdapter((Node) target, revEngDefinition);				
		} else if("primary-key".equals(nodeName)) {
			result = new RevEngPrimaryKeyAdapter((Node) target, revEngDefinition);
		} else if("generator".equals(nodeName)) { 
			result = new RevEngGeneratorAdapter((Node) target, revEngDefinition);
		} else if("param".equals(nodeName)) { 
			result = new RevEngParamAdapter((Node) target, revEngDefinition);
		}
		else if("foreign-key".equals(nodeName) 
				|| "column-ref".equals(nodeName) 
				) {
			result = new UnknownNodeAdapter(key, revEngDefinition) {
				public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
					observer.tablesChanged(notifier);
				}
			};
		}
    
		if(result==null) {
			result = new UnknownNodeAdapter(key, revEngDefinition);
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
