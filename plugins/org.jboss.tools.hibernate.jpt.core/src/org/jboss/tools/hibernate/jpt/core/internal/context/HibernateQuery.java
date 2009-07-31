/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.core.context.Query;
import org.eclipse.jpt.utility.model.Model;

/**
 * @author Dmitry Geraskov
 *
 */
public interface HibernateQuery extends Query, Model {
	
	// **************** flushMode ***************************************************
	
	FlushModeType getFlushMode();
	FlushModeType getSpecifiedFlushMode();
		String SPECIFIED_FLUSH_MODE_PROPERTY = "specifiedFlushMode"; //$NON-NLS-1$
	FlushModeType getDefaultFlushMode();
		String DEFAULT_FLUSH_MODE_PROPERTY = "defaultFlushMode"; //$NON-NLS-1$
		FlushModeType DEFAULT_FLUSH_MODE_TYPE = FlushModeType.AUTO;
	
	void setSpecifiedFlushMode(FlushModeType flushMode);
	
	//************************ cacheMode *********************************	
	
	CacheModeType getCacheMode();
	CacheModeType getSpecifiedCacheMode();
		String SPECIFIED_CACHE_MODE_PROPERTY = "specifiedCacheMode"; //$NON-NLS-1$
	CacheModeType getDefaultCacheMode();
		String DEFAULT_CACHE_MODE_PROPERTY = "defaultCacheMode"; //$NON-NLS-1$
		CacheModeType DEFAULT_CACHE_MODE_TYPE = CacheModeType.NORMAL;
	
	void setSpecifiedCacheMode(CacheModeType cacheMode);
	
	//************************ cacheable *********************************	
	boolean isCacheable();
	Boolean getSpecifiedCacheable();
	void setSpecifiedCacheable(Boolean value);
		String SPECIFIED_CACHEABLE_PROPERTY = "specifiedCacheable"; //$NON-NLS-1$
	
	boolean isDefaultCacheable();
		boolean DEFAULT_CACHEABLE = false;
		String DEFAULT_CACHEABLE_PROPERTY = "defaultCacheable"; //$NON-NLS-1$
		
	//************************ cacheRegion *********************************
	String getCacheRegion();
	String getSpecifiedCacheRegion();
	void setSpecifiedCacheRegion(String value);
		String SPECIFIED_CACHE_REGION_PROPERTY = "specifiedCacheRegion"; //$NON-NLS-1$
	
	String getDefaultCacheRegion();
		String DEFAULT_CACHE_REGION = ""; //$NON-NLS-1$
		String DEFAULT_CACHE_REGION_PROPERTY = "defaultCacheRegion"; //$NON-NLS-1$
	
	//************************ fetchSize *********************************
	int getFetchSize();
	
	Integer getSpecifiedFetchSize();
	void setSpecifiedFetchSize(Integer value);
		String SPECIFIED_FETCH_SIZE_PROPERTY = "specifiedFetchSize"; //$NON-NLS-1$

	int getDefaultFetchSize();
		int DEFAULT_FETCH_SIZE = -1;
		String DEFAULT_FETCH_SIZE_PROPERTY = "defaultFetchSize"; //$NON-NLS-1$
		
	//************************ timeout *********************************
	int getTimeout();
	
	Integer getSpecifiedTimeout();
	void setSpecifiedTimeout(Integer value);
		String SPECIFIED_TIMEOUT_PROPERTY = "specifiedTimeout"; //$NON-NLS-1$

	int getDefaultTimeout();
		int DEFAULT_TIMEOUT = -1;
		String DEFAULT_TIMEOUT_PROPERTY = "defaultTimeout"; //$NON-NLS-1$

	//************************ comment *********************************
	String getComment();
	String getSpecifiedComment();
	void setSpecifiedComment(String value);
		String SPECIFIED_COMMENT_PROPERTY = "specifiedComment"; //$NON-NLS-1$
	
	String getDefaultComment();
		String DEFAULT_COMMENT = ""; //$NON-NLS-1$
		String DEFAULT_COMMENT_PROPERTY = "defaultComment"; //$NON-NLS-1$
	
	//************************ readOnly *********************************	
	boolean isReadOnly();
	Boolean getSpecifiedReadOnly();
	void setSpecifiedReadOnly(Boolean value);
		String SPECIFIED_READ_ONLY_PROPERTY = "specifiedReadOnly"; //$NON-NLS-1$
	
	boolean isDefaultReadOnly();
		boolean DEFAULT_READ_ONLY = false;
		String DEFAULT_READ_ONLY_PROPERTY = "defaultReadOnly"; //$NON-NLS-1$

}
