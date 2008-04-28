/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.xml.model;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.loaders.impl.SimpleWebFileLoader;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.w3c.dom.Element;

public class HibConfigLoader extends SimpleWebFileLoader {

	protected XModelObjectLoaderUtil createUtil() {
		return new HibConfigLoaderUtil();
	}

}

class HibConfigLoaderUtil extends XModelObjectLoaderUtil {
	static String[] factoryFolders = new String[]{"Properties", "Mappings", "Caches", "Events", "Listeners"};
	
    public boolean save(Element parent, XModelObject o) {
    	if(o.getModelEntity().getName().equals("HibConfig3Security")) {
    		if(o.getAttributeValue("context").length() == 0 && o.getChildren().length == 0 && 
    			o.getAttributeValue("comment").length() == 0) {
    			return true;
    		}
    	}
    	return super.save(parent, o);
    }

	private String[] getFolders(XModelObject o) {
		String entity = o.getModelEntity().getName();
		if(entity.equals("HibConfig3SessionFactory")) return factoryFolders;
		return null;
	}

	public void loadChildren(Element element, XModelObject o) {
		super.loadChildren(element, o);
		String[] fs = getFolders(o);
		if(fs != null) loadFolders(element, o, fs);
	}
	
	protected void loadFolders(Element element, XModelObject o, String[] folders) {
		for (int i = 0; i < folders.length; i++) {
			XModelObject c = o.getChildByPath(folders[i]);
			if(c != null) super.loadChildren(element, c);
		}
	}

    public boolean saveChildren(Element element, XModelObject o) {
		boolean b = super.saveChildren(element, o);
		String[] fs = getFolders(o);
		if(fs != null) saveFolders(element, o, fs);
    	return b;	
    }
    
	protected boolean saveFolders(Element element, XModelObject o, String[] folders) {
		boolean b = true;
		for (int i = 0; i < folders.length; i++) {
			XModelObject c = o.getChildByPath(folders[i]);
			if(c != null) b &= super.saveChildren(element, c);
		}
		return b;
	}

	protected boolean isSaveable(XModelEntity entity, String n, String v, String dv) {
		if(v == null) return false;
		if(v.length() == 0 || v.equals(dv)) {
			XAttribute attr = entity.getAttribute(n);
			return (attr != null && "always".equals(attr.getProperty("save")));
		}
		return super.isSaveable(entity, n, v, dv);
	}
	
}
