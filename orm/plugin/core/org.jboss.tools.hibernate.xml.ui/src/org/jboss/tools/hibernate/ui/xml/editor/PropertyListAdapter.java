package org.jboss.tools.hibernate.ui.xml.editor;

import java.util.TreeSet;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.attribute.IListContentProvider;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultComboBoxValueAdapter;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultXAttributeListContentProvider;
import org.jboss.tools.hibernate.ui.xml.form.HibConfig3PropertyFormLayoutData;
import org.jboss.tools.hibernate.xml.model.impl.HibConfigComplexPropertyImpl;

public class PropertyListAdapter extends DefaultComboBoxValueAdapter {

	protected IListContentProvider createListContentProvider(XAttribute attribute) {
		PropertyListContentProvider p = new PropertyListContentProvider();
		p.setContext(modelObject);
		p.setAttribute(attribute);
		return p;	
	}

}

class PropertyListContentProvider extends DefaultXAttributeListContentProvider {
	private XModelObject context;
	
	public void setContext(XModelObject context) {
		this.context = context;
	}

	protected void loadTags() {
		XModelObject f = context;
		XModelEntity ent = f.getModelEntity().getMetaModel().getEntity(HibConfig3PropertyFormLayoutData.PROPERTY_FOLDER_ENTITY);
		if(ent == null) return;
		XChild[] cs = ent.getChildren();
		TreeSet<String> set = new TreeSet<String>();

		for (int i = 0; i < cs.length; i++) {
			XModelEntity e = f.getModelEntity().getMetaModel().getEntity(cs[i].getName());
			XAttribute[] as = e.getAttributes();
			for (int j = 0; j < as.length; j++) {
				String prop = as[j].getProperty(HibConfigComplexPropertyImpl.H_PROPERTY);
				if(prop != null && prop.length() > 0) {
					set.add(prop);
				}
			}
		}
		
		tags = set.toArray(new String[0]);		
	}
	
}

