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
package org.jboss.tools.hibernate.orm.test;

import org.hibernate.eclipse.console.views.QueryPageViewer;
import org.junit.Assert;
import org.junit.Test;

public class QueryPageViewerTest {
	
	@Test
	public void testLabelProviderImpl() {
		
		QueryPageViewer.LabelProviderImpl labelProvider = 
			new QueryPageViewer.LabelProviderImpl();
		String res = labelProvider.getColumnText(null, 0);
		Assert.assertTrue("".equals(res)); //$NON-NLS-1$
		String[] arr = new String[1];
		final String testStr = "testVal"; //$NON-NLS-1$
		arr[0] = testStr;
		res = labelProvider.getColumnText(arr, 0);
		Assert.assertTrue(testStr.equals(res));

	}

}
