/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.basic;

/**
 * @author Dmitry Geraskov
 *
 */
public interface Hibernate {
	
	// Hibernate package
	String PACKAGE = "org.hibernate.annotations"; //$NON-NLS-1$
	String PACKAGE_ = PACKAGE + "."; //$NON-NLS-1$
	
	// ********** API **********

	// Hibernate annotations
	String GENERIC_GENERATOR = PACKAGE_ + "GenericGenerator"; //$NON-NLS-1$
		String GENERIC_GENERATOR__NAME = "name"; //$NON-NLS-1$
		String GENERIC_GENERATOR__STRATEGY = "strategy"; //$NON-NLS-1$
		String GENERIC_GENERATOR__PARAMETERS = "parameters"; //$NON-NLS-1$
		
	String GENERATOR_PARAMETER = PACKAGE_ + "Parameter"; //$NON-NLS-1$
		String GENERATOR_PARAMETER__NAME = "name"; //$NON-NLS-1$
		String GENERATOR_PARAMETER__VALUE = "value"; //$NON-NLS-1$
		
	String GENERIC_GENERATORS = PACKAGE_ + "GenericGenerators"; //$NON-NLS-1$
		String GENERIC_GENERATORS__VALUE = "value"; //$NON-NLS-1$
		
	String NAMED_QUERY = PACKAGE_ + "NamedQuery"; //$NON-NLS-1$
		String NAMED_QUERY__NAME = "name"; //$NON-NLS-1$
		String NAMED_QUERY__QUERY = "query"; //$NON-NLS-1$
		String NAMED_QUERY__HINTS = "hints"; //$NON-NLS-1$
		String NAMED_QUERY__FLUSH_MODE = "flushMode"; //$NON-NLS-1$
		String NAMED_QUERY__CACHE_MODE = "cacheMode"; //$NON-NLS-1$
		String NAMED_QUERY__CACHEABLE = "cacheable"; //$NON-NLS-1$
		String NAMED_QUERY__CACHE_REGION = "cacheRegion"; //$NON-NLS-1$
		String NAMED_QUERY__FETCH_SIZE = "fetchSize"; //$NON-NLS-1$
		String NAMED_QUERY__TIMEOUT = "timeout"; //$NON-NLS-1$
		String NAMED_QUERY__COMMENT = "comment"; //$NON-NLS-1$
		String NAMED_QUERY__READ_ONLY = "readOnly"; //$NON-NLS-1$
		
	String NAMED_QUERIES = PACKAGE_ + "NamedQueries"; //$NON-NLS-1$
		String NAMED_QUERIES__VALUE = "value"; //$NON-NLS-1$
		
	String NAMED_NATIVE_QUERY = PACKAGE_ + "NamedNativeQuery"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__NAME = "name"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__QUERY = "query"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__HINTS = "hints"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__FLUSH_MODE = "flushMode"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__CACHE_MODE = "cacheMode"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__CACHEABLE = "cacheable"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__CACHE_REGION = "cacheRegion"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__FETCH_SIZE = "fetchSize"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__TIMEOUT = "timeout"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__COMMENT = "comment"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__READ_ONLY = "readOnly"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__RESULT_CLASS = "resultClass"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__RESULT_SET_MAPPING = "resultSetMapping"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERY__CALLABLE = "callable"; //$NON-NLS-1$
	
	String NAMED_NATIVE_QUERIES = PACKAGE_ + "NamedNativeQueries"; //$NON-NLS-1$
		String NAMED_NATIVE_QUERIES__VALUE = "value"; //$NON-NLS-1$

	String FLUSH_MODE_TYPE = PACKAGE_ + "FlushModeType"; //$NON-NLS-1$
		String FLUSH_MODE_TYPE_ = FLUSH_MODE_TYPE + '.'; 
		String FLUSH_MODE_TYPE__ALWAYS = FLUSH_MODE_TYPE_ + "ALWAYS"; //$NON-NLS-1$
		String FLUSH_MODE_TYPE__AUTO = FLUSH_MODE_TYPE_ + "AUTO"; //$NON-NLS-1$
		String FLUSH_MODE_TYPE__COMMIT = FLUSH_MODE_TYPE_ + "COMMIT"; //$NON-NLS-1$
		String FLUSH_MODE_TYPE__NEVER = FLUSH_MODE_TYPE_ + "NEVER"; //$NON-NLS-1$
		String FLUSH_MODE_TYPE__MANUAL = FLUSH_MODE_TYPE_ + "MANUAL"; //$NON-NLS-1$
		
	String CACHE_MODE_TYPE = PACKAGE_ + "CacheModeType"; //$NON-NLS-1$
		String CACHE_MODE_TYPE_ = CACHE_MODE_TYPE + '.';
		String CACHE_MODE_TYPE__GET = CACHE_MODE_TYPE_ + "GET"; //$NON-NLS-1$
		String CACHE_MODE_TYPE__IGNORE = CACHE_MODE_TYPE_ + "IGNORE"; //$NON-NLS-1$
		String CACHE_MODE_TYPE__NORMAL = CACHE_MODE_TYPE_ + "NORMAL"; //$NON-NLS-1$
		String CACHE_MODE_TYPE__PUT = CACHE_MODE_TYPE_ + "PUT"; //$NON-NLS-1$
		String CACHE_MODE_TYPE__REFRESH = CACHE_MODE_TYPE_ + "REFRESH"; //$NON-NLS-1$

	String DISCRIMINATOR_FORMULA = PACKAGE_ + "DiscriminatorFormula"; //$NON-NLS-1$
		String DISCRIMINATOR_FORMULA__VALUE = "value"; //$NON-NLS-1$
		
	String GENERATED = PACKAGE_ + "Generated"; //$NON-NLS-1$
		String GENERATED__VALUE = "value"; //$NON-NLS-1$
	
}
