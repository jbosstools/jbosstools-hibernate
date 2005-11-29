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
