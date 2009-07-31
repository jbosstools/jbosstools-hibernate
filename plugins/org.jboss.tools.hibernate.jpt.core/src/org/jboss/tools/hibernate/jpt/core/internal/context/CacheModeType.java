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
package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */

/**
 * Corresponds to the Hibernate enum
 * org.hibernate.annotations.CacheModeType
 * 
 * Provisional API: This interface is part of an interim API that is still
 * under development and expected to change significantly before reaching
 * stability. It is available at this early stage to solicit feedback from
 * pioneering adopters on the understanding that any code that uses this API
 * will almost certainly be broken (repeatedly) as the API evolves.
 */
public enum CacheModeType {

	GET(Hibernate.CACHE_MODE_TYPE__GET),
	IGNORE(Hibernate.CACHE_MODE_TYPE__IGNORE),
	NORMAL(Hibernate.CACHE_MODE_TYPE__NORMAL),
	PUT(Hibernate.CACHE_MODE_TYPE__PUT),
	REFRESH(Hibernate.CACHE_MODE_TYPE__REFRESH);


	private String javaAnnotationValue;

	CacheModeType(String javaAnnotationValue) {
		if (javaAnnotationValue == null) {
			throw new NullPointerException();
		}
		this.javaAnnotationValue = javaAnnotationValue;
	}

	public String getJavaAnnotationValue() {
		return this.javaAnnotationValue;
	}


	// ********** static methods **********

	public static CacheModeType fromJavaAnnotationValue(Object javaAnnotationValue) {
		return (javaAnnotationValue == null) ? null : fromJavaAnnotationValue_(javaAnnotationValue);
	}

	private static CacheModeType fromJavaAnnotationValue_(Object javaAnnotationValue) {
		for (CacheModeType cacheModeType : CacheModeType.values()) {
			if (cacheModeType.getJavaAnnotationValue().equals(javaAnnotationValue)) {
				return cacheModeType;
			}
		}
		return null;
	}

	public static String toJavaAnnotationValue(CacheModeType cacheModeType) {
		return (cacheModeType == null) ? null : cacheModeType.getJavaAnnotationValue();
	}

}
