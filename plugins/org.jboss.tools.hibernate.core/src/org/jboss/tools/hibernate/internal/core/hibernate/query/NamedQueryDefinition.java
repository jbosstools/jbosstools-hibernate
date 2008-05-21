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
package org.jboss.tools.hibernate.internal.core.hibernate.query;

import org.eclipse.swt.widgets.TreeItem;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.INamedQueryMapping;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;



/**
 * Definition of a named query, defined in the mapping metadata.
 * 
 * @author troyas
 */
public class NamedQueryDefinition implements INamedQueryMapping  {
	
	private static final long serialVersionUID = -3514934473385067518L;
	private String query;
	private boolean cacheable;
	private String cacheRegion;
	private Integer timeout;
	private Integer fetchSize;
	private String flushMode;
	private String name;
	
	// add tau 20.10.2005;
	private IMappingStorage mappingStorage;
	
	public NamedQueryDefinition(IMappingStorage mappingStorage, String name,String query, boolean cacheable,
			String cacheRegion, Integer timeout, Integer fetchSize,
			String flushMode) {
		this(mappingStorage, query,cacheable,cacheRegion,timeout,fetchSize,flushMode);
		this.name=name;
	}
	
	
	public NamedQueryDefinition(IMappingStorage mappingStorage, String query, boolean cacheable,
			String cacheRegion, Integer timeout, Integer fetchSize,
			String flushMode) {
		this.query = query;
		this.cacheable = cacheable;
		this.cacheRegion = cacheRegion;
		this.timeout = timeout;
		this.fetchSize = fetchSize;
		this.flushMode = flushMode;
		// add tau 20.10.2005
		this.mappingStorage = mappingStorage;		
	}

	public String getQueryString() {
		return query;
	}

	public boolean isCacheable() {
		return cacheable;
	}

	public String getCacheRegion() {
		return cacheRegion;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public String getFlushMode() {
		return flushMode;
	}

	public String toString() {
		return getClass().getName() + '(' + query + ')';
	}
	
	// 20050719 <yan>

	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}

	public void setCacheRegion(String cacheRegion) {
		this.cacheRegion = cacheRegion;
	}

	public void setFetchSize(Integer fetchSize) {
		this.fetchSize = fetchSize;
	}

	public void setFlushMode(String flushMode) {
		this.flushMode = flushMode;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	
	public void setQueryString(String query) {
		this.query=query;
	}

	public String getName() {
		return name;
	}


	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitNamedQueryMapping(this,argument);
	}


	public Object getAdapter(Class adapter) {
		return null;
	}


	public String getQualifiedName(TreeItem item) {
		//return (String) accept(QualifiedNameVisitor.visitor,item);
		return StringUtils.parentItemName(item, this.getName());
	}


	public IMappingStorage getStorage() {
		return mappingStorage;
	}


	//</yan>

}
