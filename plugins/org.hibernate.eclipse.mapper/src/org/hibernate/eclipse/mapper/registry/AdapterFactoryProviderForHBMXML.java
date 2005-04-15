package org.hibernate.eclipse.mapper.registry;

import org.eclipse.wst.sse.core.INodeAdapterFactory;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.ui.internal.registry.AdapterFactoryProviderForXML;
import org.hibernate.eclipse.mapper.modelhandler.ModelHandlerForHBMXML;
import org.hibernate.eclipse.mapper.views.contentoutline.JFaceNodeAdapterFactoryForHBMXML;



public class AdapterFactoryProviderForHBMXML extends AdapterFactoryProviderForXML {
	
	/*
	 * @see AdapterFactoryProvider#isFor(ContentTypeDescription)
	 */
	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof ModelHandlerForHBMXML);
	}

	protected void addContentBasedFactories(IStructuredModel structuredModel) {
		FactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		//Assert.isNotNull(factoryRegistry, "Program Error: client caller must ensure model has factory registry"); //$NON-NLS-1$
		INodeAdapterFactory factory = null;
		
		factory = factoryRegistry.getFactoryFor(IJFaceNodeAdapter.class);
		if (factory == null) {
			factory = new JFaceNodeAdapterFactoryForHBMXML();
			factoryRegistry.addFactory(factory);
		}
		super.addContentBasedFactories(structuredModel);
	}
}
