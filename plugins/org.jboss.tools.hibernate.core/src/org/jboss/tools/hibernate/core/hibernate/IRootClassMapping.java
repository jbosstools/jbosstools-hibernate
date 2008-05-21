/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.core.hibernate;

/**
 * @author alex
 *
 */
public interface IRootClassMapping extends IHibernateClassMapping {
	public void setForceDiscriminator( boolean force);
	public void setDiscriminatorInsertable( boolean insert );
	public String getCacheConcurrencyStrategy();
	public void setCacheConcurrencyStrategy(String cacheConcurrencyStrategy);
	public String getCacheRegionName();
	public void setCacheRegionName(String cacheRegionName);
	
	//add tau 30.03.2006
	public boolean isDirtyCacheConcurrencyStrategy();
	public void setDirtyCacheConcurrencyStrategy(boolean dirtyCacheConcurrencyStrategy);
}