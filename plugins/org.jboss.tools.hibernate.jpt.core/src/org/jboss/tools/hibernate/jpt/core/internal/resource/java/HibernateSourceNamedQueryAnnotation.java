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

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.common.core.internal.utility.jdt.CombinationIndexedDeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.ConversionDeclarationAnnotationElementAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.ElementIndexedAnnotationAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.EnumDeclarationAnnotationElementAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.ShortCircuitAnnotationElementAdapter;
import org.eclipse.jpt.common.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.resource.java.JavaResourceModel;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.common.core.utility.jdt.AnnotatedElement;
import org.eclipse.jpt.common.core.utility.jdt.AnnotationAdapter;
import org.eclipse.jpt.common.core.utility.jdt.AnnotationElementAdapter;
import org.eclipse.jpt.common.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.common.core.utility.jdt.DeclarationAnnotationElementAdapter;
import org.eclipse.jpt.common.core.utility.jdt.IndexedAnnotationAdapter;
import org.eclipse.jpt.common.core.utility.jdt.IndexedDeclarationAnnotationAdapter;
import org.eclipse.jpt.common.utility.internal.iterable.EmptyListIterable;
import org.eclipse.jpt.common.utility.iterable.ListIterable;
import org.eclipse.jpt.jpa.core.resource.java.QueryHintAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.CacheModeType;
import org.jboss.tools.hibernate.jpt.core.internal.context.FlushModeType;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateSourceNamedQueryAnnotation extends SourceAnnotation
	implements HibernateNamedQueryAnnotation {

	public static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);
	private static final DeclarationAnnotationAdapter CONTAINER_DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(Hibernate.NAMED_QUERIES);
	
	private DeclarationAnnotationElementAdapter<String> nameDeclarationAdapter;
	private AnnotationElementAdapter<String> nameAdapter;
	private String name;
	TextRange nameTextRange;

	private DeclarationAnnotationElementAdapter<String> queryDeclarationAdapter;
	private AnnotationElementAdapter<String> queryAdapter;
	private String query;
	List<TextRange> queryTextRanges;
