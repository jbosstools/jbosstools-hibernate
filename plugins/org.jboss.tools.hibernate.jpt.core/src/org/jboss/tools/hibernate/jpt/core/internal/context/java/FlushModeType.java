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

import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */

/**
 * Corresponds to the Hibernate enum
 * org.hibernate.annotations.FlushModeType
 * 
 * Provisional API: This interface is part of an interim API that is still
 * under development and expected to change significantly before reaching
 * stability. It is available at this early stage to solicit feedback from
 * pioneering adopters on the understanding that any code that uses this API
 * will almost certainly be broken (repeatedly) as the API evolves.
 */
public enum FlushModeType {

	ALWAYS(Hibernate.FLUSH_MODE_TYPE__ALWAYS),
	AUTO(Hibernate.FLUSH_MODE_TYPE__AUTO),
	COMMIT(Hibernate.FLUSH_MODE_TYPE__COMMIT),
	NEVER(Hibernate.FLUSH_MODE_TYPE__NEVER),
	MANUAL(Hibernate.FLUSH_MODE_TYPE__MANUAL);


	private String javaAnnotationValue;

	FlushModeType(String javaAnnotationValue) {
		if (javaAnnotationValue == null) {
			throw new NullPointerException();
		}
		this.javaAnnotationValue = javaAnnotationValue;
	}

	public String getJavaAnnotationValue() {
		return this.javaAnnotationValue;
	}


	// ********** static methods **********

	public static FlushModeType fromJavaAnnotationValue(Object javaAnnotationValue) {
		return (javaAnnotationValue == null) ? null : fromJavaAnnotationValue_(javaAnnotationValue);
	}

	private static FlushModeType fromJavaAnnotationValue_(Object javaAnnotationValue) {
		for (FlushModeType flushModeType : FlushModeType.values()) {
			if (flushModeType.getJavaAnnotationValue().equals(javaAnnotationValue)) {
				return flushModeType;
			}
		}
		return null;
	}

	public static String toJavaAnnotationValue(FlushModeType flushModeType) {
		return (flushModeType == null) ? null : flushModeType.getJavaAnnotationValue();
	}

}
