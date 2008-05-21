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
package org.jboss.tools.hibernate.wizard.hibernatecachewizard.datamodel;


public class ListDataModel 
{
    private String		 		Name;
	private String		 		CacheableParam;
	public ListDataModel(final String name)
	{
		setName(name);
	}
	
	public void setName(final String newname)
	{
		Name = newname;
	}
	public final String getName()
    {
        return Name;
    }

	public final String getCacheableParam() 
	{
		return CacheableParam;
	}

	public void setCacheableParam(final String cacheableParam) 
	{
		CacheableParam = cacheableParam;
	}
}
