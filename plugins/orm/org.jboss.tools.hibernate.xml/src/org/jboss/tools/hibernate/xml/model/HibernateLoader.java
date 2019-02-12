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

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintAList;
import org.jboss.tools.common.meta.impl.XModelMetaDataImpl;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.loaders.impl.SimpleWebFileLoader;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.hibernate.xml.HibernateXMLPlugin;
import org.jboss.tools.hibernate.xml.model.helpers.HibernateTypeHelper;
import org.w3c.dom.Element;


public class HibernateLoader extends SimpleWebFileLoader {

	protected XModelObjectLoaderUtil createUtil() {
		return new HibernateLoaderUtil();
	}

	public void load(XModelObject object) {
		if(!HibernateMetaLoader.ok) HibernateMetaLoader.load();
		super.load(object);
	}

}

class HibernateLoaderUtil extends XModelObjectLoaderUtil {
	static String[] metaFolders = new String[]{"Meta"}; //$NON-NLS-1$
	static String[] fileFolders = new String[]{"Types", "Imports", "Classes", "Result Sets", "Queries", "Filters", "Database Objects"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
	static String[] classFolders = new String[]{"Tuplizers", "Properties", "Subclasses", "SQL", "Filters", "Result Sets", "Queries"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
	static String[] subclassFolders = new String[]{"Tuplizers", "Synchronize", "Properties", "Subclasses", "SQL", "Result Sets", "Queries"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
	static String[] compositeElementFolders = new String[]{"Properties"}; //$NON-NLS-1$
	static String[] listFolders = new String[]{"SQL"}; //$NON-NLS-1$
	static String[] setFolders = new String[]{"SQL", "Filters"}; //$NON-NLS-1$ //$NON-NLS-2$
	
	static Map<String,String[]> folders = new HashMap<String,String[]>();
	
	static {
		folders.put("file", fileFolders); //$NON-NLS-1$
		folders.put("class", classFolders); //$NON-NLS-1$
		folders.put("subclass", subclassFolders); //$NON-NLS-1$
		folders.put("component", compositeElementFolders); //$NON-NLS-1$
		folders.put("element", compositeElementFolders); //$NON-NLS-1$
		folders.put("list", listFolders); //$NON-NLS-1$
		folders.put("set", setFolders); //$NON-NLS-1$
	}
	
	private String[] getFolders(XModelObject o) {
		String loaderKind = o.getModelEntity().getProperty("loaderKind"); //$NON-NLS-1$
		return (loaderKind == null) ? null : (String[])folders.get(loaderKind);
	}

	public void loadChildren(Element element, XModelObject o) {
		super.loadChildren(element, o);
		loadFolders(element, o, metaFolders);
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
		saveFolders(element, o, metaFolders);
		String entity = o.getModelEntity().getName();
		if("Hibernate3Component".equals(entity)) { //$NON-NLS-1$
			moveElementToEnd(element, "parent"); //$NON-NLS-1$
		}
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
			return (attr != null && "always".equals(attr.getProperty("save"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return super.isSaveable(entity, n, v, dv);
	}
	
	static void moveElementToEnd(Element element, String name) {
		Element[] es = XMLUtilities.getChildren(element, name);
		if(es != null) for (int i = 0; i < es.length; i++) {
			element.removeChild(es[i]);
			element.appendChild(es[i]);
		}
	}

    protected String getChildEntity(XModelEntity entity, Element e) {
    	if(e != null && "database-object".equals(e.getNodeName())) { //$NON-NLS-1$
    		Element c = XMLUtilities.getUniqueChild(e, "definition"); //$NON-NLS-1$
    		return c != null ? "Hibernate3DatabaseObjectDef" : "Hibernate3DatabaseObjectCreateDrop"; //$NON-NLS-1$ //$NON-NLS-2$
    	}
    	return super.getChildEntity(entity, e);
    }
}

class HibernateMetaLoader {
	static boolean ok = false;
	
	public static void load() {
		ok = true;
		try {
			XModelEntity e = XModelMetaDataImpl.getInstance().getEntity(HibernateConstants.ENTITY_HIBERNATE_PROPERTY_3_0);
			XAttribute a = e.getAttribute("type"); //$NON-NLS-1$
			if(a.getConstraint() instanceof XAttributeConstraintAList) {
				XAttributeConstraintAList c = (XAttributeConstraintAList)a.getConstraint();
				c.setValues(HibernateTypeHelper.TYPE_NAMES);
			}
		} catch (Exception e) {
			HibernateXMLPlugin.log(e);
		}
	}
	
}