//	TextRange queryTextRange;

	private DeclarationAnnotationElementAdapter<String> flushModeDeclarationAdapter;
	private AnnotationElementAdapter<String> flushModeAdapter;
	private FlushModeType flushMode;
	TextRange flushModeTextRange;

	private DeclarationAnnotationElementAdapter<String> cacheModeDeclarationAdapter;
	private AnnotationElementAdapter<String> cacheModeAdapter;
	private CacheModeType cacheMode;
	TextRange cacheModeTextRange;

	private DeclarationAnnotationElementAdapter<Boolean> cacheableDeclarationAdapter;
	private AnnotationElementAdapter<Boolean> cacheableAdapter;
	private Boolean cacheable;

	private DeclarationAnnotationElementAdapter<String> cacheRegionDeclarationAdapter;
	private AnnotationElementAdapter<String> cacheRegionAdapter;
	private String cacheRegion;

	private DeclarationAnnotationElementAdapter<Integer> fetchSizeDeclarationAdapter;
	private AnnotationElementAdapter<Integer> fetchSizeAdapter;
	private Integer fetchSize;

	private DeclarationAnnotationElementAdapter<Integer> timeoutDeclarationAdapter;
	private AnnotationElementAdapter<Integer> timeoutAdapter;
	private Integer timeout;

	private DeclarationAnnotationElementAdapter<String> commentDeclarationAdapter;
	private AnnotationElementAdapter<String> commentAdapter;
	private String comment;

	private DeclarationAnnotationElementAdapter<Boolean> readOnlyDeclarationAdapter;
	private AnnotationElementAdapter<Boolean> readOnlyAdapter;
	private Boolean readOnly;
	
	private CompilationUnit astRoot;


	HibernateSourceNamedQueryAnnotation(JavaResourceModel parent, AnnotatedElement annotatedElement, DeclarationAnnotationAdapter daa, AnnotationAdapter annotationAdapter) {
		super(parent, annotatedElement, daa, annotationAdapter);
		this.nameDeclarationAdapter = this.buildNameAdapter(daa);
		this.nameAdapter = this.buildAdapter(this.nameDeclarationAdapter);
		this.queryDeclarationAdapter = this.buildQueryAdapter(daa);
		this.queryAdapter = this.buildAdapter(this.queryDeclarationAdapter);
		this.flushModeDeclarationAdapter = this.buildFlushModeAdapter(daa);
		this.flushModeAdapter = new ShortCircuitAnnotationElementAdapter<String>(annotatedElement, this.flushModeDeclarationAdapter);
		this.cacheModeDeclarationAdapter = this.buildCacheModeAdapter(daa);
		this.cacheModeAdapter = new ShortCircuitAnnotationElementAdapter<String>(annotatedElement, this.cacheModeDeclarationAdapter);
		this.cacheableDeclarationAdapter = this.buildCacheableAdapter(daa);
		this.cacheableAdapter = new ShortCircuitAnnotationElementAdapter<Boolean>(annotatedElement, this.cacheableDeclarationAdapter);
		this.cacheRegionDeclarationAdapter = this.buildCacheRegionAdapter(daa);
		this.cacheRegionAdapter = this.buildAdapter(this.cacheRegionDeclarationAdapter);
		this.fetchSizeDeclarationAdapter = this.buildFetchSizeAdapter(daa);
		this.fetchSizeAdapter = new ShortCircuitAnnotationElementAdapter<Integer>(annotatedElement, this.fetchSizeDeclarationAdapter);
		this.timeoutDeclarationAdapter = this.buildTimeoutAdapter(daa);
		this.timeoutAdapter = new ShortCircuitAnnotationElementAdapter<Integer>(annotatedElement, this.timeoutDeclarationAdapter);
		this.commentDeclarationAdapter = this.buildCommentAdapter(daa);
		this.commentAdapter = new ShortCircuitAnnotationElementAdapter<String>(annotatedElement, this.commentDeclarationAdapter);
		this.readOnlyDeclarationAdapter = this.buildReadOnlyAdapter(daa);
		this.readOnlyAdapter = new ShortCircuitAnnotationElementAdapter<Boolean>(annotatedElement, this.readOnlyDeclarationAdapter);
	}

	@Override
	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}

	@Override
	public void initialize(CompilationUnit astRoot) {
		this.astRoot = astRoot;
		this.name = this.buildName(astRoot);
		this.nameTextRange = this.buildNameTextRange();
		this.query = this.buildQuery(astRoot);
		this.queryTextRanges = this.buildQueryTextRanges(astRoot);
		this.flushMode = this.buildFlushMode(astRoot);
		this.flushModeTextRange =  this.buildFlushModeTextRange(astRoot);
		this.cacheMode = this.buildCacheMode(astRoot);
		this.cacheModeTextRange = this.buildCacheModeTextRange(astRoot);
		this.cacheable = this.buildCacheable(astRoot);
		this.cacheRegion = this.buildCacheRegion(astRoot);
		this.fetchSize = this.buildFetchSize(astRoot);
		this.timeout = this.buildTimeout(astRoot);
		this.comment = this.buildComment(astRoot);
		this.readOnly = this.buildReadOnly(astRoot);
	}

	@Override
	public void synchronizeWith(CompilationUnit astRoot) {
		this.astRoot = astRoot;
		this.syncName(this.buildName(astRoot));
		this.nameTextRange = this.buildNameTextRange();
		this.syncQuery(this.buildQuery(astRoot));
		this.queryTextRanges = this.buildQueryTextRanges(astRoot);
		this.syncFlushMode(this.buildFlushMode(astRoot));
		this.flushModeTextRange =  this.buildFlushModeTextRange(astRoot);
		this.syncCacheMode(this.buildCacheMode(astRoot));
		this.cacheModeTextRange = this.buildCacheModeTextRange(astRoot);
		this.syncCacheable(this.buildCacheable(astRoot));
		this.syncCacheRegion(this.buildCacheRegion(astRoot));
		this.syncFetchSize(this.buildFetchSize(astRoot));
		this.syncTimeout(this.buildTimeout(astRoot));
		this.syncComment(this.buildComment(astRoot));
		this.syncReadOnly(this.buildReadOnly(astRoot));
	}

	/**
	 * convenience method
	 */
	AnnotationElementAdapter<String> buildAdapter(DeclarationAnnotationElementAdapter<String> daea) {
		return new ShortCircuitAnnotationElementAdapter<String>(this.annotatedElement, daea);
	}

	// ********** BaseNamedQueryAnnotation implementation **********

	// ***** name
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		if (this.attributeValueHasChanged(this.name, name)) {
			this.name = name;
			this.nameAdapter.setValue(name);
		}
	}

	private void syncName(String astName) {
		String old = this.name;
		this.name = astName;
		this.firePropertyChanged(NAME_PROPERTY, old, astName);
	}

	private String buildName(CompilationUnit astRoot) {
		return this.nameAdapter.getValue(astRoot);
	}
	
	@Override
	public TextRange getNameTextRange() {
		return this.nameTextRange;
	}

	private TextRange buildNameTextRange() {
		return this.getElementTextRange(this.nameDeclarationAdapter, getAstAnnotation(astRoot));
	}
	
	// ***** query
	@Override
	public String getQuery() {
		return this.query;
	}

	@Override
	public void setQuery(String query) {
		if (this.attributeValueHasChanged(this.query, query)) {
			this.query = query;
			this.queryAdapter.setValue(query);
		}
	}

	private void syncQuery(String annotationQuery) {
		String old = this.query;
		this.query = annotationQuery;
		this.firePropertyChanged(QUERY_PROPERTY, old, annotationQuery);
	}

	private String buildQuery(CompilationUnit astRoot) {
		return this.queryAdapter.getValue(astRoot);
	}

	@Override
	public List<TextRange> getQueryTextRanges() {
		return this.queryTextRanges;
	}
	
	private List<TextRange> buildQueryTextRanges(CompilationUnit astRoot) {
		return this.getElementTextRanges(this.queryDeclarationAdapter, getAstAnnotation(astRoot));
	}

	// ***** hints
	@Override
	public ListIterable<QueryHintAnnotation> getHints() {
		return EmptyListIterable.instance();
	}

	@Override
	public int getHintsSize() {
		return 0;
	}

	@Override
	public QueryHintAnnotation hintAt(int index) {
		return null;
	}

	@Override
	public QueryHintAnnotation addHint(int index) {
		return null;
	}

	@Override
	public void moveHint(int targetIndex, int sourceIndex) {
		//nothing to do
	}

	@Override
	public void removeHint(int index) {
		//nothing to do
	}
	// ******************** HibernateNamedQueryAnnotation implementation *************
	// ***** flushMode

	@Override
	public FlushModeType getFlushMode() {
		return this.flushMode;
	}

	@Override
	public void setFlushMode(FlushModeType flushMode) {
		if (this.attributeValueHasChanged(this.flushMode, flushMode)) {
			this.flushMode = flushMode;
			this.flushModeAdapter.setValue(FlushModeType.toJavaAnnotationValue(flushMode));
		}
	}

	private void syncFlushMode(FlushModeType flushMode) {
		FlushModeType old = this.flushMode;
		this.flushMode = flushMode;
		this.firePropertyChanged(FLUSH_MODE_PROPERTY, old, flushMode);
	}
	
	private FlushModeType buildFlushMode(CompilationUnit astRoot) {
		return FlushModeType.fromJavaAnnotationValue(this.flushModeAdapter.getValue(astRoot));
	}

	protected TextRange buildFlushModeTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(this.flushModeDeclarationAdapter, getAstAnnotation(astRoot));
	}
	
	@Override
	public TextRange getFlushModeTextRange() {
		return this.flushModeTextRange;
	}

	// ***** caheMode

	@Override
	public CacheModeType getCacheMode() {
		return this.cacheMode;
	}

	@Override
	public void setCacheMode(CacheModeType cacheMode) {
		if (this.attributeValueHasChanged(this.cacheMode, cacheMode)) {
			this.cacheMode = cacheMode;
			this.cacheModeAdapter.setValue(CacheModeType.toJavaAnnotationValue(cacheMode));
		}
	}

	private void syncCacheMode(CacheModeType cacheMode) {
		CacheModeType old = this.cacheMode;
		this.cacheMode = cacheMode;
		this.firePropertyChanged(CACHE_MODE_PROPERTY, old, cacheMode);
	}

	private CacheModeType buildCacheMode(CompilationUnit astRoot) {
		return CacheModeType.fromJavaAnnotationValue(this.cacheModeAdapter.getValue(astRoot));
	}

	protected TextRange buildCacheModeTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(this.flushModeDeclarationAdapter, getAstAnnotation(astRoot));
	}
	
	@Override
	public TextRange getCacheModeTextRange() {
		return this.cacheModeTextRange;
	}

	//************************ cacheable *********************************
	@Override
	public Boolean isCacheable(){
		return this.cacheable;
	}

	@Override
	public void setCacheable(Boolean cacheable){
		if (this.attributeValueHasChanged(this.cacheable, cacheable)) {
			this.cacheable = cacheable;
			this.cacheableAdapter.setValue(cacheable);
		}
	}

	private void syncCacheable(Boolean cacheable) {
		Boolean old = this.cacheable;
		this.cacheable = cacheable;
		this.firePropertyChanged(CACHEABLE_PROPERTY, old, cacheable);
	}

	private Boolean buildCacheable(CompilationUnit astRoot) {
		return this.cacheableAdapter.getValue(astRoot);
	}

	//************************ cacheRegion *********************************
	@Override
	public String getCacheRegion(){
		return this.cacheRegion;
	}

	@Override
	public void setCacheRegion(String cacheRegion){
		if (this.attributeValueHasChanged(this.cacheRegion, cacheRegion)) {
			this.cacheRegion = cacheRegion;
			this.cacheRegionAdapter.setValue(cacheRegion);
		}
	}

	private void syncCacheRegion(String cacheRegion) {
		String old = this.cacheRegion;
		this.cacheRegion = cacheRegion;
		this.firePropertyChanged(CACHE_REGION_PROPERTY, old, cacheRegion);
	}

	private String buildCacheRegion(CompilationUnit astRoot) {
		return this.cacheRegionAdapter.getValue(astRoot);
	}
	//************************ fetchSize *********************************
	@Override
	public Integer getFetchSize(){
		return this.fetchSize;
	}

	@Override
	public void setFetchSize(Integer fetchSize){
		if (this.attributeValueHasChanged(this.fetchSize, fetchSize)) {
			this.fetchSize = fetchSize;
			this.fetchSizeAdapter.setValue(fetchSize);
		}
	}

	private void syncFetchSize(Integer fetchSize) {
		Integer old = this.fetchSize;
		this.fetchSize = fetchSize;
		this.firePropertyChanged(FETCH_SIZE_PROPERTY, old, fetchSize);
	}

	private Integer buildFetchSize(CompilationUnit astRoot) {
		return this.fetchSizeAdapter.getValue(astRoot);
	}
	//************************ timeout *********************************
	@Override
	public Integer getTimeout(){
		return this.timeout;
	}
	@Override
	public void setTimeout(Integer timeout){
		if (this.attributeValueHasChanged(this.timeout, timeout)) {
			this.timeout = timeout;
			this.timeoutAdapter.setValue(timeout);
		}
	}

	private void syncTimeout(Integer timeout) {
		Integer old = this.timeout;
		this.timeout = timeout;
		this.firePropertyChanged(TIMEOUT_PROPERTY, old, timeout);
	}

	private Integer buildTimeout(CompilationUnit astRoot) {
		return this.timeoutAdapter.getValue(astRoot);
	}
	//************************ comment *********************************
	@Override
	public String getComment(){
		return this.comment;
	}

	@Override
	public void setComment(String comment){
		if (this.attributeValueHasChanged(this.comment, comment)) {
			this.comment = comment;
			this.commentAdapter.setValue(comment);
		}
	}

	private void syncComment(String comment) {
		String old = this.comment;
		this.comment = comment;
		this.firePropertyChanged(COMMENT_PROPERTY, old, comment);
	}

	private String buildComment(CompilationUnit astRoot) {
		return this.commentAdapter.getValue(astRoot);
	}
	//************************ readOnly *********************************
	@Override
	public Boolean isReadOnly(){
		return this.readOnly;
	}

	@Override
	public void setReadOnly(Boolean readOnly){
		if (this.attributeValueHasChanged(this.readOnly, readOnly)) {
			this.readOnly = readOnly;
			this.readOnlyAdapter.setValue(readOnly);
		}
	}

	private void syncReadOnly(Boolean readOnly) {
		Boolean old = this.readOnly;
		this.readOnly = readOnly;
		this.firePropertyChanged(READ_ONLY_PROPERTY, old, readOnly);
	}

	private Boolean buildReadOnly(CompilationUnit astRoot) {
		return this.readOnlyAdapter.getValue(astRoot);
	}
	// ********** NestableAnnotation implementation **********
	@Override
	public boolean isUnset() {
		return super.isUnset() &&
				(this.name == null) &&
				(this.query == null) &&
				(this.flushMode == null) &&
				(this.cacheMode == null) &&
				(this.cacheable == null) &&
				(this.cacheRegion == null) &&
				(this.fetchSize == null) &&
				(this.comment == null) &&
				(this.readOnly == null);
	}


	/**
	 * convenience implementation of method from NestableAnnotation interface
	 * for subclasses
	 */
	@Override
	public void moveAnnotation(int newIndex) {
		this.getIndexedAnnotationAdapter().moveAnnotation(newIndex);
	}

	private IndexedAnnotationAdapter getIndexedAnnotationAdapter() {
		return (IndexedAnnotationAdapter) this.annotationAdapter;
	}

	public static HibernateNamedQueryAnnotation createNamedQuery(JavaResourceModel parent, AnnotatedElement annotatedElement, int index) {
		IndexedDeclarationAnnotationAdapter idaa = buildNamedQueryDeclarationAnnotationAdapter(index);
		IndexedAnnotationAdapter iaa = buildNamedQueryAnnotationAdapter(annotatedElement, idaa);
		return new HibernateSourceNamedQueryAnnotation(
			parent,
			annotatedElement,
			idaa,
			iaa);
	}

	protected DeclarationAnnotationElementAdapter<String> buildNameAdapter(DeclarationAnnotationAdapter daAdapter) {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(daAdapter, Hibernate.NAMED_QUERY__NAME);
	}

	protected DeclarationAnnotationElementAdapter<String> buildQueryAdapter(DeclarationAnnotationAdapter daAdapter) {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(daAdapter, Hibernate.NAMED_QUERY__QUERY);
	}

	protected DeclarationAnnotationElementAdapter<String> buildFlushModeAdapter(DeclarationAnnotationAdapter daAdapter) {
		return new EnumDeclarationAnnotationElementAdapter(daAdapter, Hibernate.NAMED_QUERY__FLUSH_MODE);
	}

	protected DeclarationAnnotationElementAdapter<String> buildCacheModeAdapter(DeclarationAnnotationAdapter daAdapter) {
		return new EnumDeclarationAnnotationElementAdapter(daAdapter, Hibernate.NAMED_QUERY__CACHE_MODE);
	}

	protected DeclarationAnnotationElementAdapter<Boolean> buildCacheableAdapter(DeclarationAnnotationAdapter daAdapter) {
		return ConversionDeclarationAnnotationElementAdapter.forBooleans(daAdapter, Hibernate.NAMED_QUERY__CACHEABLE);
	}

	protected DeclarationAnnotationElementAdapter<String> buildCacheRegionAdapter(DeclarationAnnotationAdapter daAdapter){
		return ConversionDeclarationAnnotationElementAdapter.forStrings(daAdapter, Hibernate.NAMED_QUERY__CACHE_REGION);
	}

	protected DeclarationAnnotationElementAdapter<Integer> buildFetchSizeAdapter(DeclarationAnnotationAdapter daAdapter){
		return ConversionDeclarationAnnotationElementAdapter.forNumbers(daAdapter, Hibernate.NAMED_QUERY__FETCH_SIZE);
	}

	protected DeclarationAnnotationElementAdapter<Integer> buildTimeoutAdapter(DeclarationAnnotationAdapter daAdapter){
		return ConversionDeclarationAnnotationElementAdapter.forNumbers(daAdapter, Hibernate.NAMED_QUERY__TIMEOUT);
	}

	protected DeclarationAnnotationElementAdapter<String> buildCommentAdapter(DeclarationAnnotationAdapter daAdapter){
		return ConversionDeclarationAnnotationElementAdapter.forStrings(daAdapter, Hibernate.NAMED_QUERY__COMMENT);
	}

	protected DeclarationAnnotationElementAdapter<Boolean> buildReadOnlyAdapter(DeclarationAnnotationAdapter daAdapter) {
		return ConversionDeclarationAnnotationElementAdapter.forBooleans(daAdapter, Hibernate.NAMED_QUERY__READ_ONLY);
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(this.name);
	}

	// ********** static methods **********
	protected static IndexedAnnotationAdapter buildNamedQueryAnnotationAdapter(AnnotatedElement annotatedElement, IndexedDeclarationAnnotationAdapter idaa) {
		return new ElementIndexedAnnotationAdapter(annotatedElement, idaa);
	}

	private static IndexedDeclarationAnnotationAdapter buildNamedQueryDeclarationAnnotationAdapter(int index) {
		IndexedDeclarationAnnotationAdapter idaa = 
			new CombinationIndexedDeclarationAnnotationAdapter(
				DECLARATION_ANNOTATION_ADAPTER,
				CONTAINER_DECLARATION_ANNOTATION_ADAPTER,
				index,
				ANNOTATION_NAME);
		return idaa;
	}

}
