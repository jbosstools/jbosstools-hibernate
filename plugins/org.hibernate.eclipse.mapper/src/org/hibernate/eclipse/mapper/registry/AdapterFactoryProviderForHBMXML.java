package org.hibernate.eclipse.mapper.registry;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.common.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.IFactoryRegistry;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.core.util.Assert;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.views.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.core.modelhandler.ModelHandlerForXML;
import org.eclipse.wst.xml.core.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.ui.DOMObserver;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.registry.AdapterFactoryProviderForXML;
import org.eclipse.wst.xml.ui.views.contentoutline.JFaceNodeAdapterFactory;
import org.eclipse.wst.xml.ui.views.properties.XMLPropertySourceAdapterFactory;
import org.hibernate.eclipse.mapper.modelhandler.ModelHandlerForHBMXML;

public class AdapterFactoryProviderForHBMXML extends AdapterFactoryProviderForXML {
	
	/*
	 * @see AdapterFactoryProvider#addAdapterFactories(IStructuredModel)
	 */
	public void addAdapterFactories(IStructuredModel structuredModel) {

		// add the normal content based factories to model's registry
		addContentBasedFactories(structuredModel);
	}

	protected void addContentBasedFactories(IStructuredModel structuredModel) {
		IFactoryRegistry factoryRegistry = structuredModel.getFactoryRegistry();
		Assert.isNotNull(factoryRegistry, "Program Error: client caller must ensure model has factory registry"); //$NON-NLS-1$
		AdapterFactory factory = null;
		// == this list came from the previous "XML only" list

		// what was this still here? (6/4/03)
		// I commented out on 6/4/03) but may have been something "extra"
		// initializing
		// old content assist adapter unnecessarily?
		//factory =
		// factoryRegistry.getFactoryFor(com.ibm.sed.edit.adapters.ContentAssistAdapter.class);

		factory = factoryRegistry.getFactoryFor(IPropertySource.class);
		if (factory == null) {
			factory = new XMLPropertySourceAdapterFactory();
			factoryRegistry.addFactory(factory);
		}
		factory = factoryRegistry.getFactoryFor(IJFaceNodeAdapter.class);
		if (factory == null) {
			factory = new JFaceNodeAdapterFactory();
			factoryRegistry.addFactory(factory);
		}

		// cs... added for inferred grammar support
		//
		if (structuredModel != null) {
			ModelQuery modelQuery = ModelQueryUtil.getModelQuery(structuredModel);
			if (modelQuery != null) {
				CMDocumentManager documentManager = modelQuery.getCMDocumentManager();
				if (documentManager != null) {
					IPreferenceStore store = XMLUIPlugin.getDefault().getPreferenceStore();
					boolean useInferredGrammar = (store != null) ? store.getBoolean(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR) : true;

					documentManager.setPropertyEnabled(CMDocumentManager.PROPERTY_ASYNC_LOAD, true);
					documentManager.setPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD, false);
					documentManager.setPropertyEnabled(CMDocumentManager.PROPERTY_USE_CACHED_RESOLVED_URI, true);
					DOMObserver domObserver = new DOMObserver(structuredModel);
					domObserver.setGrammarInferenceEnabled(useInferredGrammar);
					domObserver.init();
				}
			}
		}
	}

	/*
	 * @see AdapterFactoryProvider#isFor(ContentTypeDescription)
	 */
	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof ModelHandlerForHBMXML);
	}

	public void reinitializeFactories(IStructuredModel structuredModel) {
		// nothing to do, since no embedded type
	}

}
