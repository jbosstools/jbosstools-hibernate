package org.hibernate.eclipse.mapper.modelhandler;

import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.AbstractModelHandler;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentCharsetDetector;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xml.core.internal.modelhandler.XMLModelLoader;

/**
 * Provides persistence unit model handling. 
 */
public class ModelHandlerForPersistenceUnitXML extends AbstractModelHandler implements IModelHandler {
	
	final static String CONTENTTYPE_ID = "org.hibernate.eclipse.mapper.persistenceunitxml"; //$NON-NLS-1$
	final private static String MODELHANDLER_ID = "org.hibernate.eclipse.mapper.handler.persistenceunitxml"; //$NON-NLS-1$
	
	public ModelHandlerForPersistenceUnitXML() {
		super();
		setId(MODELHANDLER_ID);
		setAssociatedContentTypeId(CONTENTTYPE_ID);
	}

	public IDocumentCharsetDetector getEncodingDetector() {
		return new XMLDocumentCharsetDetector();
	}

	public IDocumentLoader getDocumentLoader() {
		return new XMLDocumentLoader();
	}

	public IModelLoader getModelLoader() {
		return new XMLModelLoader();
	}
}
