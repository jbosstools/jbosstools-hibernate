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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.proxy.*;

/**
 * @author yan
 *
 */
public class QueryResultItem {

	private Object source;
	private String name,value,type,presentation;
	private boolean trivial;
	private QueryResultContentProvider provider;
	
	public QueryResultItem(QueryResultContentProvider p,Object o) {
		this(p,o,null);
	}
	
	public QueryResultItem(QueryResultContentProvider p,Object o,String name) {
		
		provider=p;
		this.name=name;
		source=o;
		trivial=true;
		if (source!=null) {
			
			if (source.getClass().isArray()) {
				type=source.getClass().getComponentType().getName();
				value=source.getClass().getComponentType().getName()+"["+Array.getLength(source)+"]";
				trivial=false;
			} else if (source instanceof Collection) {
				type=source.getClass().getName();
				value="java.lang.Object["+((Collection)source).size()+"]";
				trivial=false;
			} else if (source instanceof Enumeration || source instanceof Iterator) {
				type=source.getClass().getName();
				value="java.lang.Object";
				trivial=false;
			} else if (source instanceof Map) {
				type=source.getClass().getName();
				value="java.lang.Object["+((Map)source).size()+"]";
				trivial=false;
			} else if (source instanceof Throwable){
				value=((Throwable)source).getLocalizedMessage();
				type=name;
				trivial=((Throwable)source).getCause()==null;
				this.name=null;
			} else if (provider!=null && provider.isMapped(HibernateProxyHelper.getClassWithoutInitializingProxy(source))) {
				type=source.getClass().getName();
				value="("+source.toString()+")";
				trivial=false;
			} else {
				type=source.getClass().getName();
				value="\""+source.toString()+"\"";
			}
			
			if (provider==null) {
				value=null;
			}
			
		} else {
			if (provider!=null) value="null";
		}
		
		presentation=null;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		presentation=null;
	}

	public Object getSource() {
		return source;
	}

	public String toString() {
		if (presentation==null) makePresentation();
		return presentation;
	}
	
	private void makePresentation() {
		StringBuffer sb=new StringBuffer();
		if (type!=null) {
			sb.append(type);
			sb.append(' ');
		}
		if (name!=null) {
			sb.append(name);
			if (value!=null) {
				sb.append('=');
			}
		}
		if (value!=null) {
			sb.append(value);
		}
		presentation=sb.toString();
	}

	public boolean isTrivial() {
		return trivial;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	protected String getValue() {
		return value;
	}
	protected void setValue(String value) {
		this.value = value;
	}
	protected boolean isRoot() {
		return provider==null;
	}
	
	
	
}
