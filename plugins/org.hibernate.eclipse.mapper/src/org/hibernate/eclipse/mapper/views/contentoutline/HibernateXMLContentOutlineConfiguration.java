package org.hibernate.eclipse.mapper.views.contentoutline;

import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapterFactory;
import org.eclipse.wst.xml.ui.internal.views.contentoutline.XMLContentOutlineConfiguration;

public class HibernateXMLContentOutlineConfiguration extends
		XMLContentOutlineConfiguration {

	
	private JFaceNodeAdapterFactoryForXML myFactory;

	protected IJFaceNodeAdapterFactory getFactory() {
		
		if(myFactory==null) {
			IJFaceNodeAdapterFactory realFactory = super.getFactory();
			
			myFactory = new JFaceNodeAdapterFactoryForXML(realFactory);
		}
		return myFactory;
	}
}
