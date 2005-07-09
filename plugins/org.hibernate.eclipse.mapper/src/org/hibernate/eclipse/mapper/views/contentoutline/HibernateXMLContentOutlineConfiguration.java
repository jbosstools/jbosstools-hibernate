package org.hibernate.eclipse.mapper.views.contentoutline;

import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapterFactory;
import org.eclipse.wst.xml.ui.internal.views.contentoutline.XMLContentOutlineConfiguration;

public class HibernateXMLContentOutlineConfiguration extends
		XMLContentOutlineConfiguration {

	
	private JFaceNodeAdapterFactoryForXML factory;

	protected IJFaceNodeAdapterFactory getFactory() {
		if(factory==null) {
			factory = new JFaceNodeAdapterFactoryForXML();
		}
		return factory;
	}
}
