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
package org.jboss.tools.hibernate.wizard.queries;

import java.lang.reflect.*;
import java.util.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.hibernate.HibernateException;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.internal.core.BaseMappingVisitor;
import org.jboss.tools.hibernate.internal.core.PersistentField;
import org.jboss.tools.hibernate.internal.core.hibernate.PersistableProperty;
import org.jboss.tools.hibernate.internal.core.util.*;

/**
 * @author yan
 *
 */
public class QueryResultContentProvider implements ITreeContentProvider {
	
	private Object[] queryResult;
//	private static final ResourceBundle BUNDLE = NamedQueriesWizard.BUNDLE;
	private HashSet<String> classSet;
	private IMapping mapping;
	
	public QueryResultContentProvider(IMapping m) {
		mapping=m;
		IPersistentClass[] classes=m.getPertsistentClasses();
		classSet=new HashSet<String>(classes.length);
		for(int i=0; i<classes.length; i++) classSet.add(classes[i].getName());
	
        // added by Nick 22.09.2005
		BaseMappingVisitor visitor = new BaseMappingVisitor()
        {
            /* (non-Javadoc)
             * @see org.jboss.tools.hibernate.internal.core.BaseMappingVisitor#visitComponentMapping(org.jboss.tools.hibernate.core.hibernate.IComponentMapping, java.lang.Object)
             */
            public Object visitComponentMapping(IComponentMapping mapping, Object argument) {
                classSet.add(mapping.getComponentClassName());
                return super.visitComponentMapping(mapping, argument);
            }
        };
        
        m.accept(visitor,null);
		// by Nick
    }
	

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object res) {
		if (res instanceof QueryResultItem) {
			queryResult=new QueryResultItem[]{(QueryResultItem)res};
		} else queryResult=null;
	}

	public Object[] getChildren(Object parentElement) {
		
		QueryResultItem parentItem=(QueryResultItem)parentElement;
		if (parentItem.isTrivial()) return null;
		
		ArrayList<QueryResultItem> children = new ArrayList<QueryResultItem>();
		
		if (parentItem.getSource() instanceof Throwable) {
			
			QueryResultItem qri=new QueryResultItem(this,((Throwable)parentItem.getSource()).getCause());
			children.add(qri);
			
		} else {
			
			Class parentClass=parentItem.getSource().getClass();
			
			if (parentClass.isArray()) {
				
				int length=Array.getLength(parentItem.getSource());
				QueryResultItem[] ch=new QueryResultItem[length>NamedQueryEditorPage.MAX_RESULTS?NamedQueryEditorPage.MAX_RESULTS+1:length];
				try {
					
					for(int i=0; i<ch.length; i++) {
						ch[i]=new QueryResultItem(this,Array.get(parentItem.getSource(),i));						
						if (!parentItem.isRoot()) ch[i].setName("["+i+"]");
					}
					
					if (ch.length<length) {
						ch[ch.length-1]=new QueryResultItem(this,null);
						ch[ch.length-1].setValue("...");
					}
					
				} catch (HibernateException e) {
	            	//TODO (tau-tau) for Exception					
					ch=new QueryResultItem[1];
					ch[0]=new QueryResultItem(this,e);
					ExceptionHandler.logThrowableWarning(e,e.toString());
				}
				
				return ch;
				
			}
			
			
			if (parentItem.getSource() instanceof Collection || parentItem.getSource() instanceof Map) {
				
				Collection coll=null;
				if (parentItem.getSource() instanceof Map) {
					coll=((Map)parentItem.getSource()).values();
				} else coll=(Collection)parentItem.getSource();

				int length=coll.size();
				QueryResultItem[] ch=new QueryResultItem[length>NamedQueryEditorPage.MAX_RESULTS?NamedQueryEditorPage.MAX_RESULTS+1:length];
				try {
					
					Iterator it=coll.iterator();
					for(int i=0; i<ch.length && it.hasNext(); i++) {
						ch[i]=new QueryResultItem(this,it.next());						
						if (!parentItem.isRoot()) ch[i].setName("["+i+"]");
					}
					
					if (ch.length<length && it.hasNext()) {
						ch[ch.length-1]=new QueryResultItem(this,null);
						ch[ch.length-1].setValue("...");
					}
					
				} catch (HibernateException e) {
	            	//TODO (tau-tau) for Exception					
					ch=new QueryResultItem[1];
					ch[0]=new QueryResultItem(this,e);
					ExceptionHandler.logThrowableWarning(e,e.toString());
				}

				
				
				return ch;
			}
			
			if (parentItem.getSource() instanceof Enumeration) {
				
				Enumeration en=(Enumeration)parentItem.getSource();
				ArrayList<QueryResultItem> tmp=new ArrayList<QueryResultItem>(NamedQueryEditorPage.MAX_RESULTS);
				Object[] ch=null;
				try {
					int i=0;
					for(; en.hasMoreElements() && i<NamedQueryEditorPage.MAX_RESULTS; i++) {
						QueryResultItem item=new QueryResultItem(this,en.nextElement());
						if (!parentItem.isRoot()) item.setName("["+i+"]");
						tmp.add(item);						
					}
					
					if (en.hasMoreElements()) {
						QueryResultItem item=new QueryResultItem(this,null);
						if (!parentItem.isRoot()) item.setValue("["+i+"]");
						tmp.add(item);						
					}
					
					ch=tmp.toArray();
					
				} catch (HibernateException e) {
	            	//TODO (tau-tau) for Exception					
					ch=new QueryResultItem[1];
					ch[0]=new QueryResultItem(this,e);
					ExceptionHandler.logThrowableWarning(e,e.toString());
				}
				
				return ch;
			}
			
			if (parentItem.getSource() instanceof Iterator) {
				
				Iterator it = (Iterator)parentItem.getSource();
				ArrayList<QueryResultItem> tmp = new ArrayList<QueryResultItem>(NamedQueryEditorPage.MAX_RESULTS);
				Object[] ch=null;
				try {
					int i=0;
					for(; it.hasNext() && i<NamedQueryEditorPage.MAX_RESULTS; i++) {
						QueryResultItem item=new QueryResultItem(this,it.next());
						if (!parentItem.isRoot()) item.setName("["+i+"]");
						tmp.add(item);						
					}
					
					if (it.hasNext()) {
						QueryResultItem item=new QueryResultItem(this,null);
						if (!parentItem.isRoot()) item.setValue("["+i+"]");
						tmp.add(item);						
					}
					
					ch=tmp.toArray();
					
				} catch (HibernateException e) {
	            	//TODO (tau-tau) for Exception					
					ch=new QueryResultItem[1];
					ch[0]=new QueryResultItem(this,e);
					ExceptionHandler.logThrowableWarning(e,e.toString());
				}
				
				return ch;
				
			}
			
			IPersistentClass clazz=mapping.findClass(HibernateProxyHelper.getClassWithoutInitializingProxy(parentItem.getSource()).getName());
			if (clazz!=null) {
				IPersistentField[] fields=clazz.getFields();
				for(int i=0; i<fields.length; i++) {
					try {
						QueryResultItem qri=new QueryResultItem(this,getValue(fields[i].getName(),parentItem.getSource()));
						qri.setName(fields[i].getName());
						if (qri.isTrivial() && qri.getSource()==null) qri.setType(fields[i].getType()); 
						children.add(qri);
					} catch (Exception e) {
		            	//TODO (tau-tau) for Exception						
						children.add(new QueryResultItem(this,e,fields[i].getName()));
						ExceptionHandler.logThrowableWarning(e,e.toString());
					}
				}
				
			} else {
				
				try {
					IType iType=ScanProject.findClass(HibernateProxyHelper.getClassWithoutInitializingProxy(parentItem.getSource()).getName(),mapping.getProject().getProject());
					TypeAnalyzer ta=new TypeAnalyzer(iType);
					PersistableProperty[] pp=ta.getPropertiesByMask(PersistentField.ACCESSOR_FIELD | PersistentField.ACCESSOR_PROPERTY);
					for(int i=0; i<pp.length; i++) {
						try {
							QueryResultItem qri=new QueryResultItem(this,getValue(pp[i].getName(),parentItem.getSource()));
							qri.setName(pp[i].getName());
							if (qri.isTrivial() && qri.getSource()==null) qri.setType(pp[i].getType()); 
							children.add(qri);
						} catch (Exception e) {
			            	//TODO (tau-tau) for Exception							
							children.add(new QueryResultItem(this,e,pp[i].getName()));
							ExceptionHandler.logThrowableWarning(e,e.toString());
						}
					}
				} catch (CoreException e) {
	            	//TODO (tau-tau) for Exception					
					ExceptionHandler.logThrowableWarning(e,e.toString());
				}
			}
				
		}
		
		
		
		return children.toArray();
	}
	
	private Object getValue(String name,Object src) throws NoSuchFieldException {
		try {
			Field field=src.getClass().getDeclaredField(name);
			if (!field.isAccessible()) field.setAccessible(true);
			return field.get(src);
		} catch (Exception e) {
        	//TODO (tau-tau) for Exception			
		}
		try {
			Method method=src.getClass().getDeclaredMethod("get"+name.substring(0,1).toUpperCase()+name.substring(1),new Class[0]);
			if (!method.isAccessible()) method.setAccessible(true);
			return method.invoke(src,(Object[])null);
		} catch (Exception e) {
        	//TODO (tau-tau) for Exception			
		}
		throw new NoSuchFieldException(name);
	}
	

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return !((QueryResultItem)element).isTrivial();
	}

	public Object[] getElements(Object inputElement) {
		//return getChildren(inputElement);
		Object[] ret=queryResult;
		if (queryResult!=null) {
			queryResult=null;
		} else ret=getChildren(inputElement);
		return ret;
		//return new QueryResultItem[]{(QueryResultItem)inputElement};
	}
	
	public boolean isMapped(Class c) {
		return classSet.contains(c.getName());
	}
	

}
