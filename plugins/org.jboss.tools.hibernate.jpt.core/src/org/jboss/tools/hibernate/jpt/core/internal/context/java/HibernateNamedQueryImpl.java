/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.GenericJavaNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueryAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateNamedQueryImpl extends GenericJavaNamedQuery
	implements HibernateNamedQuery {
	
	private FlushModeType specifiedFlushMode;
	
	private CacheModeType specifiedCacheMode;
	
	private Boolean specifiedCacheable;
	
	private String specifiedCacheRegion;
	
	private Integer specifiedFetchSize;
	
	private Integer specifiedTimeout;
	
	private String specifiedComment;
	
	private Boolean specifiedReadOnly;

	/**
	 * @param parent
	 */
	public HibernateNamedQueryImpl(JavaJpaContextNode parent) {
		super(parent);
	}
	
	public void initialize(HibernateNamedQueryAnnotation resourceQuery) {		
		this.specifiedFlushMode = this.getResourceFlushMode(resourceQuery);
		this.specifiedCacheMode = this.getResourceCacheMode(resourceQuery);
		this.specifiedCacheable = resourceQuery.isCacheable();
		this.specifiedCacheRegion = resourceQuery.getCacheRegion();
		this.specifiedFetchSize = resourceQuery.getFetchSize();
		this.specifiedTimeout = resourceQuery.getTimeout();
		this.specifiedComment = resourceQuery.getComment();
		this.specifiedReadOnly = resourceQuery.isReadOnly();
		super.initialize(resourceQuery);
	}
	
	public void update(HibernateNamedQueryAnnotation resourceQuery) {
		this.setFlushMode_(resourceQuery.getFlushMode());
		this.setCacheMode_ (resourceQuery.getCacheMode());
		this.setSpecifiedCacheable_(resourceQuery.isCacheable());
		this.setSpecifiedCacheRegion_(resourceQuery.getCacheRegion());
		this.setSpecifiedFetchSize_(resourceQuery.getFetchSize());
		this.setSpecifiedTimeout_(resourceQuery.getTimeout());
		this.setSpecifiedComment_(resourceQuery.getComment());
		this.setSpecifiedReadOnly_(resourceQuery.isReadOnly());
		super.update(resourceQuery);
	}
	
	private CacheModeType getResourceCacheMode(HibernateNamedQueryAnnotation resourceQuery) {
		return CacheModeType.fromJavaAnnotationValue(resourceQuery);
	}

	private FlushModeType getResourceFlushMode(HibernateNamedQueryAnnotation resourceQuery) {
		return FlushModeType.fromJavaAnnotationValue(resourceQuery);
	}

	@Override
	protected HibernateNamedQueryAnnotation getResourceQuery() {
		return (HibernateNamedQueryAnnotation) super.getResourceQuery();
	}

	// ****** flushMode
	public FlushModeType getFlushMode() {
		return getSpecifiedFlushMode() == null ? getDefaultFlushMode() : getSpecifiedFlushMode();
	}	

	public FlushModeType getSpecifiedFlushMode() {
		return specifiedFlushMode;
	}
	
	public FlushModeType getDefaultFlushMode() {
		return HibernateNamedQuery.DEFAULT_FLUSH_MODE_TYPE;
	}	

	public void setSpecifiedFlushMode(FlushModeType flushMode) {
		FlushModeType oldFlushMode = this.specifiedFlushMode;
		this.specifiedFlushMode = flushMode;
		getResourceQuery().setFlushMode(flushMode);
		firePropertyChanged(SPECIFIED_FLUSH_MODE_PROPERTY, oldFlushMode, flushMode);		
	}
	
	protected void setFlushMode_(FlushModeType flushMode) {
		FlushModeType oldFlushMode = this.specifiedFlushMode;
		this.specifiedFlushMode = flushMode;
		firePropertyChanged(SPECIFIED_FLUSH_MODE_PROPERTY, oldFlushMode, flushMode);
	}
	// ****** cacheMode
	public CacheModeType getCacheMode() {
		return getSpecifiedCacheMode() == null ? getDefaultCacheMode() : getSpecifiedCacheMode();
	}
	
	public CacheModeType getDefaultCacheMode() {
		return HibernateNamedQuery.DEFAULT_CACHE_MODE_TYPE;
	}

	public CacheModeType getSpecifiedCacheMode() {
		return specifiedCacheMode;
	}

	public void setSpecifiedCacheMode(CacheModeType cacheMode) {
		CacheModeType oldCacheMode = this.specifiedCacheMode;
		this.specifiedCacheMode = cacheMode;
		getResourceQuery().setCacheMode(cacheMode);
		firePropertyChanged(SPECIFIED_CACHE_MODE_PROPERTY, oldCacheMode, cacheMode);		
	}
	
	public void setCacheMode_(CacheModeType cacheMode) {
		CacheModeType oldCacheMode = this.specifiedCacheMode;
		this.specifiedCacheMode = cacheMode;
		firePropertyChanged(SPECIFIED_CACHE_MODE_PROPERTY, oldCacheMode, cacheMode);		
	}	
	//************************ cacheable *********************************	
	public boolean isCacheable(){
		return (this.getSpecifiedCacheable() == null) ? this.isDefaultCacheable() 
													  : this.getSpecifiedCacheable().booleanValue();
	}
	
	public Boolean getSpecifiedCacheable(){
		return this.specifiedCacheable;
	}
	
	public void setSpecifiedCacheable(Boolean newSpecifiedCacheable) {
		Boolean oldSpecifiedCacheable = this.specifiedCacheable;
		this.specifiedCacheable = newSpecifiedCacheable;
		this.getResourceQuery().setCacheable(newSpecifiedCacheable);
		firePropertyChanged(SPECIFIED_CACHEABLE_PROPERTY, oldSpecifiedCacheable, newSpecifiedCacheable);

	}
	
	public void setSpecifiedCacheable_(Boolean newSpecifiedCacheable) {
		Boolean oldSpecifiedCacheable = this.specifiedCacheable;
		this.specifiedCacheable = newSpecifiedCacheable;
		firePropertyChanged(SPECIFIED_CACHEABLE_PROPERTY, oldSpecifiedCacheable, newSpecifiedCacheable);
	}
	
	public boolean isDefaultCacheable() {
		return HibernateNamedQuery.DEFAULT_CACHEABLE;
	}
	//************************ cacheRegion *********************************
	public String getCacheRegion(){
		return (getSpecifiedCacheRegion() == null ? getDefaultCacheRegion()
												  : getSpecifiedCacheRegion());
	}
	public String getSpecifiedCacheRegion(){
		return this.specifiedCacheRegion;
	}
	
	public void setSpecifiedCacheRegion(String cacheRegion){
		String oldSpecifiedCacheRegion = this.specifiedCacheRegion;
		this.specifiedCacheRegion = cacheRegion;
		this.getResourceQuery().setCacheRegion(cacheRegion);
		firePropertyChanged(SPECIFIED_CACHE_REGION_PROPERTY, oldSpecifiedCacheRegion, cacheRegion);
	}
	
	public void setSpecifiedCacheRegion_(String cacheRegion){
		String oldSpecifiedCacheRegion = this.specifiedCacheRegion;
		this.specifiedCacheRegion = cacheRegion;
		firePropertyChanged(SPECIFIED_CACHE_REGION_PROPERTY, oldSpecifiedCacheRegion, cacheRegion);
	}
		
	public String getDefaultCacheRegion(){
		return HibernateNamedQuery.DEFAULT_CACHE_REGION;		
	}
	//************************ fetchSize *********************************
	public int getFetchSize(){
		return (getSpecifiedFetchSize() == null ? getDefaultFetchSize()
				  : getSpecifiedFetchSize().intValue());
	}
	
	public Integer getSpecifiedFetchSize(){
		return this.specifiedFetchSize;
	}
	
	public void setSpecifiedFetchSize(Integer newSpecifiedFetchSize){
		Integer oldSpecifiedFetchSize = this.specifiedFetchSize;
		this.specifiedFetchSize = newSpecifiedFetchSize;
		this.getResourceQuery().setFetchSize(newSpecifiedFetchSize);
		firePropertyChanged(SPECIFIED_FETCH_SIZE_PROPERTY, oldSpecifiedFetchSize, newSpecifiedFetchSize);
	}
	
	public void setSpecifiedFetchSize_(Integer fetchSize){
		Integer oldSpecifiedFetchSize = this.specifiedFetchSize;
		this.specifiedFetchSize = fetchSize;
		firePropertyChanged(SPECIFIED_FETCH_SIZE_PROPERTY, oldSpecifiedFetchSize, fetchSize);
	}

	public int getDefaultFetchSize(){
		return HibernateNamedQuery.DEFAULT_FETCH_SIZE;		
	}
	//************************ timeout *********************************
	public int getTimeout(){
		return (getSpecifiedTimeout() == null ? getDefaultTimeout()
				  : getSpecifiedTimeout().intValue());
	}
	
	public Integer getSpecifiedTimeout(){
		return this.specifiedTimeout;
	}
	
	public void setSpecifiedTimeout(Integer newSpecifiedTimeout){
		Integer oldSpecifiedTimeout = this.specifiedTimeout;
		this.specifiedTimeout = newSpecifiedTimeout;
		this.getResourceQuery().setTimeout(newSpecifiedTimeout);
		firePropertyChanged(SPECIFIED_TIMEOUT_PROPERTY, oldSpecifiedTimeout, newSpecifiedTimeout);
	}
	
	public void setSpecifiedTimeout_(Integer timeout){
		Integer oldSpecifiedTimeout = this.specifiedTimeout;
		this.specifiedTimeout = timeout;
		firePropertyChanged(SPECIFIED_TIMEOUT_PROPERTY, oldSpecifiedTimeout, timeout);
	}

	public int getDefaultTimeout(){
		return HibernateNamedQuery.DEFAULT_TIMEOUT;
	}
	//************************ comment *********************************
	public String getComment(){
		return (getSpecifiedComment() == null ? getDefaultComment()
				  : getSpecifiedComment());
	}
	public String getSpecifiedComment(){
		return this.specifiedComment;
	}
	
	public void setSpecifiedComment(String newSpecifiedComment){
		String oldSpecifiedComment = this.specifiedComment;
		this.specifiedComment = newSpecifiedComment;
		this.getResourceQuery().setComment(newSpecifiedComment);
		firePropertyChanged(SPECIFIED_COMMENT_PROPERTY, oldSpecifiedComment, newSpecifiedComment);
	}
	
	public void setSpecifiedComment_(String comment){
		String oldSpecifiedComment = this.specifiedComment;
		this.specifiedComment = comment;
		firePropertyChanged(SPECIFIED_COMMENT_PROPERTY, oldSpecifiedComment, comment);
	}
	
	public String getDefaultComment(){
		return HibernateNamedQuery.DEFAULT_COMMENT;
	}
	
	//************************ readOnly *********************************	
	public boolean isReadOnly(){
		return (getSpecifiedReadOnly() == null ? isDefaultReadOnly()
				  : getSpecifiedReadOnly().booleanValue());
	}
	
	public Boolean getSpecifiedReadOnly(){
		return this.specifiedReadOnly;
	}
	
	public void setSpecifiedReadOnly(Boolean newSpecifiedReadOnly){
		Boolean oldSpecifiedReadOnly = this.specifiedReadOnly;
		this.specifiedReadOnly = newSpecifiedReadOnly;
		this.getResourceQuery().setReadOnly(newSpecifiedReadOnly);
		firePropertyChanged(SPECIFIED_READ_ONLY_PROPERTY, oldSpecifiedReadOnly, newSpecifiedReadOnly);
	}
	
	public void setSpecifiedReadOnly_(Boolean readOnly){
		Boolean oldSpecifiedReadOnly = this.specifiedReadOnly;
		this.specifiedReadOnly = readOnly;
		firePropertyChanged(SPECIFIED_READ_ONLY_PROPERTY, oldSpecifiedReadOnly, readOnly);
	}
	
	public boolean isDefaultReadOnly(){
		return HibernateNamedQuery.DEFAULT_READ_ONLY;
	}
}
