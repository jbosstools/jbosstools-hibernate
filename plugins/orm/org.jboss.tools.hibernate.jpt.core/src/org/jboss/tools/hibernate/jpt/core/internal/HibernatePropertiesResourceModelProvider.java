/*******************************************************************************
  * Copyright (c) 2012 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.JpaResourceModelProvider;

/**
 * 
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class HibernatePropertiesResourceModelProvider implements
		JpaResourceModelProvider {

	// singleton
		private static final JpaResourceModelProvider INSTANCE = new HibernatePropertiesResourceModelProvider();

		/**
		 * Return the singleton.
		 */
		public static JpaResourceModelProvider instance() {
			return INSTANCE;
		}

		/**
		 * Ensure single instance.
		 */
		private HibernatePropertiesResourceModelProvider() {
			super();
		}

		public IContentType getContentType() {
			return HibernateJptPlugin.JAVA_PROPERTIES_CONTENT_TYPE;
		}

		public HibernatePropertiesResourceModel buildResourceModel(
				JpaProject jpaProject, IFile file) {
			return new HibernatePropertiesResourceModel(jpaProject, file);
		}

}
