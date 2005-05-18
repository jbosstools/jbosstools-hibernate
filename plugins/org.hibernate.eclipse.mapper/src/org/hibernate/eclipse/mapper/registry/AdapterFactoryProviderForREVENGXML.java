package org.hibernate.eclipse.mapper.registry;

import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.hibernate.eclipse.mapper.modelhandler.ModelHandlerForREVENGXML;

public class AdapterFactoryProviderForREVENGXML extends AdapterFactoryProviderForXML {
	
	public AdapterFactoryProviderForREVENGXML() {
		
	}
	
	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof ModelHandlerForREVENGXML);
	}


	protected void addContentBasedFactories(IStructuredModel structuredModel) {
		// TODO Auto-generated method stub
		super.addContentBasedFactories(structuredModel);
	}
}
