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
package org.hibernate.eclipse.jdt.ui.internal.jpa.process.wizard;

import java.util.Map;

import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AnnotStyle;

/**
 * Hibernate JPA wizard input parameters interface
 *
 * @author Vitali
 */
public interface IHibernateJPAWizardParams {

	public AnnotStyle getAnnotationStyle();

	public void setAnnotationStyle(AnnotStyle annotationStyle);

	public boolean getEnableOptLock();

	public void setEnableOptLock(boolean enableOptLock);

	public int getDefaultStrLength();

	public void setDefaultStrLength(int defaultStrLength);

	public AnnotStyle getAnnotationStylePreference();

	public void reCollectModification(ITextFileBufferManager bufferManager, 
			Map<String, EntityInfo> entities);
}
