package org.hibernate.eclipse.mapper.registry;

import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.hibernate.eclipse.mapper.views.contentoutline.JFaceNodeAdapterFactoryForXML;



public class AdapterFactoryProviderForXML extends org.eclipse.wst.xml.ui.internal.registry.AdapterFactoryProviderForXML {
	
	public AdapterFactoryProviderForXML() {
		
	}
	
	protected void addContentBasedFactories(IStructuredModel structuredModel) {
		FactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		//Assert.isNotNull(factoryRegistry, "Program Error: client caller must ensure model has factory registry"); //$NON-NLS-1$
		INodeAdapterFactory factory = null;
		
		factory = factoryRegistry.getFactoryFor(IJFaceNodeAdapter.class);
		if (factory == null) {
			factory = new JFaceNodeAdapterFactoryForXML();
			factoryRegistry.addFactory(factory);
		}
		super.addContentBasedFactories(structuredModel);
	}
}
