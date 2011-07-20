/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java.jpa2;

import org.eclipse.jpt.common.utility.internal.iterables.ArrayIterable;
import org.eclipse.jpt.common.utility.internal.iterables.CompositeIterable;
import org.eclipse.jpt.jpa.core.context.java.JavaConverter;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.jpa.core.context.java.JavaConverter.Adapter;
import org.eclipse.jpt.jpa.core.internal.jpa2.context.java.AbstractJavaElementCollectionMapping2_0;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaTypeConverter;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaElementCollectionMapping2_0 extends
		AbstractJavaElementCollectionMapping2_0 {
	
	protected static final Iterable<JavaConverter.Adapter> HIBERNATE_CONVERTER_ADAPTERS = 
			new CompositeIterable<JavaConverter.Adapter>(JavaTypeConverter.Adapter.instance(),
					new ArrayIterable<JavaConverter.Adapter>(CONVERTER_ADAPTER_ARRAY));
	
	public HibernateJavaElementCollectionMapping2_0(
			JavaPersistentAttribute parent) {
		super(parent);
	}

	@Override
	protected Iterable<Adapter> getConverterAdapters() {
		return HIBERNATE_CONVERTER_ADAPTERS;
	}

}
