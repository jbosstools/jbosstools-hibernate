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
package org.jboss.tools.hibernate.core;

import org.jboss.tools.hibernate.core.IAutoMappingService.Settings;

/**
 * @author kaa
 * akuzmin@exadel.com
 * Oct 12, 2005
 */
public interface IDAOGenerator {

	public void generateDAO(IPersistentClass[] classes, Settings settings,String packageName, boolean generateInterfaces,boolean isNeedLog,boolean generateTests);
}
