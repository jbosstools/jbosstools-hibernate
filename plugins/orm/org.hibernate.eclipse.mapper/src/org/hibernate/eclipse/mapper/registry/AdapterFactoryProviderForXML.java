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
package org.hibernate.eclipse.mapper.registry;

import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;



public class AdapterFactoryProviderForXML extends org.eclipse.wst.xml.ui.internal.registry.AdapterFactoryProviderForXML {
	
	public AdapterFactoryProviderForXML() {
	
	}
	
	protected void addContentBasedFactories(IStructuredModel structuredModel) {
		super.addContentBasedFactories(structuredModel);
		
		/* doesn't work in M7 since it doesnt select the content type deterministicly
		FactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		//Assert.isNotNull(factoryRegistry, "Program Error: client caller must ensure model has factory registry"); //$NON-NLS-1$
		INodeAdapterFactory factory = null;
		
		factory = factoryRegistry.getFactoryFor(IJFaceNodeAdapter.class);
		if (factory == null) {
			factory = new JFaceNodeAdapterFactoryForXML();
			factoryRegistry.addFactory(factory);
		}
		super.addContentBasedFactories(structuredModel);
		*/
	}
}
