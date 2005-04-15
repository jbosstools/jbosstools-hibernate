package org.hibernate.eclipse.mapper.registry;

import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.xml.ui.internal.registry.AdapterFactoryProviderForXML;
import org.hibernate.eclipse.mapper.modelhandler.ModelHandlerForCFGXML;

public class AdapterFactoryProviderForCFGXML extends AdapterFactoryProviderForXML {
	
	/*
	 * @see AdapterFactoryProvider#isFor(ContentTypeDescription)
	 */
	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof ModelHandlerForCFGXML);
	}


}
