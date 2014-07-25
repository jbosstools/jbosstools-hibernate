/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.mapper;

import org.hibernate.eclipse.mapper.extractor.HBMInfoExtractor;
import org.jboss.tools.hibernate.spi.IService;
import org.w3c.dom.Node;

/**
 * @author Vitali
 *
 */
public class HBMInfoExtractorStub extends HBMInfoExtractor {

	public HBMInfoExtractorStub(IService service) {
		super(service);
	}

	protected String packageName = null;
	
	protected String getPackageName(Node root) {
		return packageName;		
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;		
	}

}
