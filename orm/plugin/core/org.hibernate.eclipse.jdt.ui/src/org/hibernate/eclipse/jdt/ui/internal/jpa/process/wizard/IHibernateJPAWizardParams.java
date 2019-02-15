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

import java.util.ArrayList;
import java.util.Map;

import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AnnotStyle;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.ChangeStructure;

/**
 * Hibernate JPA wizard input parameters interface
 *
 * @author Vitali
 */
public interface IHibernateJPAWizardParams {
	/**
	 * @Column length - default value
	 */
	public final static int columnLength = 255;

	public AnnotStyle getAnnotationStyle();

	public void setAnnotationStyle(AnnotStyle annotationStyle);

	public boolean getEnableOptLock();

	public void setEnableOptLock(boolean enableOptLock);

	public int getDefaultStrLength();

	public void setDefaultStrLength(int defaultStrLength);

	public AnnotStyle getAnnotationStylePreference();

	public void reCollectModification(Map<String, EntityInfo> entities);
	
	public void performDisconnect();

	public ArrayList<ChangeStructure> getChanges();
}
