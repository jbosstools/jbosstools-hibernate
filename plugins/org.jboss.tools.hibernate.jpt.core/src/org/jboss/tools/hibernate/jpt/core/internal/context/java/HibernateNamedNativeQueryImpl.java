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
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.GenericJavaNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.CacheModeType;
import org.jboss.tools.hibernate.jpt.core.internal.context.FlushModeType;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedNativeQueryAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateNamedNativeQueryImpl extends GenericJavaNamedNativeQuery
	implements HibernateNamedNativeQuery {

	private FlushModeType specifiedFlushMode;
	
	private CacheModeType specifiedCacheMode;
	
	private Boolean specifiedCacheable;
	
	private String specifiedCacheRegion;
	
	private Integer specifiedFetchSize;
	
	private Integer specifiedTimeout;
	
	private String specifiedComment;
	
	private Boolean specifiedReadOnly;
	
	private Boolean specifiedCallable;

	/**
	 * @param parent
	 */
	public HibernateNamedNativeQueryImpl(JavaJpaContextNode parent) {
		super(parent);
	}
	
	public void initialize(HibernateNamedNativeQueryAnnotation resourceQuery) {		
		this.specifiedFlushMode = this.getResourceFlushMode(resourceQuery);
		this.specifiedCacheMode = this.getResourceCacheMode(resourceQuery);
		this.specifiedCacheable = resourceQuery.isCacheable();
		this.specifiedCacheRegion = resourceQuery.getCacheRegion();
		this.specifiedFetchSize = resourceQuery.getFetchSize();
		this.specifiedTimeout = resourceQuery.getTimeout();
		this.specifiedComment = resourceQuery.getComment();
		this.specifiedReadOnly = resourceQuery.isReadOnly();
		this.specifiedCallable = resourceQuery.isCallable();
		super.initialize(resourceQuery);
	}
	
	public void update(HibernateNamedNativeQueryAnnotation resourceQuery) {
		this.setFlushMode_(resourceQuery.getFlushMode());
		this.setCacheMode_ (resourceQuery.getCacheMode());
		this.setSpecifiedCacheable_(resourceQuery.isCacheable());
		this.setSpecifiedCacheRegion_(resourceQuery.getCacheRegion());
		this.setSpecifiedFetchSize_(resourceQuery.getFetchSize());
		this.setSpecifiedTimeout_(resourceQuery.getTimeout());
		this.setSpecifiedComment_(resourceQuery.getComment());
		this.setSpecifiedReadOnly_(resourceQuery.isReadOnly());
		this.setSpecifiedCallable_(resourceQuery.isCallable());
		super.update(resourceQuery);
	}
	
	private CacheModeType getResourceCacheMode(HibernateNamedNativeQueryAnnotation resourceQuery) {
		return CacheModeType.fromJavaAnnotationValue(resourceQuery);
	}

	private FlushModeType getResourceFlushMode(HibernateNamedNativeQueryAnnotation resourceQuery) {
		return FlushModeType.fromJavaAnnotationValue(resourceQuery);
	}

	@Override
	protected HibernateNamedNativeQueryAnnotation getResourceQuery() {
		return (HibernateNamedNativeQueryAnnotation) super.getResourceQuery();
	}

	// ****** flushMode
	public FlushModeType getFlushMode() {
		return getSpecifiedFlushMode() == null ? getDefaultFlushMode() : getSpecifiedFlushMode();
	}	

	public FlushModeType getSpecifiedFlushMode() {
		return specifiedFlushMode;
	}
	
	public FlushModeType getDefaultFlushMode() {
		return HibernateNamedNativeQuery.DEFAULT_FLUSH_MODE_TYPE;
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
		return HibernateNamedNativeQuery.DEFAULT_CACHE_MODE_TYPE;
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
		return HibernateNamedNativeQuery.DEFAULT_CACHEABLE;
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
		return HibernateNamedNativeQuery.DEFAULT_CACHE_REGION;		
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
		return HibernateNamedNativeQuery.DEFAULT_FETCH_SIZE;		
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
		return HibernateNamedNativeQuery.DEFAULT_TIMEOUT;
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
		return HibernateNamedNativeQuery.DEFAULT_COMMENT;
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
		return HibernateNamedNativeQuery.DEFAULT_READ_ONLY;
	}

	//************************ callable *********************************	
	public boolean isCallable(){
		return (getSpecifiedCallable() == null ? isDefaultCallable()
				  : getSpecifiedCallable().booleanValue());
	}
	
	public Boolean getSpecifiedCallable(){
		return this.specifiedCallable;
	}
	
	public void setSpecifiedCallable(Boolean newSpecifiedCallable){
		Boolean oldSpecifiedCallable = this.specifiedCallable;
		this.specifiedCallable = newSpecifiedCallable;
		this.getResourceQuery().setCallable(newSpecifiedCallable);
		firePropertyChanged(SPECIFIED_CALLABLE_PROPERTY, oldSpecifiedCallable, newSpecifiedCallable);
	}
	
	public void setSpecifiedCallable_(Boolean callable){
		Boolean oldSpecifiedCallable = this.specifiedCallable;
		this.specifiedCallable = callable;
		firePropertyChanged(SPECIFIED_CALLABLE_PROPERTY, oldSpecifiedCallable, callable);
	}
	
	public boolean isDefaultCallable(){
		return HibernateNamedNativeQuery.DEFAULT_CALLABLE;
	}


}
