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
package org.jboss.tools.hibernate.jpt.ui.internal.details.java;

import org.eclipse.jpt.common.core.JptCommonCorePlugin;
import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.internal.Tools;
import org.eclipse.jpt.jpa.core.JpaStructureNode;
import org.eclipse.jpt.jpa.core.context.java.JavaStructureNodes;
import org.eclipse.jpt.jpa.ui.details.JpaDetailsPage;
import org.eclipse.jpt.jpa.ui.details.JpaDetailsProvider;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernatePackageInfo;
import org.jboss.tools.hibernate.jpt.ui.internal.details.PackageInfoDetailsPage;

/**
 * @author Dmitry Geraskov
 *
 */
public class JavaPackageInfoDetailsProvider implements JpaDetailsProvider {

	// singleton
	private static final JpaDetailsProvider INSTANCE = new JavaPackageInfoDetailsProvider();
	
	
	/**
	 * Return the singleton
	 */
	public static JpaDetailsProvider instance() {
		return INSTANCE;
	}
	
	
	/**
	 * Enforce singleton usage
	 */
	private JavaPackageInfoDetailsProvider() {
		super();
	}
	
	
	public boolean providesDetails(JpaStructureNode structureNode) {
			return Tools.valuesAreEqual(structureNode.getId(), JavaStructureNodes.COMPILATION_UNIT_ID)
				&& structureNode.getResourceType().getContentType().equals(JptCommonCorePlugin.JAVA_SOURCE_PACKAGE_INFO_CONTENT_TYPE);
	}
	
	public JpaDetailsPage<HibernatePackageInfo> buildDetailsPage(
			Composite parent,
			WidgetFactory widgetFactory) {
		
		return new PackageInfoDetailsPage(parent, widgetFactory);
	}

}
