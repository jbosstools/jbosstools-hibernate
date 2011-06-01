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

import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.jpa.core.internal.context.java.AbstractJavaQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.CacheModeType;
import org.jboss.tools.hibernate.jpt.core.internal.context.FlushModeType;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateQueryAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractHibernateNamedQueryImpl<T extends HibernateQueryAnnotation> extends AbstractJavaQuery<T>
	implements HibernateJavaQuery {

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
	public AbstractHibernateNamedQueryImpl(JavaJpaContextNode parent, T queryAnnotation) {
		super(parent, queryAnnotation);
		this.specifiedFlushMode = this.getResourceFlushMode(queryAnnotation);
		this.specifiedCacheMode = this.getResourceCacheMode(queryAnnotation);
		this.specifiedCacheable = queryAnnotation.isCacheable();
		this.specifiedCacheRegion = queryAnnotation.getCacheRegion();
		this.specifiedFetchSize = queryAnnotation.getFetchSize();
		this.specifiedTimeout = queryAnnotation.getTimeout();
		this.specifiedComment = queryAnnotation.getComment();
		this.specifiedReadOnly = queryAnnotation.isReadOnly();
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		setSpecifiedFlushMode_(this.getResourceFlushMode(this.queryAnnotation));
		setSpecifiedCacheMode_(this.getResourceCacheMode(this.queryAnnotation));
		setSpecifiedCacheable_(this.queryAnnotation.isCacheable());
		setSpecifiedCacheRegion_(this.queryAnnotation.getCacheRegion());
		setSpecifiedFetchSize_(this.queryAnnotation.getFetchSize());
		setSpecifiedTimeout_(this.queryAnnotation.getTimeout());
		setSpecifiedComment_(this.queryAnnotation.getComment());
		setSpecifiedReadOnly_(this.queryAnnotation.isReadOnly());
	}

	private CacheModeType getResourceCacheMode(HibernateQueryAnnotation queryAnnotation) {
		return CacheModeType.fromJavaAnnotationValue(queryAnnotation);
	}

	private FlushModeType getResourceFlushMode(HibernateQueryAnnotation queryAnnotation) {
		return FlushModeType.fromJavaAnnotationValue(queryAnnotation);
	}

	// ****** flushMode
	@Override
	public FlushModeType getFlushMode() {
		return getSpecifiedFlushMode() == null ? getDefaultFlushMode() : getSpecifiedFlushMode();
	}

	@Override
	public FlushModeType getSpecifiedFlushMode() {
		return this.specifiedFlushMode;
	}

	@Override
	public FlushModeType getDefaultFlushMode() {
		return HibernateNamedQuery.DEFAULT_FLUSH_MODE_TYPE;
	}

	@Override
	public void setSpecifiedFlushMode(FlushModeType flushMode) {
		getQueryAnnotation().setFlushMode(flushMode);
		setSpecifiedFlushMode_(flushMode);
	}

	protected void setSpecifiedFlushMode_(FlushModeType flushMode) {
		FlushModeType oldFlushMode = this.specifiedFlushMode;
		this.specifiedFlushMode = flushMode;
		firePropertyChanged(SPECIFIED_FLUSH_MODE_PROPERTY, oldFlushMode, flushMode);
	}
	// ****** cacheMode
	@Override
	public CacheModeType getCacheMode() {
		return getSpecifiedCacheMode() == null ? getDefaultCacheMode() : getSpecifiedCacheMode();
	}

	@Override
	public CacheModeType getDefaultCacheMode() {
		return HibernateNamedQuery.DEFAULT_CACHE_MODE_TYPE;
	}

	@Override
	public CacheModeType getSpecifiedCacheMode() {
		return this.specifiedCacheMode;
	}

	@Override
	public void setSpecifiedCacheMode(CacheModeType cacheMode) {
		getQueryAnnotation().setCacheMode(cacheMode);
		this.setSpecifiedCacheMode_(cacheMode);
	}

	public void setSpecifiedCacheMode_(CacheModeType cacheMode) {
		CacheModeType oldCacheMode = this.specifiedCacheMode;
		this.specifiedCacheMode = cacheMode;
		firePropertyChanged(SPECIFIED_CACHE_MODE_PROPERTY, oldCacheMode, cacheMode);
	}
	//************************ cacheable *********************************
	@Override
	public boolean isCacheable(){
		return (this.getSpecifiedCacheable() == null) ? this.isDefaultCacheable()
													  : this.getSpecifiedCacheable().booleanValue();
	}

	@Override
	public Boolean getSpecifiedCacheable(){
		return this.specifiedCacheable;
	}

	@Override
	public void setSpecifiedCacheable(Boolean newSpecifiedCacheable) {
		this.getQueryAnnotation().setCacheable(newSpecifiedCacheable);
		setSpecifiedCacheable_(newSpecifiedCacheable);

	}

	public void setSpecifiedCacheable_(Boolean newSpecifiedCacheable) {
		Boolean oldSpecifiedCacheable = this.specifiedCacheable;
		this.specifiedCacheable = newSpecifiedCacheable;
		firePropertyChanged(SPECIFIED_CACHEABLE_PROPERTY, oldSpecifiedCacheable, newSpecifiedCacheable);
	}

	@Override
	public boolean isDefaultCacheable() {
		return HibernateNamedQuery.DEFAULT_CACHEABLE;
	}
	//************************ cacheRegion *********************************
	@Override
	public String getCacheRegion(){
		return (getSpecifiedCacheRegion() == null ? getDefaultCacheRegion()
												  : getSpecifiedCacheRegion());
	}
	@Override
	public String getSpecifiedCacheRegion(){
		return this.specifiedCacheRegion;
	}

	@Override
	public void setSpecifiedCacheRegion(String cacheRegion){
		this.getQueryAnnotation().setCacheRegion(cacheRegion);
		setSpecifiedCacheRegion_(cacheRegion);
	}

	public void setSpecifiedCacheRegion_(String cacheRegion){
		String oldSpecifiedCacheRegion = this.specifiedCacheRegion;
		this.specifiedCacheRegion = cacheRegion;
		firePropertyChanged(SPECIFIED_CACHE_REGION_PROPERTY, oldSpecifiedCacheRegion, cacheRegion);
	}

	@Override
	public String getDefaultCacheRegion(){
		return HibernateNamedQuery.DEFAULT_CACHE_REGION;
	}
	//************************ fetchSize *********************************
	@Override
	public int getFetchSize(){
		return (getSpecifiedFetchSize() == null ? getDefaultFetchSize()
				  : getSpecifiedFetchSize().intValue());
	}

	@Override
	public Integer getSpecifiedFetchSize(){
		return this.specifiedFetchSize;
	}

	@Override
	public void setSpecifiedFetchSize(Integer newSpecifiedFetchSize){
		this.getQueryAnnotation().setFetchSize(newSpecifiedFetchSize);
		setSpecifiedFetchSize_(newSpecifiedFetchSize);
	}

	public void setSpecifiedFetchSize_(Integer fetchSize){
		Integer oldSpecifiedFetchSize = this.specifiedFetchSize;
		this.specifiedFetchSize = fetchSize;
		firePropertyChanged(SPECIFIED_FETCH_SIZE_PROPERTY, oldSpecifiedFetchSize, fetchSize);
	}

	@Override
	public int getDefaultFetchSize(){
		return HibernateNamedQuery.DEFAULT_FETCH_SIZE;
	}
	//************************ timeout *********************************
	@Override
	public int getTimeout(){
		return (getSpecifiedTimeout() == null ? getDefaultTimeout()
				  : getSpecifiedTimeout().intValue());
	}

	@Override
	public Integer getSpecifiedTimeout(){
		return this.specifiedTimeout;
	}

	@Override
	public void setSpecifiedTimeout(Integer newSpecifiedTimeout){
		this.getQueryAnnotation().setTimeout(newSpecifiedTimeout);
		setSpecifiedTimeout_(newSpecifiedTimeout);
	}

	public void setSpecifiedTimeout_(Integer timeout){
		Integer oldSpecifiedTimeout = this.specifiedTimeout;
		this.specifiedTimeout = timeout;
		firePropertyChanged(SPECIFIED_TIMEOUT_PROPERTY, oldSpecifiedTimeout, timeout);
	}

	@Override
	public int getDefaultTimeout(){
		return HibernateNamedQuery.DEFAULT_TIMEOUT;
	}
	//************************ comment *********************************
	@Override
	public String getComment(){
		return (getSpecifiedComment() == null ? getDefaultComment()
				  : getSpecifiedComment());
	}
	@Override
	public String getSpecifiedComment(){
		return this.specifiedComment;
	}

	@Override
	public void setSpecifiedComment(String newSpecifiedComment){
		this.getQueryAnnotation().setComment(newSpecifiedComment);
		setSpecifiedComment_(newSpecifiedComment);
	}

	public void setSpecifiedComment_(String comment){
		String oldSpecifiedComment = this.specifiedComment;
		this.specifiedComment = comment;
		firePropertyChanged(SPECIFIED_COMMENT_PROPERTY, oldSpecifiedComment, comment);
	}

	@Override
	public String getDefaultComment(){
		return HibernateNamedQuery.DEFAULT_COMMENT;
	}

	//************************ readOnly *********************************
	@Override
	public boolean isReadOnly(){
		return (getSpecifiedReadOnly() == null ? isDefaultReadOnly()
				  : getSpecifiedReadOnly().booleanValue());
	}

	@Override
	public Boolean getSpecifiedReadOnly(){
		return this.specifiedReadOnly;
	}

	@Override
	public void setSpecifiedReadOnly(Boolean newSpecifiedReadOnly){
		this.getQueryAnnotation().setReadOnly(newSpecifiedReadOnly);
		setSpecifiedReadOnly_(newSpecifiedReadOnly);
	}

	public void setSpecifiedReadOnly_(Boolean readOnly){
		Boolean oldSpecifiedReadOnly = this.specifiedReadOnly;
		this.specifiedReadOnly = readOnly;
		firePropertyChanged(SPECIFIED_READ_ONLY_PROPERTY, oldSpecifiedReadOnly, readOnly);
	}

	@Override
	public boolean isDefaultReadOnly(){
		return HibernateNamedQuery.DEFAULT_READ_ONLY;
	}
}
