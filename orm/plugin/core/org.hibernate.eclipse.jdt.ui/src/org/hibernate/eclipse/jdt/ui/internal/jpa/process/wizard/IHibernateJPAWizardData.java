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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.ChangeStructure;

/**
 * Hibernate JPA wizard data for modification provide interface
 *
 * @author Vitali
 */
public interface IHibernateJPAWizardData {
	
	public Map<String, EntityInfo> getEntities();

	public ArrayList<ChangeStructure> getChanges();

	public IStructuredSelection getSelection2Update();
}
