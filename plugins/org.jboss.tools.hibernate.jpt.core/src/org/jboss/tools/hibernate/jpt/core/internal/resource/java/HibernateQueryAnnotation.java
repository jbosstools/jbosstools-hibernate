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
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.utility.TextRange;
import org.jboss.tools.hibernate.jpt.core.internal.context.CacheModeType;
import org.jboss.tools.hibernate.jpt.core.internal.context.FlushModeType;

/**
 * @author Dmitry Geraskov
 * 
 * Interface contains Hibernate specific attributes.
 */
public interface HibernateQueryAnnotation {
	
	//************************ flushMode *********************************
	
	/**
	 * Corresponds to the 'flushMode' element of the *NamedQuery annotation.
	 * Return null if the element does not exist in Java.
	 */
	FlushModeType getFlushMode();
		String FLUSH_MODE_PROPERTY = "flushMode"; //$NON-NLS-1$

	/**
	 * Corresponds to the 'flushMode' element of the *NamedQuery annotation.
	 * Set to null to remove the element.
	 */
	void setFlushMode(FlushModeType flushMode);
	
	/**
	 * Return the {@link TextRange} for the 'flushMode' element. If element
	 * does not exist return the {@link TextRange} for the *NamedQuery annotation.
	 */
	TextRange getFlushModeTextRange(CompilationUnit astRoot);
	
	//************************ cacheMode *********************************
	
	/**
	 * Corresponds to the 'cacheMode' element of the *NamedQuery annotation.
	 * Return null if the element does not exist in Java.
	 */
	CacheModeType getCacheMode();	
		String CACHE_MODE_PROPERTY = "cacheMode"; //$NON-NLS-1$

	/**
	 * Corresponds to the 'cacheMode' element of the *NamedQuery annotation.
	 * Set to null to remove the element.
	 */
	void setCacheMode(CacheModeType cacheMode);
	
	/**
	 * Return the {@link TextRange} for the 'cacheMode' element. If element
	 * does not exist return the {@link TextRange} for the *NamedQuery annotation.
	 */
	TextRange getCacheModeTextRange(CompilationUnit astRoot);
	
	//************************ cacheable *********************************	
	Boolean isCacheable();
	void setCacheable(Boolean value);
		String CACHEABLE_PROPERTY = "cacheable"; //$NON-NLS-1$
	
	//************************ cacheRegion *********************************
	String getCacheRegion();
	void setCacheRegion(String value);
		String CACHE_REGION_PROPERTY = "cacheRegion"; //$NON-NLS-1$
		
	//************************ fetchSize *********************************
	Integer getFetchSize();
	void setFetchSize(Integer value);
		String FETCH_SIZE_PROPERTY = "fetchSize"; //$NON-NLS-1$
		
	//************************ timeout *********************************
	Integer getTimeout();	
	void setTimeout(Integer value);
		String TIMEOUT_PROPERTY = "timeout"; //$NON-NLS-1$

	//************************ comment *********************************
	String getComment();
	void setComment(String value);
		String COMMENT_PROPERTY = "comment"; //$NON-NLS-1$
	
	//************************ readOnly *********************************	
	Boolean isReadOnly();
	void setReadOnly(Boolean value);
		String READ_ONLY_PROPERTY = "readOnly"; //$NON-NLS-1$

}
