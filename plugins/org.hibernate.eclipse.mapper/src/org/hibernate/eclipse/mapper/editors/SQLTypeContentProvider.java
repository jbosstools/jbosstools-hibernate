package org.hibernate.eclipse.mapper.editors;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SQLTypeContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		IStructuredModel im = (IStructuredModel)inputElement;
		
		if(im instanceof IDOMModel) {
			IDOMModel model = (IDOMModel)im;
			NodeList childNodes = model.getDocument().getElementsByTagName("hibernate-reverse-engineering");
			if(childNodes.getLength()>=1) {
				Element l = (Element)childNodes.item(0);
				childNodes = l.getElementsByTagName("type-mapping");
				if(childNodes.getLength()>=1) {
					childNodes = l.getElementsByTagName("sql-type");
					Object[] o = new Object[childNodes.getLength()];
					for (int i = 0; i < childNodes.getLength(); i++) {
						o[i] = childNodes.item(i);					
					}
					return o;	
				}
				
			}		
		}
		return new Object[0];
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
