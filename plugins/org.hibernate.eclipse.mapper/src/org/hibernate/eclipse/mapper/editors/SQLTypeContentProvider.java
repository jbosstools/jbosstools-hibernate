package org.hibernate.eclipse.mapper.editors;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.hibernate.eclipse.mapper.model.DOMModelUtil;
import org.w3c.dom.Element;

public class SQLTypeContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		IStructuredModel im = (IStructuredModel)inputElement;
		
		if(im instanceof IDOMModel) {
			IDOMModel model = (IDOMModel)im;
			List childNodes = DOMModelUtil.getChildrenByTagName(model.getDocument(), "hibernate-reverse-engineering");
			if(childNodes.size()>=1) {
				Element l = (Element)childNodes.get(0);
				childNodes = DOMModelUtil.getChildrenByTagName(l, "type-mapping");
				if(childNodes.size()>=1) {
					childNodes = DOMModelUtil.getChildrenByTagName(l, "sql-type");
					Object[] o = new Object[childNodes.size()];
					for (int i = 0; i < childNodes.size(); i++) {
						o[i] = childNodes.get(i);					
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
