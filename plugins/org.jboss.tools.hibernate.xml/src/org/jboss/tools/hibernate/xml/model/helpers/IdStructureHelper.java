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
package org.jboss.tools.hibernate.xml.model.helpers;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.engines.impl.EnginesLoader;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class IdStructureHelper {
	public static String ENT_HIBERNATE_ID = "Hibernate3Id";
	public static String ENT_HIBERNATE_COMPOSITE_ID = "Hibernate3CompositeId";
	public static String ID_PATH_PART = "[id]";
	
	public static XModelObject getIdChild(XModelObject clsReference) {
		return clsReference.getChildByPath(ID_PATH_PART);
	}
	
//	public static XModelObject getClassId(OrmDiagramClassImpl cls) {
//		XModelObject ref = cls.getReference();
//		return (ref == null) ? null : getIdChild(ref);
//	}
	
	public static boolean isId(XModelObject object) {
		if(object == null) return false;
		String entity = object.getModelEntity().getName();
		return(ENT_HIBERNATE_ID.equals(entity) || ENT_HIBERNATE_COMPOSITE_ID.equals(entity));
	}

//	public static Set getIdPropertyNames(XModelObject diagramClass) {
//		Set idProperties = null;
//		if(!(diagramClass instanceof OrmDiagramClassImpl)) return null;
//		OrmDiagramClassImpl cls = (OrmDiagramClassImpl)diagramClass;
//		XModelObject ref = cls.getReference();
//		if(ref == null || !"HibernateClass".equals(ref.getModelEntity().getName())) return null;
//		XModelObject id = getIdChild(ref);
//		if(id == null) return null;
//		if(!"HibernateCompositeId".equals(id.getModelEntity().getName())) return null;
//		if(id.getAttributeValue("name").length() > 0 || id.getAttributeValue("class").length() > 0) return null;
//		XModelObject[] ps = getIdStructure(id);
//		if(ps.length == 0) return null;
//		idProperties = new HashSet();
//		for (int i = 0; i < ps.length; i++) 
//			idProperties.add(ps[i].getAttributeValue("name"));
//		return idProperties;
//	}
	
	public static XModelObject[] getIdStructure(XModelObject id) {
		if(ENT_HIBERNATE_ID.equals(id.getModelEntity().getName())) {
			return new XModelObject[]{id};
		} else {
			List<XModelObject> list = new ArrayList<XModelObject>();
			XModelObject[] cs = id.getChildren();
			for (int i = 0; i < cs.length; i++) {
				if(cs[i].getModelEntity().getName().startsWith("HibernateMeta")) continue;
				list.add(cs[i]);
			}
			return list.toArray(new XModelObject[0]);
		}
	}
	
	public static XModelObject[] getAsAttributes(XModelObject id) {
		String name = id.getAttributeValue("name");
		if(name.length() == 0) return new XModelObject[0];
		String entity = id.getModelEntity().getName();
		if(ENT_HIBERNATE_ID.equals(entity)) {
			XModelObject c = id.getModel().createModelObject("HibernateProperty", null);
			EnginesLoader.merge(c, id, false);
			return new XModelObject[]{c};
		} else if("HibernateCompositeId".equals(entity)) {
			String cls = id.getAttributeValue("class");
			if(cls.length() == 0) {
				XModelObject[] cs = id.getChildren();
				List<XModelObject> list = new ArrayList<XModelObject>();
				for (int i = 0; i < cs.length; i++) {
					if(cs[i].getModelEntity().getName().startsWith("HibernateMeta")) continue;
					XModelObject gc = getAsAttribute(cs[i]);
					if(gc != null) list.add(gc);
				}
				return list.toArray(new XModelObject[0]);
			} else {
				XModelObject c = id.getModel().createModelObject("HibernateComponent", null);
				EnginesLoader.merge(c, id, false);
				XModelObject[] cs = id.getChildren();
				for (int i = 0; i < cs.length; i++) {
					XModelObject gc = getAsAttribute(cs[i]);
					if(gc != null) c.addChild(gc);
				}
				return new XModelObject[]{c};
			}
		}
		return new XModelObject[0];
	}
	
	public static XModelObject getAsAttribute(XModelObject key) {
		String ec = key.getModelEntity().getName();
		String er = ("HibernateKeyProperty".equals(ec))	? "HibernateProperty" 
		    : ("HibernateKeyManyToOne".equals(ec)) ? "HibernateManyToOne"
		    : null;
		if(er == null) return null;
		XModelObject a = key.getModel().createModelObject(er, null);
		EnginesLoader.merge(a, key, false);
		return a;
	}
	
	public static List getAsColumns(XModelObject id) {
		List<XModelObject> list = new ArrayList<XModelObject>();
//		String name = id.getAttributeValue("name");
//		if(name.length() == 0) return list;
		String entity = id.getModelEntity().getName();
		if(ENT_HIBERNATE_ID.equals(entity)) {
			addColumnsToList(id, list);
		} else if(ENT_HIBERNATE_COMPOSITE_ID.equals(entity)) {
			XModelObject[] cs = id.getChildren();
			for (int i = 0; i < cs.length; i++) {
				if(cs[i].getModelEntity().getName().startsWith("HibernateMeta")) continue;
				addColumnsToList(cs[i], list);
			}
		}
		return list;
	}
	
	private static void addColumnsToList(XModelObject o, List<XModelObject> list) {
		XModelObject[] cs = o.getChildren("HibernateColumn");
		if(cs.length > 0) {
			for (int i = 0; i < cs.length; i++) list.add(cs[i].copy());
		} else {
			String column = o.getAttributeValue("column");
			if(column == null) return;
			XModelObject c = o.getModel().createModelObject("HibernateColumn", null);
			EnginesLoader.merge(c, o, false);
			if(column.length() > 0) c.setAttributeValue("name", column);
			list.add(c);
		}
	}
	
	public static boolean isDefaultId(XModelObject id) {
		return isNamelessId(id) && !isIdComposite(id) &&
			   id.getAttributeValue("column").length() == 0 && 
			   id.getChildren("HibernateColumn").length == 0;
	}
	
	public static boolean isNamelessId(XModelObject id) {
		return isId(id) && id.getAttributeValue("name").length() == 0;
	}
	
	public static String getIdType(XModelObject id) {
		if(id == null) return null;
		if(ENT_HIBERNATE_ID.equals(id.getModelEntity().getName())) {
			return id.getAttributeValue("type");
		} else if(isIdComposite(id)) {
			return id.getAttributeValue("class");
		} else {
			return null;
		}
	}
	
	public static boolean isIdComposite(XModelObject id) {
		return ENT_HIBERNATE_COMPOSITE_ID.equals(id.getModelEntity().getName());		
	}
	
	public static XModelObject newDefaultId(XModel model) {
		return XModelObjectLoaderUtil.createValidObject(model, ENT_HIBERNATE_ID);
	}
	
	/*
	 * Returns true only if object is composite-id and has empty
	 * both attributes "name" and "class";
	 */
	
	public static boolean isCompositeIdOfOtherType(XModelObject object) {
		if(object == null || !isIdComposite(object)) return false;
		return (object.getAttributeValue("name").length() > 0 || 
		        object.getAttributeValue("class").length() > 0);
	}
	
	public static boolean isLastPropertyInCompositeId(XModelObject object) {
		if(object == null) return false;
		XModelObject parent = object.getParent();
		if(parent == null || !isIdComposite(parent)) return false;
		return (getIdStructure(parent).length < 2);
	}
	
	public static void showLastPropertyInCompositeIdWarning(XModel model) {
		String message = "You should not remove last attribute from composite-id.\n" +
						 "Please replace id first.";
		model.getService().showDialog("Warning", message, new String[]{"Close"}, null, ServiceDialog.WARNING);
	}
	
//	public static JavaBean getBeanForId(XModelObject id) {
//		if(id == null) return null;
//		OrmDiagramHelper h = OrmDiagramStructureHelper.getInstance().getDiagramHelper(id);
//		String clsname = h.getQualifiedClassName(id.getParent().getAttributeValue("name"));
//		JavaBean bean = JavaBeanRegistry.getInstance().getBean(null);
//		try {
//			bean = JavaBeanRegistry.getInstance().getBean(EclipseJavaUtil.findType(h.getJavaProject(), clsname));
//		} catch (Exception e) {}
//		if(bean.getType() == null) return null;
//		if(ENT_HIBERNATE_ID.equals(id.getModelEntity().getName())) {
//			String name = id.getAttributeValue("name");
//			if(name.length() > 0) return bean;
//			return null;
//		} else if(ENT_HIBERNATE_COMPOSITE_ID.equals(id.getModelEntity().getName())) {
//			String name = id.getAttributeValue("name");
//			if(name.length() > 0) {
//				JavaBeanProperty p = bean.getProperty(name);
//				if(p == null) return null;
//				try {
//					IType type = EclipseJavaUtil.findType(h.getJavaProject(), p.getTypeAsString());
//					bean = JavaBeanRegistry.getInstance().getBean(type);
//				} catch (Exception e) {}
//				return bean.getType() == null ? null : bean;
//			} 
//			String cls = id.getAttributeValue("class");
//			if(cls.length() == 0) return bean;
//			clsname = h.getQualifiedClassName(cls);
//			try {
//				IType type = EclipseJavaUtil.findType(h.getJavaProject(), clsname);
//				bean = JavaBeanRegistry.getInstance().getBean(type);
//			} catch (Exception e) {}
//			return (bean.getType() == null) ? null : bean;
//		}
//		return null;
//	}

}
