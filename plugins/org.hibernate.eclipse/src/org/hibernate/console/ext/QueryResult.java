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
package org.hibernate.console.ext;

import java.util.List;

/**
 * @author Dmitry Geraskov
 *
 */
public interface QueryResult {
	
	public List<Object> list();
	
	public List<String> getPathNames();
	
	public long getQueryTime();
	
	public boolean hasExceptions();
	
	public List<Throwable> getExceptions();

	public void setPathNames(List<String> pathNames);

}
